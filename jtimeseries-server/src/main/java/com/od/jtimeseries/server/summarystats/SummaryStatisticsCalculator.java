/**
 * Copyright (C) 2011 (nick @ objectdefinitions.com)
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
package com.od.jtimeseries.server.summarystats;

import com.od.jtimeseries.context.ContextProperties;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.identifiable.QueryResult;
import com.od.jtimeseries.server.timeseries.FilesystemTimeSeries;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.util.numeric.Numeric;
import com.od.jtimeseries.util.time.TimePeriod;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 21-Feb-2010
 * Time: 11:27:55
 * To change this template use File | Settings | File Templates.
 */
public class SummaryStatisticsCalculator {

    private static final LogMethods logMethods = LogUtils.getLogMethods(SummaryStatisticsCalculator.class);

    private TimeSeriesContext rootContext;
    private List<SummaryStatistic> statistics;
    private TimePeriod summaryRecalculationSleepTime;

    private static ThreadLocal<SimpleDateFormat> simpleDateFormat = new ThreadLocal<SimpleDateFormat>() {
        public SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss, z");
        }
    };

    public SummaryStatisticsRecalculator recalculator;
    private volatile boolean running;

    public SummaryStatisticsCalculator(TimeSeriesContext rootContext, TimePeriod summaryRecalculationSleepTime, List<SummaryStatistic> statistics) {
        this.rootContext = rootContext;
        this.summaryRecalculationSleepTime = summaryRecalculationSleepTime;
        this.statistics = statistics;
    }

    public synchronized void start() {
        if ( ! running && statistics.size() > 0) {
            logMethods.info("Starting summary statistics calculation every " + summaryRecalculationSleepTime);
            recalculator = new SummaryStatisticsRecalculator();
            Thread t = new Thread(recalculator);
            t.setDaemon(true);
            t.setName("SummaryStatsRecalculation");
            t.start();
            running = true;
        }
    }

    public synchronized void stop() {
        if ( running ) {
            recalculator.stop();
            running = false;
        }
    }

    /**
     * Calculating summary stats for a series may require that series to be deserialized which is expensive
     * To avoid doing this for all series at once and causing a sudden spike in cpu and disk access, the strategy
     * here is to calculate a sleep time and gradually recalculate 
     * the stats for all the series over the period specified
     */
    private class SummaryStatisticsRecalculator implements Runnable {

        private volatile boolean stopping;

        public void run() {
            try {
                runSummaryStatsLoop();
            } catch ( Throwable t) {
                logMethods.error("Summary stats thread died!");
            }
        }

        public void stop() {
            this.stopping = true;
        }

        private void runSummaryStatsLoop() {
            while(! stopping) {
                //if there are no series requiring recalc we still need to pause between runs or we'll max out cpu
                sleepFor(100);

                QueryResult<FilesystemTimeSeries> r = rootContext.findAll(FilesystemTimeSeries.class);
                int numberOfSeries = r.getNumberOfMatches();
                doRecalculations(r, numberOfSeries);
            }
        }

        private void doRecalculations(QueryResult<FilesystemTimeSeries> r, int numberOfSeries) {
            long requiredSleepTime = summaryRecalculationSleepTime.getLengthInMillis();
            if (logMethods.isDebugEnabled()) logMethods.debug("Summary statistics calculator sleep time is " + requiredSleepTime +
                    ", it will take at least " + (numberOfSeries * requiredSleepTime / 1000) + " seconds to " +
                    "recalculate all series stats for this run");

            for (FilesystemTimeSeries s : r.getAllMatches()) {

                if ( stopping ) {
                    break;
                }

                long startTime = System.currentTimeMillis();

                //remove any stats properties no longer configured
                boolean legacyStatsRemoved = removeLegacyStats(s);

                String lastRecalcValue = s.getProperty(ContextProperties.SUMMARY_STATS_LAST_UPDATE_TIMESTAMP_PROPERTY);
                long lastRecalcTimestamp = lastRecalcValue == null ? -1 : Long.valueOf(lastRecalcValue);
                long latestTimestamp = s.getLatestTimestamp();

                //check whether any stats need updating or deletion
                boolean requiresUpdateOrDelete = statsRequireUpdateOrDelete(s, latestTimestamp, lastRecalcTimestamp);

                if ( legacyStatsRemoved || requiresUpdateOrDelete ) {

                    recalculateStats(s, latestTimestamp, lastRecalcTimestamp);
                    Date d = new Date();
                    s.setProperty(ContextProperties.SUMMARY_STATS_LAST_UPDATE_TIMESTAMP_PROPERTY, String.valueOf(d.getTime()));
                    s.setProperty(ContextProperties.SUMMARY_STATS_LAST_UPDATE_TIME_PROPERTY, simpleDateFormat.get().format(d));
                    s.queueHeaderRewrite();

                    long timeTaken = System.currentTimeMillis() - startTime;
                    if ( timeTaken < requiredSleepTime) {
                        sleepFor(requiredSleepTime - timeTaken);
                    }
                }
            }
        }

        /**
         * @return true, if a stat was removed since no longer configured
         */
        private boolean removeLegacyStats(FilesystemTimeSeries s) {
            //first remove all summary properties which are no longer supported
            HashSet<String> stats = new HashSet<String>();
            Properties p = s.getProperties();
            for ( Object o : p.keySet() ) {
                String key = (String)o;
                if ( ContextProperties.isSummaryStatsProperty(key)) {
                    stats.add(key);
                }
            }

            for ( SummaryStatistic stat : statistics) {
                stats.remove(stat.getSummaryStatProperty());
            }
            stats.remove(ContextProperties.SUMMARY_STATS_LAST_UPDATE_TIMESTAMP_PROPERTY);
            stats.remove(ContextProperties.SUMMARY_STATS_LAST_UPDATE_TIME_PROPERTY);

            for ( String key : stats) {
                s.removeProperty(key);
            }
            return stats.size() > 0;
        }

        private void sleepFor(long requiredSleepTime) {
            try {
                Thread.sleep(requiredSleepTime);
            } catch (InterruptedException e) {
                logMethods.error("Interrupted when sleeping for summary stats", e);
            }
        }

        private void recalculateStats(IdentifiableTimeSeries s, long lastUpdateTimestamp, long lastRecalcTimestamp) {
            for ( SummaryStatistic stat : statistics) {
                try {
                    if ( stat.shouldDelete(lastUpdateTimestamp, lastRecalcTimestamp)) {
                        stat.deleteSummaryStatistic(s);
                    } else if ( stat.shouldRecalc(lastUpdateTimestamp, lastRecalcTimestamp)) {
                       stat.recalcSummaryStatistic(s);
                    }

                } catch (Throwable t) {
                    logMethods.error("Error calculating Summary Stat " + stat.getStatisticName() + " for series " + s.getPath(), t);
                }
            }
        }

        private boolean statsRequireUpdateOrDelete(IdentifiableTimeSeries s, long lastUpdateTimestamp, long lastRecalcTimestamp) {
            boolean result = false;
            for ( SummaryStatistic stat : statistics) {
                //not passing the series as a parameter here because otherwise implementation of shouldRecalc / shouldDelete
                //could trigger deserialization, the point here is to eliminate unnecessary deserializations based on the time
                //properties of the series and stats
                if ( stat.shouldRecalc(lastUpdateTimestamp, lastRecalcTimestamp) ||
                     s.getProperty(stat.getSummaryStatProperty()) != null && stat.shouldDelete(lastUpdateTimestamp, lastRecalcTimestamp) ) {
                    result = true;
                    break;
                }
            }
            return result;
        }

//        private String getSummaryStatProperty(SummaryStatistic stat) {
//            return ContextProperties.createSummaryStatsPropertyName(stat.getStatisticName(), ContextProperties.SummaryStatsDataType.DOUBLE);
//        }
    }

}
