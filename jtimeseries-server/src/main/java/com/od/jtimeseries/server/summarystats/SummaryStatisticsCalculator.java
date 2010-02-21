package com.od.jtimeseries.server.summarystats;

import com.od.jtimeseries.util.time.TimePeriod;
import com.od.jtimeseries.util.time.Time;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.util.numeric.Numeric;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.context.ContextQueries;
import com.od.jtimeseries.context.ContextProperties;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 21-Feb-2010
 * Time: 11:27:55
 * To change this template use File | Settings | File Templates.
 */
public class SummaryStatisticsCalculator {

    private static LogMethods logMethods = LogUtils.getLogMethods(SummaryStatisticsCalculator.class);

    private TimeSeriesContext rootContext;
    private List<SummaryStatistic> statistics;
    private int summaryStatsUpdateFrequencyMins;
    private TimePeriod refreshPeriod;

    public SummaryStatisticsCalculator(TimeSeriesContext rootContext, List<SummaryStatistic> statistics, int summaryStatsUpdateFrequencyMins) {
        this.rootContext = rootContext;
        this.statistics = statistics;
        this.summaryStatsUpdateFrequencyMins = summaryStatsUpdateFrequencyMins;
        refreshPeriod = Time.minutes(summaryStatsUpdateFrequencyMins);
    }

    public void start() {
        if ( summaryStatsUpdateFrequencyMins > -1 ) {
            logMethods.logInfo("Starting summary statistics calculation every " + refreshPeriod);
            Thread t = new Thread(new SummaryStatisticsRecalculator());
            t.setDaemon(true);
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

        public void run() {
            try {
                runSummaryStatsLoop();
            } catch ( Throwable t) {
                logMethods.logError("Summary stats thread died!");
            }
        }

        private void runSummaryStatsLoop() {
            while(true) {
                ContextQueries.QueryResult<IdentifiableTimeSeries> r = rootContext.findAllTimeSeries();
                int numberOfSeries = r.getNumberOfMatches();

                long requiredSleepTime = refreshPeriod.getLengthInMillis() / numberOfSeries;
                logMethods.logInfo("Summary statistics sleep time to caculate " + numberOfSeries + " for this run will be " + requiredSleepTime);

                for (IdentifiableTimeSeries s : r.getAllMatches()) {
                    if ( requiresRecalculation(s)) {
                        recalculateStats(s);
                    }
                    s.setProperty(ContextProperties.SUMMARY_STATS_LAST_UPDATE_PROPERTY, String.valueOf(System.currentTimeMillis()));

                    try {
                        Thread.sleep(requiredSleepTime);
                    } catch (InterruptedException e) {
                        logMethods.logError("Interrupted when sleeping for summary stats", e);
                    }
                }
            }
        }

        private void recalculateStats(IdentifiableTimeSeries s) {
            for ( SummaryStatistic stat : statistics) {
                try {
                    Numeric n = stat.calculateSummaryStatistic(s);

                    //all stats will be doubles currently
                    String propertyName = ContextProperties.getSummaryStatsPropertyName(stat.getStatisticName(), ContextProperties.SummaryStatsDataType.DOUBLE);
                    s.setProperty(propertyName, String.valueOf(n.doubleValue()));
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
