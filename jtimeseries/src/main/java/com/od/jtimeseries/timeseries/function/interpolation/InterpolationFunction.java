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

import com.od.jtimeseries.timeseries.TimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesItem;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 05-Mar-2009
 * Time: 23:34:40
 * To change this template use File | Settings | File Templates.
 */
public interface InterpolationFunction {

    /**
     * Calculate an interpolated value for supplied timestamp
     * Note, if the timestamp exactly matches an item in the TimeSeries, beforeItem and afterItem will be the same
     * In this case, interpolation may not be necessary, but the function may still return a TimeSeriesItem of a
     * different type
     */
    TimeSeriesItem calculateInterpolatedValue(
            TimeSeries defaultInterpolatedTimeSeries,
            long timestamp,
            TimeSeriesItem beforeItem,
            TimeSeriesItem afterItem);
}
