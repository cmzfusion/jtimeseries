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
package com.od.jtimeseries.server.util;

import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.time.Time;
import com.od.jtimeseries.util.time.TimePeriod;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 17-May-2009
 * Time: 18:45:46
 * To change this template use File | Settings | File Templates.
 */
public class DefaultServerConfig implements TimeSeriesServerConfig {

    private static final LogMethods logMethods = LogUtils.getLogMethods(DefaultServerConfig.class);
    private static final String DEFAULT_PROPERTY_FILE_PATH_PROPERTY = "PROPERTIES_FILE_PATH";
    private static final String DEFAULT_PROPERTY_FILE_PATH = "/jtimeseries-server.properties";

    private static final String HTTPD_PORT_PROPERTY = "HTTPD_PORT";
    private static final String DEFAULT_HTTPD_PORT = "18080";

    private static final String UDP_SERVER_PORT_PROPERTY = "UDP_SERVER_PORT";
    private static final String DEFAULT_UDP_SERVER_PORT = "18081";

    private static final String JMX_HTTPD_PORT_PROPERTY = "JMX_HTTPD_PORT";
    private static final String DEFAULT_JMX_HTTPD_PORT = "18082";

    private static final String SERIES_STORE_DIRECTORY_PATH_PROPERTY = "SERIES_DIRECTORY_PATH";
    private static final String DEFAULT_SERIES_STORE_DIRECTORY = System.getProperty("user.home") +
    System.getProperty("file.separator") +
    "jtimeseries-server";

    private static final String SERIES_FILE_SUFFIX_PROPERTY = "SERIES_FILE_SUFFIX";
    public static final String DEFAULT_TIMESERIES_SUFFIX = ".TIMESERIES";

    private static final String CLIENT_PING_PERIOD_SECONDS_PROPERTY = "PING_PERIOD_SECONDS";
    private static final String DEFAULT_CLIENT_PING_PERIOD_SECONDS = "30";

    private static final String SERVER_NAME_PROPERTY = "SERVER_NAME";
    private static final String DEFAULT_SERVER_NAME = "JTimeSeries Server " + getHostname();

    private static final String MAX_FILE_COUNT_PROPERTY = "MAX_SERIES_FILE_COUNT";
    private static final String DEFAULT_MAX_FILE_COUNT = "20000";

    private static final String MAX_SERIES_DISK_SPACE_MB_PROPERTY = "MAX_SERIES_DISK_SPACE_MB";
    private static final String DEFAULT_MAX_SERIES_DISK_SPACE_MB = "1000";

    private static final String MAX_TIMESERIES_FILE_AGE_DAYS_PROPERTY = "MAX_SERIES_AGE_DAYS";
    private static final String DEFUALT_MAX_SERIES_FILE_AGE_DAYS = "30";

    //file writes for appends can be cached for longer because we only hold the appended values in memory
    private static final String FILE_WRITE_DELAY_FOR_APPEND_PROPERTY = "FILE_WRITE_DELAY_FOR_TIMESERIES_APPEND";
    private static final String DEFAULT_WRITE_DELAY_FOR_APPEND = "300000";

    private static final String FILE_WRITE_DELAY_FOR_REWRITE_PROPERTY = "FILE_WRITE_DELAY_FOR_TIMESERIES_REWRITE";
    private static final String DEFAULT_WRITE_DELAY_FOR_REWRITE = "10000";

    private static final String MAX_ITEMS_IN_SERIES_PROPERTY = "MAX_ITEMS_IN_TIMESERIES_PROPERTY";
    private static final String DEFAULT_MAX_ITEMS_IN_SERIES = "10000";

    private static final String LOG_FILE_DIRECTORY_PATH_PROPERTY = "LOG_FILE_DIRECTORY_PATH";
    //private static final String DEFAULT_LOG_FILE_DIRECTORY_PATH = ""; //this will be set to the timeseries directory

    private static final String LOG_FILE_NAME_PROPERTY = "LOG_FILE_NAME";
    private static final String DEFAULT_LOG_FILE_NAME = "timeseries-server.log";

    private static final String MAX_LOG_FILE_SIZE_BYTES_PROPERTY = "MAX_LOG_FILE_SIZE_BYTES";
    private static final String DEFAULT_MAX_LOG_FILE_SIZE_BYTES = "1000000";

    private static final String MAX_LOG_FILE_COUNT_PROPERTY = "MAX_LOG_FILE_COUNT";
    private static final String DEFAULT_MAX_LOG_FILE_COUNT = "5";

    private static final String LOG_LEVEL_PROPERTY = "LOG_LEVEL";
    private static final String DEFAULT_LOG_LEVEL = "INFO";

    private static final String SERVER_METRICS_CONTEXT_PATH_PROPERTY = "SERVER_CONTEXT_PATH";
    private static final String DEFAULT_SERVER_METRICS_CONTEXT_PATH = "jtimeseries-server";

    private static int secondsToStartServer;

    private ScheduledExecutorService serverMaintenanceExecutor = Executors.newSingleThreadScheduledExecutor();

    //set the default properties when class loaded
    static {
        //set the path for properties file if not already set
        checkSetProperty(DEFAULT_PROPERTY_FILE_PATH_PROPERTY, DEFAULT_PROPERTY_FILE_PATH);

        //load in properties from props file
        loadPropertiesFromFile(System.getProperty(DEFAULT_PROPERTY_FILE_PATH_PROPERTY));

        //default any missing properties
        setDefaultProperties();
    }

    private static void setDefaultProperties() {
        checkSetProperty(HTTPD_PORT_PROPERTY, DEFAULT_HTTPD_PORT);
        checkSetProperty(UDP_SERVER_PORT_PROPERTY, DEFAULT_UDP_SERVER_PORT);
        checkSetProperty(JMX_HTTPD_PORT_PROPERTY, DEFAULT_JMX_HTTPD_PORT);
        checkSetProperty(SERIES_STORE_DIRECTORY_PATH_PROPERTY, DEFAULT_SERIES_STORE_DIRECTORY);
        checkSetProperty(SERIES_FILE_SUFFIX_PROPERTY, DEFAULT_TIMESERIES_SUFFIX);
        checkSetProperty(FILE_WRITE_DELAY_FOR_APPEND_PROPERTY, DEFAULT_WRITE_DELAY_FOR_APPEND);
        checkSetProperty(FILE_WRITE_DELAY_FOR_REWRITE_PROPERTY, DEFAULT_WRITE_DELAY_FOR_REWRITE);
        checkSetProperty(CLIENT_PING_PERIOD_SECONDS_PROPERTY, DEFAULT_CLIENT_PING_PERIOD_SECONDS);
        checkSetProperty(SERVER_NAME_PROPERTY, DEFAULT_SERVER_NAME);
        checkSetProperty(MAX_FILE_COUNT_PROPERTY, DEFAULT_MAX_FILE_COUNT);
        checkSetProperty(MAX_SERIES_DISK_SPACE_MB_PROPERTY, DEFAULT_MAX_SERIES_DISK_SPACE_MB);
        checkSetProperty(MAX_TIMESERIES_FILE_AGE_DAYS_PROPERTY, DEFUALT_MAX_SERIES_FILE_AGE_DAYS);
        checkSetProperty(FILE_WRITE_DELAY_FOR_APPEND_PROPERTY, DEFAULT_WRITE_DELAY_FOR_APPEND);
        checkSetProperty(FILE_WRITE_DELAY_FOR_REWRITE_PROPERTY, DEFAULT_WRITE_DELAY_FOR_REWRITE);
        checkSetProperty(MAX_ITEMS_IN_SERIES_PROPERTY, DEFAULT_MAX_ITEMS_IN_SERIES);

        //put the logs in the series directory by default (we check this exists on startup and abort if not)
        checkSetProperty(LOG_FILE_DIRECTORY_PATH_PROPERTY, System.getProperty(SERIES_STORE_DIRECTORY_PATH_PROPERTY));

        checkSetProperty(LOG_FILE_NAME_PROPERTY, DEFAULT_LOG_FILE_NAME);
        checkSetProperty(MAX_LOG_FILE_SIZE_BYTES_PROPERTY, DEFAULT_MAX_LOG_FILE_SIZE_BYTES);
        checkSetProperty(MAX_LOG_FILE_COUNT_PROPERTY, DEFAULT_MAX_LOG_FILE_COUNT);
        checkSetProperty(LOG_LEVEL_PROPERTY, DEFAULT_LOG_LEVEL);
        checkSetProperty(SERVER_METRICS_CONTEXT_PATH_PROPERTY, DEFAULT_SERVER_METRICS_CONTEXT_PATH);
    }

    private static void checkSetProperty(String property, String value) {
        if ( System.getProperty(property) == null) {
            System.setProperty(property, value);
        }
        logMethods.logInfo("Property " + property + " value is --> " + System.getProperty(property));
    }

    private static void loadPropertiesFromFile(String propertyFilePath) {
        InputStream propertyStream = DefaultServerConfig.class.getResourceAsStream(propertyFilePath);
        if (propertyStream != null) {
            try {
                System.getProperties().load(propertyStream);
            } catch (IOException e) {
                logMethods.logError("Failed to read properties file, will use default properties");
            }
        } else {
            logMethods.logInfo("Could not find properties file " + propertyFilePath + " in classpath to load properties, will use defaults");
        }
    }

    public int getHttpdDaemonPort() {
        return Integer.parseInt(System.getProperty(HTTPD_PORT_PROPERTY));
    }

    public int getUdpServerPort() {
        return Integer.parseInt(System.getProperty(UDP_SERVER_PORT_PROPERTY));
    }

    public int getJmxHttpdPort() {
        return Integer.parseInt(System.getProperty(JMX_HTTPD_PORT_PROPERTY));
    }

    public String getSeriesDirectoryPath() {
        return System.getProperty(SERIES_STORE_DIRECTORY_PATH_PROPERTY);
    }

    public String getTimeSeriesFileSuffix() {
        return System.getProperty(SERIES_FILE_SUFFIX_PROPERTY);
    }

    public TimePeriod getPingPeriodSeconds() {
        return Time.seconds(Integer.parseInt(System.getProperty(CLIENT_PING_PERIOD_SECONDS_PROPERTY)));
    }

    public String getServerName() {
        return System.getProperty(SERVER_NAME_PROPERTY);
    }

    public void setSecondsToStartServer(int seconds) {
        DefaultServerConfig.secondsToStartServer = seconds;
    }

    public int getSecondsToStartServer() {
        return secondsToStartServer;
    }

    public int getMaxFileCount() {
        return Integer.parseInt(System.getProperty(MAX_FILE_COUNT_PROPERTY));
    }

    public int getMaxDiskSpaceForTimeseriesMb() {
        return Integer.parseInt(System.getProperty(MAX_SERIES_DISK_SPACE_MB_PROPERTY));
    }

    public int getMaxTimeseriesFileAgeDays() {
        return Integer.parseInt(System.getProperty(MAX_TIMESERIES_FILE_AGE_DAYS_PROPERTY));
    }

    public TimePeriod getFileAppendDelayMillis() {
        return Time.milliseconds(Integer.parseInt(System.getProperty(FILE_WRITE_DELAY_FOR_APPEND_PROPERTY)));
    }

    public TimePeriod getFileRewriteDelayMillis() {
        return Time.milliseconds(Integer.parseInt(System.getProperty(FILE_WRITE_DELAY_FOR_REWRITE_PROPERTY)));
    }

    public int getMaxItemsInTimeseries() {
        return Integer.parseInt(System.getProperty(MAX_ITEMS_IN_SERIES_PROPERTY));
    }

    public String getLogFilePath() {
        return System.getProperty(LOG_FILE_DIRECTORY_PATH_PROPERTY);
    }

    public String getLogFileName() {
        return System.getProperty(LOG_FILE_NAME_PROPERTY);
    }

    public int getMaxLogFileSizeBytes() {
        return Integer.parseInt(System.getProperty(MAX_LOG_FILE_SIZE_BYTES_PROPERTY));
    }

    public int getMaxLogFileCount() {
        return Integer.parseInt(System.getProperty(MAX_LOG_FILE_COUNT_PROPERTY));
    }

    public String getLogLevel() {
        return System.getProperty(LOG_LEVEL_PROPERTY.toUpperCase());
    }

    public String getServerMetricsContextPath() {
        return System.getProperty(SERVER_METRICS_CONTEXT_PATH_PROPERTY);
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

    public void writeAllConfigPropertiesToLog(LogMethods logMethods) {
        logMethods.logInfo("Current config:");
        for ( Map.Entry entry : System.getProperties().entrySet()) {
            logMethods.logInfo(entry.getKey() + "=" + entry.getValue());
        }
    }

    public ScheduledExecutorService getServerMaintenanceExecutor() {
        return serverMaintenanceExecutor;
    }
}
