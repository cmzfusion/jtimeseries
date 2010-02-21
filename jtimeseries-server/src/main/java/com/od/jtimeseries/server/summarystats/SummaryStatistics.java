package com.od.jtimeseries.server.summarystats;

import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunctions;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 21-Feb-2010
 * Time: 13:02:55
 * To change this template use File | Settings | File Templates.
 */
public class SummaryStatistics {

    public static SummaryStatistic MEAN() {
        return new DefaultSummaryStatistic("Mean", AggregateFunctions.MEAN());
    }

    public static SummaryStatistic MEAN_TODAY() {
        return new TodayOnlySummaryStatistic("Mean Today", AggregateFunctions.MEAN());
    }

    public static SummaryStatistic MAX() {
        return new DefaultSummaryStatistic("Max", AggregateFunctions.MAX());
    }

    public static SummaryStatistic MAX_TODAY() {
        return new TodayOnlySummaryStatistic("Max Today", AggregateFunctions.MAX());
    }
}
