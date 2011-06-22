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
package com.od.jtimeseries.timeseries.interpolation;

import com.od.jtimeseries.timeseries.TimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.timeseries.function.interpolation.InterpolationFunction;
import com.od.jtimeseries.timeseries.impl.AbstractProxyTimeSeries;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 05-Mar-2009
 * Time: 23:24:59
 * To change this template use File | Settings | File Templates.
 */
public class DefaultInterpolatedTimeSeries extends AbstractProxyTimeSeries implements InterpolatedTimeSeries {

    private InterpolationFunction interpolationFunction;

    public DefaultInterpolatedTimeSeries(TimeSeries wrappedTimeSeries, InterpolationFunction interpolationFunction) {
        super(wrappedTimeSeries);
        this.interpolationFunction = interpolationFunction;
    }

    public TimeSeriesItem getInterpolatedValue(long timestamp) {
        TimeSeriesItem result = null;
        if ( timestamp >= getEarliestTimestamp() && timestamp <= getLatestTimestamp()) {
            result = calculateInterpolatedValue(timestamp);
        }
        return result;
    }

    private TimeSeriesItem calculateInterpolatedValue(long timestamp) {
        TimeSeriesItem result;
        TimeSeriesItem beforeItem = getFirstItemAtOrBefore(timestamp);
        if ( beforeItem.getTimestamp() == timestamp) {
            //although the timestamp matches an item in the series exactly, we still use interp function to return a value
            //this is because the function may generate a TimeSeriesItem of a different type - and we want the
            //TimeSeriesItem returned by calculateInterpolatedValue to be consistent in type
            result = interpolationFunction.calculateInterpolatedValue(this, timestamp, beforeItem, beforeItem);
        } else {
            TimeSeriesItem afterItem = getFirstItemAtOrAfter(timestamp);
            result = interpolationFunction.calculateInterpolatedValue(this, timestamp, beforeItem, afterItem);
        }
        return result;
    }
}
