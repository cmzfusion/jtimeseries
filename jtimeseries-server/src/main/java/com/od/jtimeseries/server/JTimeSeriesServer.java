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
import javax.management.remote.JMXServiceURL;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.registry.LocateRegistry;
import java.lang.management.ManagementFactory;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 16-May-2009
 * Time: 15:32:19
 *
 */
public class JTimeSeriesServer {

    public static long startTime = System.currentTimeMillis();
    private static final LogMethods logMethods;
    private static final ApplicationContext ctx;

    private int serverAnnouncementPingPeriodSeconds = 30;
    private int jmxManagementPort;
    private TimeSeriesContext rootContext;
    private UdpClient udpClient;
    private RoundRobinSerializer fileSerializer;
    private HttpServerAnnouncementMessage serverAnnouncementMessage;
    private ServerConfigJmx serverConfigJmx;
    private UdpServer udpServer;
    private ServerMetricInitializer serverMetricInitializer;
    private HtmlAdaptorServer htmlAdaptorServer;
    private JTimeSeriesHttpd httpdServer;

    static {
        //set the hostname as a system property so that it is available on startup to the spring context property placeholder configurer
        System.setProperty("hostname", getHostname());

        //First read the logging configuration and set this up before other classes are loaded, since other classes in JTimeSeriesServer will
        //initialize their static loggers when they are first loaded, and the logging subsystem needs to be set up first.
        ApplicationContext loggingContext = new ClassPathXmlApplicationContext("logContext.xml");
        configureLogging(loggingContext);

        logMethods = LogUtils.getLogMethods(JTimeSeriesServer.class);
        logMethods.logInfo("Reading Spring Application Context");
        ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
    }

    public JTimeSeriesServer() {
    }

    private void startup() {
        logMethods.logInfo("Starting JTimeSeriesServer");

        startSeriesDirectoryManager();
        startJmxManagementServer();
        setupServerMetrics();
        addUdpMessageListeners();
        addHttpdShutdownHook();
        startServerAnnouncementPings();
        startJmx();

        //start scheduling for any series (e.g server metrics) which require it
        rootContext.startScheduling().startDataCapture();

        serverConfigJmx.setSecondsToStartServer((int)(System.currentTimeMillis() - startTime) / 1000);
        logMethods.logInfo("JTimeSeriesServer is up. Time taken to start was " + serverConfigJmx.getSecondsToStartServer() + " seconds");
    }

    private void startJmxManagementServer() {
        try {
            logMethods.logInfo("Starting JMX Management Service");
            LocateRegistry.createRegistry(jmxManagementPort);
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://:" + jmxManagementPort + "/jmxrmi");
            JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(url, null, server);
            cs.start();
        } catch (IOException e) {
            logMethods.logError("Error creating jmx server", e);
        }
    }

    private void startServerAnnouncementPings() {
        logMethods.logInfo("Starting Server Announcement Pings");
        udpClient.sendRepeatedMessage(serverAnnouncementMessage, Time.seconds(serverAnnouncementPingPeriodSeconds));
    }

    private void addHttpdShutdownHook() {
        //this handler enables the TimeSeriesServer to be shutdown from an http call
        //the plan is to add extra shutdown listeners to cleanly shut the server down (e.g. stop file writing and services first)
        ShutdownHandlerFactory shutdownFactory = new ShutdownHandlerFactory(rootContext, new ShutdownHandlerFactory.SystemExitShutdownListener());
        shutdownFactory.addShutdownListener(fileSerializer);
        httpdServer.setHandlerFactory(shutdownFactory);
    }

    private void addUdpMessageListeners() {
        logMethods.logInfo("Adding UDP message listeners");
        udpServer.addUdpMessageListener(new AppendToSeriesMessageListener(rootContext));
        udpServer.addUdpMessageListener(new ClientAnnouncementMessageListener(udpClient));
    }

    private void setupServerMetrics() {
        logMethods.logInfo("Setting up server metrics series");
        serverMetricInitializer.initializeServerMetrics();
    }

    private void startSeriesDirectoryManager() {
        logMethods.logInfo("Starting Series Directory Manager");
        SeriesDirectoryManager seriesDirectoryManager = (SeriesDirectoryManager)ctx.getBean("seriesDirectoryManager");
        seriesDirectoryManager.removeOldTimeseriesFiles();
        seriesDirectoryManager.loadExistingSeries();
    }

    private void startJmx() {
        logMethods.logInfo("Starting JMX Html Adapter Interface");
        try {
            MBeanServer mBeanServer = MBeanServerFactory.createMBeanServer();

            ObjectName configMBeanName = new ObjectName("JTimeSeriesServerConfig:name=JTimeSeriesServerConfig");
            mBeanServer.registerMBean(serverConfigJmx, configMBeanName);
            mBeanServer.registerMBean(htmlAdaptorServer, new ObjectName("adaptor:protocol=HTTP"));

            htmlAdaptorServer.start();
        } catch (Exception e) {
            logMethods.logError("Failed to start JMX interface", e);
        }
    }

    public void setServerAnnouncementPingPeriodSeconds(int serverAnnouncementPingPeriodSeconds) {
        this.serverAnnouncementPingPeriodSeconds = serverAnnouncementPingPeriodSeconds;
    }

    public void setRootContext(TimeSeriesContext rootContext) {
        this.rootContext = rootContext;
    }

    public void setUdpClient(UdpClient udpClient) {
        this.udpClient = udpClient;
    }

    public void setFileSerializer(RoundRobinSerializer roundRobinSerializer) {
        this.fileSerializer = roundRobinSerializer;
    }

    public void setServerAnnouncementMessage(HttpServerAnnouncementMessage announceMessage) {
        this.serverAnnouncementMessage = announceMessage;
    }

    public void setServerConfigJmx(ServerConfigJmx serverConfigJmx) {
        this.serverConfigJmx = serverConfigJmx;
    }

    public void setUdpServer(UdpServer udpServer) {
        this.udpServer = udpServer;
    }

    public void setServerMetricInitializer(ServerMetricInitializer metricInitializer) {
        this.serverMetricInitializer = metricInitializer;
    }

    public void setHtmlAdaptorServer(HtmlAdaptorServer htmlAdaptorServer) {
        this.htmlAdaptorServer = htmlAdaptorServer;
    }

    public void setHttpdServer(JTimeSeriesHttpd httpdServer) {
        this.httpdServer = httpdServer;
    }

    public void setJmxManagementPort(int jmxManagementPort) {
        this.jmxManagementPort = jmxManagementPort;
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

    public static void main(String[] args) throws IOException {
        JTimeSeriesServer server = (JTimeSeriesServer)ctx.getBean("timeSeriesServer");
        server.startup();
    }
}
