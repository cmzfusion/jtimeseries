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

import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.time.TimePeriod;

import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 22-Jun-2009
 * Time: 16:03:00
 * To change this template use File | Settings | File Templates.
 */
public interface TimeSeriesServerConfig {

    /**
     * @return port number on which to start http daemon
     */
    int getHttpdDaemonPort();

    /**
     * @return port number on which to listen for UDP messages
     */
    int getUdpServerPort();

    /**
     * @return port number for JMX interfaces
     */
    int getJmxHttpdPort();

    /**
     * @return path to timeseries storage directory
     */
    String getSeriesDirectoryPath();

    /**
     * @return suffix to identify timeseries files
     */
    String getTimeSeriesFileSuffix();

    /**
     * @return Frequency at which to send UDP pings to announce the server's availability
     */
    TimePeriod getPingPeriodSeconds();

    /**
     * @return a name identifying this server instance
     */
    String getServerName();

    /**
     * Set the length of time in seconds taken to start the server
     */
    void setSecondsToStartServer(int seconds);

    /**
     * @return Get the length of time taken to start the server
     */
    int getSecondsToStartServer();

    /**
     * Files above this count deleted on startup, oldest first
     * @return Max timeseries file count
     */
    int getMaxFileCount();

     /**
     * Files deleted on startup until this disk space is reached, oldest first
     * During run this size may currently be exceeded
     * @return Max permissable sum of timeseries file size
     */
    int getMaxDiskSpaceForTimeseriesMb();

    /**
     * Files which exceed this age deleted on startup, oldest first
     * @return  Max age for a timeseries file based on timestamp
     */
    int getMaxTimeseriesFileAgeDays();

    /**
     * @return time in ms to cache new series data for appends before writing to filesystem
     */
    TimePeriod getFileAppendDelayMillis();

    /**
     * The server will cache data for a series in memory for a short time if it changes before flushing to disk
     * @return time in ms to cache series data before flushing to disk once whole series has changed
     */
    TimePeriod getFileRewriteDelayMillis();

    /**
     * @return max number of series items in round robin timeseries
     */
    int getMaxItemsInTimeseries();

    /**
     * @return full path to the log file
     */
    String getLogFilePath();

    /**
     * @return Log file name
     */
    String getLogFileName();

    
    int getMaxLogFileSizeBytes();


    int getMaxLogFileCount();

    
    String getLogLevel();

    /**
     * @return a path for the context used to store the jtimeseries-server's own metrics
     */
    String getServerMetricsContextPath();


    void writeAllConfigPropertiesToLog(LogMethods logMethods);


    ScheduledExecutorService getServerMaintenanceExecutor();
}
