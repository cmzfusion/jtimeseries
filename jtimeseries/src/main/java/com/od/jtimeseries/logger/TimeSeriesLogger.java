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
package com.od.jtimeseries.logger;

import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.StandardOutputLogMethods;
import com.od.jtimeseries.util.time.TimePeriod;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 05-Dec-2008
 * Time: 10:07:24
 */
public abstract class TimeSeriesLogger {

    private static volatile LogMethods defaultLogMethods = new StandardOutputLogMethods();

    private final List<IdentifiableTimeSeries> seriesList = new ArrayList<IdentifiableTimeSeries>();
    private LogMethods logMethods;
    private TimePeriod logPeriodMillis;
    private boolean isStarted;

    private static final Timer logTimer = new Timer("JTimeSeries Log Timer", true);

    public static void setDefaultLogMethods(LogMethods defaultLogMethods) {
        TimeSeriesLogger.defaultLogMethods = defaultLogMethods;
    }

    public TimeSeriesLogger(TimePeriod logPeriodMillis, IdentifiableTimeSeries... seriesList) {
        this(defaultLogMethods, logPeriodMillis, seriesList);
    }

    public TimeSeriesLogger(LogMethods logMethods, TimePeriod logPeriodMillis, IdentifiableTimeSeries... seriesList) {
        this.seriesList.addAll(Arrays.asList(seriesList));
        this.logMethods = logMethods;
        this.logPeriodMillis = logPeriodMillis;
    }

    public void addTimeSeries(IdentifiableTimeSeries... series) {
        synchronized(seriesList) {
            seriesList.addAll(Arrays.asList(series));
        }
    }

    public synchronized void startLogging() {
        if ( ! isStarted) {
            isStarted = true;
            TimerTask logTask = new TimerTask() {
                public void run() {
                    synchronized (seriesList) {
                        for ( IdentifiableTimeSeries series : seriesList) {
                            writeToLogs(series);
                        }
                    }
                }
            };
            schedule(logTask, logPeriodMillis.getLengthInMillis(), logPeriodMillis.getLengthInMillis());
        }
    }

    protected abstract void writeToLogs(IdentifiableTimeSeries series);

    protected void logInfo(String s) {
        logMethods.logInfo(s);
    }

    protected void logDebug(String s) {
        logMethods.logDebug(s);
    }

    protected void logError(String s) {
        logMethods.logError(s);
    }

    protected void logError(Throwable t, String s) {
        logMethods.logError(s, t);
    }

    private static void schedule(TimerTask t, long delay, long period) {
        logTimer.scheduleAtFixedRate(t, delay, period);
    }

}
