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

    public static SummaryStatistic MEDIAN() {
        return new DefaultSummaryStatistic("Median", AggregateFunctions.MEDIAN());
    }

    public static SummaryStatistic MEDIAN_TODAY() {
        return new TodayOnlySummaryStatistic("Median Today", AggregateFunctions.MEDIAN());            
    }

    public static SummaryStatistic PERCENTILE(int percentile) {
        return new DefaultSummaryStatistic(percentile + " Percentile", AggregateFunctions.PERCENTILE(percentile));
    }

    public static SummaryStatistic PERCENTILE_TODAY(int percentile) {
        return new TodayOnlySummaryStatistic(percentile + " Percentile Today", AggregateFunctions.PERCENTILE(percentile));
    }
}
