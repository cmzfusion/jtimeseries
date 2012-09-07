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
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunction;
import com.od.jtimeseries.util.numeric.Numeric;

import java.text.DecimalFormat;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 21-Feb-2010
 * Time: 10:37:18
 * To change this template use File | Settings | File Templates.
 */
public class DefaultSummaryStatistic implements SummaryStatistic {

    private final String summaryStatProperty;
    private final String name;
    private AggregateFunction function;

    private ThreadLocal<DecimalFormat> decimalFormat = new ThreadLocal<DecimalFormat>() {
        public DecimalFormat initialValue() {
            return  new DecimalFormat("#.####");
        }
    };

    public DefaultSummaryStatistic(String name, AggregateFunction function) {
        this.name = name;
        this.function = function;
        this.summaryStatProperty = ContextProperties.createSummaryStatsPropertyName(
            name,
            ContextProperties.SummaryStatsDataType.DOUBLE
        );
    }

    public void recalcSummaryStatistic(IdentifiableTimeSeries timeSeries) {
        function = function.nextInstance();  //support chaining functions
        long startTime = getStartTime();
        long endTime = getEndTime();
        try {
            timeSeries.readLock().lock();
            for ( TimeSeriesItem i : timeSeries.unsafeIterable()) {
                long timestamp = i.getTimestamp();
                if (timestamp >= startTime && timestamp <= endTime) {
                    function.addValue(i.getValue());
                }
            }
        } finally {
            timeSeries.readLock().unlock();
        }

        Numeric result = function.calculateResult();
        double d = result.doubleValue();

        //always use NaN as the String NaN representation
        timeSeries.setProperty(
            summaryStatProperty, Double.isNaN(d) ? "NaN" : decimalFormat.get().format(d)
        );
    }

    protected long getStartTime() {
        return Long.MIN_VALUE;
    }

    protected long getEndTime() {
        return Long.MAX_VALUE;
    }

    public String getStatisticName() {
        return name;
    }

    public boolean shouldDelete(long latestTimestampInSeries, long lastRecalcTimestamp) {
        return false;
    }

    public void deleteSummaryStatistic(IdentifiableTimeSeries series) {
        series.removeProperty(summaryStatProperty);
    }

    public String getSummaryStatProperty() {
        return summaryStatProperty;
    }

    public boolean shouldRecalc(long latestTimestampInSeries, long lastRecalcTimestamp) {
        return lastRecalcTimestamp < latestTimestampInSeries;
    }
}
