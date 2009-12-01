/**
 * Copyright (C) 2009 (nick @ objectdefinitions.com)
 *
 * This file is part of JTimeseries.
 *
 * JTimeseries is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JTimeseries is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JTimeseries.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.od.jtimeseries.server;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.net.httpd.JTimeSeriesHttpd;
import com.od.jtimeseries.net.udp.HttpServerAnnouncementMessage;
import com.od.jtimeseries.net.udp.UdpClient;
import com.od.jtimeseries.net.udp.UdpServer;
import com.od.jtimeseries.server.jmx.ServerConfigJmx;
import com.od.jtimeseries.server.message.AppendToSeriesMessageListener;
import com.od.jtimeseries.server.message.ClientAnnouncementMessageListener;
import com.od.jtimeseries.server.serialization.RoundRobinSerializer;
import com.od.jtimeseries.server.servermetrics.ServerMetricInitializer;
import com.od.jtimeseries.server.util.ShutdownHandlerFactory;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogMethodsFactory;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.util.time.Time;
import com.sun.jdmk.comm.HtmlAdaptorServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 16-May-2009
 * Time: 15:32:19
 *
 */
public class JTimeSeriesServer {

    private static final LogMethods logMethods;
    private static final ApplicationContext ctx;

    private int serverAnnouncementPingPeriodSeconds = 30;

    static {
        //set the hostname as a system property so that it is available on startup to the spring context property placeholder configurer
        System.setProperty("hostname", getHostname());

        //First read the logging configuration and set this up before other classes are loaded, since other classes in JTimeSeriesServer will
        //initialize their static loggers when they are first loaded, and the logging subsystem needs to be set up first.
        ApplicationContext loggingContext = new ClassPathXmlApplicationContext("logContext.xml");
        configureLogging(loggingContext);

        ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        logMethods = LogUtils.getLogMethods(JTimeSeriesServer.class);
    }

    public static void main(String[] args) throws IOException {
        JTimeSeriesServer server = (JTimeSeriesServer)ctx.getBean("timeSeriesServer");
        server.startup();
    }

    private void startup() throws IOException {
        long startTime = System.currentTimeMillis();
        //config.writeAllConfigPropertiesToLog(logMethods);
        logMethods.logInfo("Starting JTimeSeriesServer");

        RoundRobinSerializer roundRobinSerializer = (RoundRobinSerializer)ctx.getBean("fileSerializer");

        logMethods.logInfo("Starting Time Series Context");
        TimeSeriesContext rootContext = (TimeSeriesContext)ctx.getBean("rootContext");

        logMethods.logInfo("Starting Series Directory Manager");
        SeriesDirectoryManager seriesDirectoryManager = (SeriesDirectoryManager)ctx.getBean("seriesDirectoryManager");
        seriesDirectoryManager.removeOldTimeseriesFiles();
        seriesDirectoryManager.loadExistingSeries();

        logMethods.logInfo("Setting up server metrics series");
        ServerMetricInitializer s = (ServerMetricInitializer)ctx.getBean("serverMetricInitializer");
        s.initializeServerMetrics();

        UdpClient udpClient = (UdpClient) ctx.getBean("udpClient");
        UdpServer udpServer = (UdpServer)ctx.getBean("udpServer");

        udpServer.addUdpMessageListener(new AppendToSeriesMessageListener(rootContext));
        udpServer.addUdpMessageListener(new ClientAnnouncementMessageListener(udpClient));

        JTimeSeriesHttpd httpd = (JTimeSeriesHttpd)ctx.getBean("httpdServer");

        //this handler enables the TimeSeriesServer to be shutdown from an http call
        //the plan is to add extra shutdown listeners to cleanly shut the server down (e.g. stop file writing and services first)
        ShutdownHandlerFactory shutdownFactory = new ShutdownHandlerFactory(rootContext, new ShutdownHandlerFactory.SystemExitShutdownListener());
        shutdownFactory.addShutdownListener(roundRobinSerializer);
        httpd.setHandlerFactory(shutdownFactory);

        logMethods.logInfo("Starting Client Pings");
        HttpServerAnnouncementMessage announceMessage = (HttpServerAnnouncementMessage)ctx.getBean("serverAnnouncementMessage");
        udpClient.sendRepeatedMessage(announceMessage, Time.seconds(serverAnnouncementPingPeriodSeconds));

        logMethods.logInfo("Starting JMX Interface");
        ServerConfigJmx serverConfigJmx = (ServerConfigJmx)ctx.getBean("serverConfixJmx");
        startJmx(serverConfigJmx);

        //start scheduling for any series (e.g server metrics) which require it
        rootContext.startScheduling().startDataCapture();

        serverConfigJmx.setSecondsToStartServer((int)(System.currentTimeMillis() - startTime) / 1000);
        logMethods.logInfo("JTimeSeriesServer is up. Time taken to start was " + serverConfigJmx.getSecondsToStartServer() + " seconds");
    }

    private void startJmx(ServerConfigJmx serverConfigJmx) {
        try {
            MBeanServer mBeanServer = MBeanServerFactory.createMBeanServer();

            ObjectName configMBeanName = new ObjectName("JTimeSeriesServerConfig:name=JTimeSeriesServerConfig");
            mBeanServer.registerMBean(serverConfigJmx, configMBeanName);

            HtmlAdaptorServer htmlAdaptorServer = (HtmlAdaptorServer)ctx.getBean("htmlAdaptorServer");
            mBeanServer.registerMBean(htmlAdaptorServer, new ObjectName("adaptor:protocol=HTTP"));

            htmlAdaptorServer.start();
        } catch (Exception e) {
            logMethods.logError("Failed to start JMX interface", e);
        }
    }

    public void setServerAnnouncementPingPeriodSeconds(int serverAnnouncementPingPeriodSeconds) {
        this.serverAnnouncementPingPeriodSeconds = serverAnnouncementPingPeriodSeconds;
    }

    private static void configureLogging(ApplicationContext ctx) {
        LogMethodsFactory f = (LogMethodsFactory)ctx.getBean("logMethodsFactory", LogMethodsFactory.class);
        boolean logMethodsOk = f.isUsable();
        if ( logMethodsOk ) {
            LogUtils.setLogMethodFactory(f);
        } else {
            LogUtils.getLogMethods(JTimeSeriesServer.class).logInfo(
                    "Cannot write to directory for logfile path " + ((File)ctx.getBean("logFileDirectory")).getAbsolutePath() +
                    ". Will log to standard out"
            );
        }
    }

    private static String getHostname() {
        String result = "(Unknown Host)";
        try {
            result = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            logMethods.logError("Failed to find hostname", e);
        }
        return result;
    }

}
