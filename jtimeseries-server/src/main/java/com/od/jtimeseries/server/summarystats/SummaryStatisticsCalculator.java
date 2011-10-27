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

import com.od.jtimeseries.server.timeseries.FilesystemTimeSeries;
import com.od.jtimeseries.identifiable.QueryResult;
import com.od.jtimeseries.util.time.TimePeriod;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.util.numeric.Numeric;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.context.ContextProperties;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.text.DecimalFormat;
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

    public SummaryStatisticsCalculator(TimeSeriesContext rootContext, TimePeriod summaryRecalculationSleepTime, List<SummaryStatistic> statistics) {
        this.rootContext = rootContext;
        this.summaryRecalculationSleepTime = summaryRecalculationSleepTime;
        this.statistics = statistics;
    }

    public void start() {
        if ( statistics.size() > 0) {
            logMethods.logInfo("Starting summary statistics calculation every " + summaryRecalculationSleepTime);
            Thread t = new Thread(new SummaryStatisticsRecalculator());
            t.setDaemon(true);
            t.setName("SummaryStatsRecalculation");
            t.start();
        }
    }

    /**
     * Calculating summary stats for a series may require that series to be deserialized which is expensive
     * To avoid doing this for all series at once and causing a sudden spike in cpu and disk access, the strategy
     * here is to calculate a sleep time and gradually recalculate 
     * the stats for all the series over the period specified
     */
    private class SummaryStatisticsRecalculator implements Runnable {

        private final DecimalFormat doubleFormat = new DecimalFormat("#.####");

        public void run() {
            try {
                runSummaryStatsLoop();
            } catch ( Throwable t) {
                logMethods.logError("Summary stats thread died!");
            }
        }

        private void runSummaryStatsLoop() {
            while(true) {
                //if there are no series requiring recalc we still need to pause between runs or we'll max out cpu
                sleepFor(5000);

                QueryResult<FilesystemTimeSeries> r = rootContext.findAll(FilesystemTimeSeries.class);
                int numberOfSeries = r.getNumberOfMatches();
                doRecalculations(r, numberOfSeries);
            }
        }

        private void doRecalculations(QueryResult<FilesystemTimeSeries> r, int numberOfSeries) {
            long requiredSleepTime = summaryRecalculationSleepTime.getLengthInMillis();
            logMethods.logDebug("Summary statistics calculator sleep time is " + requiredSleepTime +
                ", it will take at least " + (numberOfSeries * requiredSleepTime / 1000) + " seconds to " +
                "recalculate all series stats for this run");

            for (FilesystemTimeSeries s : r.getAllMatches()) {
                if ( requiresRecalculation(s)) {
                    long startTime = System.currentTimeMillis();
                    recalculateStats(s);
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

        private void sleepFor(long requiredSleepTime) {
            try {
                Thread.sleep(requiredSleepTime);
            } catch (InterruptedException e) {
                logMethods.logError("Interrupted when sleeping for summary stats", e);
            }
        }

        private void recalculateStats(IdentifiableTimeSeries s) {
            //first remove all current summary properties, these may include legacy properties which are
            //no longer in server config - we are not going to recalculate those
            Properties p = s.getProperties();
            for ( Object o : p.keySet() ) {
                String key = (String)o;
                if ( ContextProperties.isSummaryStatsProperty(key)) {
                    s.removeProperty(key);
                }
            }

            for ( SummaryStatistic stat : statistics) {
                try {
                    Numeric n = stat.calculateSummaryStatistic(s);

                    //all stats will be doubles currently
                    String propertyName = ContextProperties.createSummaryStatsPropertyName(stat.getStatisticName(), ContextProperties.SummaryStatsDataType.DOUBLE);
                    double value = n.doubleValue();
                    //always use NaN as the String NaN representation
                    s.setProperty(propertyName, Double.isNaN(value) ? "NaN" : doubleFormat.format(value));
                } catch (Throwable t) {
                    logMethods.logError("Error calculating Summary Stat " + stat.getStatisticName() + " for series " + s.getPath(), t);
                }
            }
        }


        /**
         *  No need to recalculate if there hasn't been an update since the last recalculation
         */
        private boolean requiresRecalculation(FilesystemTimeSeries s) {
            boolean result = true;
            String lastRecalcValue = s.getProperty(ContextProperties.SUMMARY_STATS_LAST_UPDATE_TIMESTAMP_PROPERTY);
            if ( lastRecalcValue != null ) {
                long lastUpdateTimestamp = s.getLatestTimestamp();
                long lastRecalc = Long.valueOf(lastRecalcValue);
                result = lastUpdateTimestamp > lastRecalc;
            }
            return result;
        }
    }



}
