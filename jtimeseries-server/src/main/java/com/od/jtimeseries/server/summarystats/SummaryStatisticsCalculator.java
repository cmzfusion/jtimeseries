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
package com.od.jtimeseries.server.summarystats;

import com.od.jtimeseries.util.identifiable.QueryResult;
import com.od.jtimeseries.util.time.TimePeriod;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.util.numeric.Numeric;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.context.ContextProperties;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;

import java.util.List;
import java.text.DecimalFormat;

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
    private TimePeriod refreshPeriod;

    public SummaryStatisticsCalculator(TimeSeriesContext rootContext, TimePeriod refreshPeriod, List<SummaryStatistic> statistics) {
        this.rootContext = rootContext;
        this.refreshPeriod = refreshPeriod;
        this.statistics = statistics;
    }

    public void start() {
        if ( statistics.size() > 0) {
            logMethods.logInfo("Starting summary statistics calculation every " + refreshPeriod);
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
                QueryResult<IdentifiableTimeSeries> r = rootContext.findAllTimeSeries();
                int numberOfSeries = r.getNumberOfMatches();

                if ( numberOfSeries == 0) {
                    sleepRecalcThread(refreshPeriod.getLengthInMillis());
                } else {
                    doRecalculations(r, numberOfSeries);
                }
            }
        }

        private void doRecalculations(QueryResult<IdentifiableTimeSeries> r, int numberOfSeries) {
            long requiredSleepTime = refreshPeriod.getLengthInMillis() / numberOfSeries;
            logMethods.logDebug("Summary statistics sleep time to caculate " + numberOfSeries + " series for this run will be " + requiredSleepTime);

            for (IdentifiableTimeSeries s : r.getAllMatches()) {
                if ( requiresRecalculation(s)) {
                    recalculateStats(s);
                }
                s.setProperty(ContextProperties.SUMMARY_STATS_LAST_UPDATE_PROPERTY, String.valueOf(System.currentTimeMillis()));

                sleepRecalcThread(requiredSleepTime);
            }
        }

        private void sleepRecalcThread(long requiredSleepTime) {
            try {
                Thread.sleep(requiredSleepTime);
            } catch (InterruptedException e) {
                logMethods.logError("Interrupted when sleeping for summary stats", e);
            }
        }

        private void recalculateStats(IdentifiableTimeSeries s) {
            for ( SummaryStatistic stat : statistics) {
                try {
                    Numeric n = stat.calculateSummaryStatistic(s);

                    //all stats will be doubles currently
                    String propertyName = ContextProperties.createSummaryStatsPropertyName(stat.getStatisticName(), ContextProperties.SummaryStatsDataType.DOUBLE);
                    s.setProperty(propertyName, doubleFormat.format(n.doubleValue()));
                } catch (Throwable t) {
                    logMethods.logError("Error calculating Summary Stat " + stat.getStatisticName() + " for series " + s.getPath(), t);
                }
            }
        }


        /**
         *  No need to recalculate if there hasn't been an update since the last recalculation
         */
        private boolean requiresRecalculation(IdentifiableTimeSeries s) {
            boolean result = true;
            String lastRecalcValue = s.getProperty(ContextProperties.SUMMARY_STATS_LAST_UPDATE_PROPERTY);
            if ( lastRecalcValue != null ) {
                long lastUpdateTimestamp = s.getLatestTimestamp();
                long lastRecalc = Long.valueOf(lastRecalcValue);
                result = lastUpdateTimestamp > lastRecalc;
            }
            return result;
        }
    }



}
