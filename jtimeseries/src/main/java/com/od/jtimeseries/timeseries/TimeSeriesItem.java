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
package com.od.jtimeseries.timeseries;

import com.od.jtimeseries.util.numeric.Numeric;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 31/01/11
 * Time: 11:07
 *
 * TimeSeries are made up of TimeSeriesItem, each TimeSeriesItem has a timestamp with one or more
 * associated values (Numeric)
 *
 * In general TimeSeriesItem are expected to be immutable by JTimeseries library. Changing a time series item
 * after adding it to a timeseries may result in unexpected behaviour.
 */
public interface TimeSeriesItem {

    long getTimestamp();

    Numeric getValue();

    double doubleValue();

    long longValue();

    /**
     * @return number of Numeric values which are available at this time point
     */
    int getValueCount();

    /**
     * @return value at valueIndex for this time point, null if valueIndex >= getValueCount()
     */
    Numeric getValue(int valueIndex);

}
