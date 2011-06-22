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

import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.util.numeric.Numeric;
import com.od.jtimeseries.timeseries.TimeSeries;
import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunction;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 21-Feb-2010
 * Time: 10:37:18
 * To change this template use File | Settings | File Templates.
 */
public class DefaultSummaryStatistic implements SummaryStatistic {

    private String name;
    private final AggregateFunction function;

    public DefaultSummaryStatistic(String name, AggregateFunction function) {
        this.name = name;
        this.function = function;
    }

    public Numeric calculateSummaryStatistic(TimeSeries timeSeries) {
        AggregateFunction f = function.newInstance();  //not supporting chaining functions
        long startTime = getStartTime();
        long endTime = getEndTime();
        synchronized (timeSeries) {
            for (TimeSeriesItem i : timeSeries) {
                long timestamp = i.getTimestamp();
                if ( timestamp >= startTime && timestamp <= endTime) {
                    f.addValue(i.getValue());
                }
            }
        }
        return f.calculateAggregateValue();
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
}
