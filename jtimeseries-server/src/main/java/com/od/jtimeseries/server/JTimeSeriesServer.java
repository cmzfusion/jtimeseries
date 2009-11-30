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

import com.od.jtimeseries.JTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesFactory;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.net.httpd.JTimeSeriesHttpd;
import com.od.jtimeseries.net.httpd.NanoHTTPD;
import com.od.jtimeseries.net.udp.AnnouncementMessage;
import com.od.jtimeseries.net.udp.HttpServerAnnouncementMessage;
import com.od.jtimeseries.net.udp.UdpClient;
import com.od.jtimeseries.net.udp.UdpServer;
import com.od.jtimeseries.server.jmx.ServerConfigJmx;
import com.od.jtimeseries.server.serialization.RoundRobinSerializer;
import com.od.jtimeseries.server.timeseries.FilesystemTimeSeriesFactory;
import com.od.jtimeseries.server.util.DefaultServerConfig;
import com.od.jtimeseries.server.util.ShutdownHandlerFactory;
import com.od.jtimeseries.server.util.TimeSeriesServerConfig;
import com.od.jtimeseries.server.servermetrics.ServerMetricInitializer;
import com.od.jtimeseries.server.message.AppendToSeriesMessageListener;
import com.od.jtimeseries.server.message.ClientAnnouncementMessageListener;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogMethodsFactory;
import com.sun.jdmk.comm.HtmlAdaptorServer;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import java.io.File;
import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 16-May-2009
 * Time: 15:32:19
 * To change this template use File | Settings | File Templates.
 */
public class JTimeSeriesServer {

    private static TimeSeriesServerConfig config;
    private static LogMethods logMethods;

    static {
        //First read the logging configuration and set this up before other classes are loaded, since other classes in JTimeSeriesServer will
        //initialize their static loggers when they are first loaded, and the logging subsystem needs to be set up first.
        ApplicationContext ctx = new ClassPathXmlApplicationContext("logContext.xml");
        configureLogging(ctx);
        logMethods = LogUtils.getLogMethods(JTimeSeriesServer.class);

        //Load the properties for this server instance
        config = new DefaultServerConfig();
    }

    private UdpClient udpClient;

    public JTimeSeriesServer() throws IOException {
        startup();
    }

    private void startup() throws IOException {
        long startTime = System.currentTimeMillis();
        config.writeAllConfigPropertiesToLog(logMethods);
        logMethods.logInfo("Starting JTimeSeriesServer");

        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

        RoundRobinSerializer roundRobinSerializer = (RoundRobinSerializer)ctx.getBean("fileSerializer");

        logMethods.logInfo("Starting Time Series Context");
        TimeSeriesContext rootContext = (TimeSeriesContext)ctx.getBean("rootContext");

        logMethods.logInfo("Starting Series Directory Manager");
        SeriesDirectoryManager seriesDirectoryManager = (SeriesDirectoryManager)ctx.getBean("seriesDirectoryManager");
        seriesDirectoryManager.removeOldTimeseriesFiles();
        seriesDirectoryManager.loadExistingSeries();

        logMethods.logInfo("Setting up server metrics series");
        ServerMetricInitializer s = new ServerMetricInitializer(config, rootContext, roundRobinSerializer);
        s.initializeServerMetrics();

        udpClient = (UdpClient)ctx.getBean("udpClient");
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
        AnnouncementMessage announceMessage = new HttpServerAnnouncementMessage(
                config.getHttpdDaemonPort(),
                config.getServerName()
        );
        udpClient.sendRepeatedMessage(announceMessage, config.getPingPeriodSeconds());

        logMethods.logInfo("Starting JMX Interface");
        startJmx();

        //start scheduling for any series (e.g server metrics) which require it
        rootContext.startScheduling().startDataCapture();

        config.setSecondsToStartServer((int)(System.currentTimeMillis() - startTime) / 1000);
        logMethods.logInfo("JTimeSeriesServer is up. Time taken to start was " + config.getSecondsToStartServer() + " seconds");
    }

    private void startJmx() {
        try {
            MBeanServer mBeanServer = MBeanServerFactory.createMBeanServer();

            ObjectName configMBeanName = new ObjectName("JTimeSeriesServerConfig:name=JTimeSeriesServerConfig");
            mBeanServer.registerMBean(new ServerConfigJmx(config, udpClient), configMBeanName);

            HtmlAdaptorServer htmlAdaptorServer = new HtmlAdaptorServer(config.getJmxHttpdPort());
            mBeanServer.registerMBean(htmlAdaptorServer, new ObjectName("adaptor:protocol=HTTP"));

            htmlAdaptorServer.start();
        } catch (Exception e) {
            logMethods.logError("Failed to start JMX interface", e);
        }
    }

    public static void main(String[] args) throws IOException {
        new JTimeSeriesServer();
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

}
