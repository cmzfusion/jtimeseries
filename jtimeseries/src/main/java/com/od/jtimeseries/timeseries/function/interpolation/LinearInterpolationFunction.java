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
package com.od.jtimeseries.timeseries.function.interpolation;

import com.od.jtimeseries.timeseries.DefaultTimeSeriesItem;
import com.od.jtimeseries.timeseries.TimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.util.numeric.DoubleNumeric;
import com.od.jtimeseries.util.numeric.Numeric;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 05-Mar-2009
 * Time: 23:53:16
 * To change this template use File | Settings | File Templates.
 *
 * A Linear Interpolation for values in a TimeSeries
 */
public class LinearInterpolationFunction implements InterpolationFunction {

    public TimeSeriesItem calculateInterpolatedValue(TimeSeries timeSeries, long timestamp, TimeSeriesItem beforeItem, TimeSeriesItem afterItem) {
        long x = timestamp;
        long x0 = beforeItem.getTimestamp();
        long x1 = afterItem.getTimestamp();
        double y0 = beforeItem.getValue().doubleValue();
        double y1 = afterItem.getValue().doubleValue();
        double y = y0 + (x - x0) * (y1 - y0 ) / (x1 - x0);
        return y0 == y1 ? createTimeSeriesItem(x, DoubleNumeric.valueOf(y0)) : createTimeSeriesItem(x, DoubleNumeric.valueOf(y));
    }

    protected TimeSeriesItem createTimeSeriesItem(long timestamp, Numeric value) {
        return new DefaultTimeSeriesItem(timestamp, value);
    }
}
