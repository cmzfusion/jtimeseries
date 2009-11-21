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
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.net.httpd.JTimeSeriesHttpd;
import com.od.jtimeseries.net.udp.AnnouncementMessage;
import com.od.jtimeseries.net.udp.HttpServerAnnouncementMessage;
import com.od.jtimeseries.net.udp.UdpClient;
import com.od.jtimeseries.net.udp.UdpServer;
import com.od.jtimeseries.server.jmx.ServerConfigJmx;
import com.od.jtimeseries.server.serialization.RoundRobinSerializer;
import com.od.jtimeseries.server.timeseries.FilesystemTimeSeriesFactory;
import com.od.jtimeseries.server.util.JavaUtilLoggingLogMethodsFactory;
import com.od.jtimeseries.server.util.ServerProperties;
import com.od.jtimeseries.server.util.ShutdownHandlerFactory;
import com.od.jtimeseries.server.util.TimeSeriesServerConfig;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.util.logging.LogMethods;
import com.sun.jdmk.comm.HtmlAdaptorServer;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 16-May-2009
 * Time: 15:32:19
 * To change this template use File | Settings | File Templates.
 */
public class JTimeSeriesServer {

    private static TimeSeriesServerConfig config;

    static {
        //Load the properties for this server instance
        config = new ServerProperties();
        configureLogging();
    }

    //this log methods needs to be non-static so that we can configure the logging statically before it's created
    private LogMethods logMethods = LogUtils.getLogMethods(JTimeSeriesServer.class);
    private File seriesDirectory;
    private UdpClient udpClient;

    public JTimeSeriesServer() throws IOException {
        startup();
    }

    private void startup() throws IOException {
        long startTime = System.currentTimeMillis();
        config.writeAllConfigPropertiesToLog(logMethods);
        logMethods.logInfo("Starting JTimeSeriesServer");

        checkAndSetSeriesDirectory();
        RoundRobinSerializer roundRobinSerializer = new RoundRobinSerializer(seriesDirectory, config.getTimeSeriesFileSuffix());

        logMethods.logInfo("Starting Time Series Context");
        TimeSeriesContext rootContext = JTimeSeries.createRootContext("TimeSeriesServer");
        rootContext.setTimeSeriesFactory(new FilesystemTimeSeriesFactory(roundRobinSerializer, config.getFileAppendDelayMillis(), config.getFileRewriteDelayMillis(), config.getMaxItemsInTimeseries()));

        logMethods.logInfo("Starting Series Directory Manager");
        SeriesDirectoryManager seriesDirectoryManager = new SeriesDirectoryManager(seriesDirectory, roundRobinSerializer, rootContext, config);
        seriesDirectoryManager.removeOldTimeseriesFiles();
        seriesDirectoryManager.loadExistingSeries();

        logMethods.logInfo("Creating UDP client");
        udpClient = new UdpClient();

        logMethods.logInfo("Starting UDP Server");
        UdpServer udpServer = new UdpServer(config.getUdpServerPort());
        udpServer.addUdpMessageListener(new TimeSeriesCreatingMessageListener(rootContext));
        udpServer.addUdpMessageListener(new ClientAnnouncementMessageListener(udpClient));

        logMethods.logInfo("Starting TimeSeries HTTPD Daemon");
        JTimeSeriesHttpd httpd = new JTimeSeriesHttpd(config.getHttpdDaemonPort(), rootContext);
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

    private void checkAndSetSeriesDirectory() {
        seriesDirectory = new File(config.getSeriesDirectoryPath());
        if (! seriesDirectory.exists()) {
            throw new RuntimeException("Cannot start, series directory " + config.getSeriesDirectoryPath() + " does not exist");
        }
    }


    public static void main(String[] args) throws IOException {
        new JTimeSeriesServer();
    }

    private static void configureLogging() {
        File f = new File(config.getLogFilePath());
        if ( f.isDirectory() && f.canWrite()) {

            File logFile = new File(f, config.getLogFileName());

            //set the Logging up to use java util logging with log file handler
            LogUtils.setLogMethodFactory(
                new JavaUtilLoggingLogMethodsFactory(
                    logFile, config.getLogLevel(), config.getMaxLogFileSizeBytes(), config.getMaxLogFileCount()
                )
            );
        } else {
            LogUtils.getLogMethods(JTimeSeriesServer.class).logInfo(
                    "Cannot write to directory for logfile path " + f.getAbsolutePath() +
                    ". Will log to standard out"
            );
        }
    }

}
