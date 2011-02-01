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
package com.od.jtimeseries.timeseries;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 06-Jan-2009
 * Time: 15:17:29
 *
 * A TimeSeries which also implements the List interface
 * Does not support null elements in the List.
 *
 * ListTimeSeries may allow consecutive items to have the same timestamp, although this is implementation specific.
 * This is quite likely to occur in practise since it is very easy to capture several values to a series in the same
 * millisecond, and a system clock may not have sub-millisecond granularity, making duplicate timestamp capture
 * likely.
 */
public interface ListTimeSeries extends List<TimeSeriesItem>, TimeSeries {

    /**
     * This method returns a snapshot of this series
     * The snapshot is effectively a shallow copy of this TimeSeries which shares the TimeSeriesValue instances
     * @return a List of the TimeSeriesItem in this TimeSeries
     */
    List<TimeSeriesItem> getSnapshot();

    /**
     * @return A new Timeseries containing all items with timestamps greater than or equal to the supplied timestamp
     */
    ListTimeSeries getSubSeries(long timestamp);

    /**
     * @return A new TimeSeries containing all items between start and end timestamps, inclusive
     */
    ListTimeSeries getSubSeries(long startTimestamp, long endTimestamp);

    /**
     * @return  index of the first item in the series with a timestamp equal to or earlier than the supplied timestamp, or -1 if there is no such item
     */
    int getIndexOfFirstItemAtOrBefore(long timestamp);

    /**
     * @return  index of the first item in the series with a timestamp equal to or later than the supplied timestamp, or -1 if there is no such item
     */
    int getIndexOfFirstItemAtOrAfter(long timestamp);

}
