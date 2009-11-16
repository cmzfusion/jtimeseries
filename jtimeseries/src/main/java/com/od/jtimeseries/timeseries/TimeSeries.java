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

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 14-Nov-2009
 * Time: 16:15:57
 *
 * A TimeSeries is an ordered sequence of TimeSeriesItems, each representing a value at a point in time
 * The timepoints may or may not be equally spaced / periodic
 *
 * A TimeSeries guarantees an ordering by timestamp, so that during iteration items with an earlier timestamp
 * appear before items with a later timestamp. If an attempt is made to insert, prepend or append items out of
 * order this will result in a TimeSeriesOrderingException.
 *
 * This interface defines a set of common methods, but implementations may be based on varying datastructures,
 * and the choice of implementation may depend on the intended use. For example, a List implementation based
 * on an array may provide rapid random access to the items by index (but may have poor performance for inserting
 * or merging TimeSeriesItems). Alternatively a TimeSeries based on a map or tree may offer fast search for an
 * item with a given timestamp and fast merge, but poor random access.
 *
 * Depending on the implementation, a TimeSeries may or may not allow more than one item with the same timestamp.
 * (Due to the lack of granularity in a system clock, an attempt to add items with a duplicate timestamp may be
 * likely, and it is probably best to let an application/implementation decide how to handle this case).
 *
 * Note on thread safety:
 * 
 * TimeSeries implementations are in general intended to be thread safe (if not, this should be documented).
 * Furthermore, the guard used to guarantee thread safety should be the monitor/mutex of the time series instance
 * itself rather than that of an encapsulated private lock object. This is to enable client classes to safely
 * implement operations involving more than one method call by holding the lock on a series instance while the
 * operation takes place. In cases where the a timeseries implementation wraps another time series instance, client
 * classes in general should not change data of the wrapped instance directly - this should be done via the wrapper,
 * since making direct changes in this case would violate the guard provided by the wrapper instance's mutex/lock.
 */
public interface TimeSeries extends Collection<TimeSeriesItem> {

    /**
     * Prepend an item to the TimeSeries.
     * The item should have a timestamp earlier than or equal to the earliest items in the series
     * If there is already an item with an earlier timestamp, no action will be taken and this method will return false
     *
     * @return true, if the item was added
     */
    boolean prepend(TimeSeriesItem item);

    /**
     * Append an item to the TimeSeries.
     * The item should have a timestamp greater than or equal to the most recent item currently in the series
     * If there is already an item with a later timestamp, no action will be taken and this method will return false
     *
     * @return true, if the item was added
     */
    boolean append(TimeSeriesItem value);

    /**
     * @return A new Timeseries containing all items with timestamps greater than or equal to the supplied timestamp
     */
    TimeSeries getSubSeries(long timestamp);

    /**
     * @return A new TimeSeries containing all items between start and end timestamps, inclusive
     */
    TimeSeries getSubSeries(long startTimestamp, long endTimestamp);

    /**
     * @return the item in the series with the earliest timestamp value, or null if no items exist.
     */
    TimeSeriesItem getEarliestItem();

    /**
     * @return the item in the series with the latest timestamp value, or null if no items exist.
     */
    TimeSeriesItem getLatestItem();

    /**
     * @return  the item removed, or null if there were no items in this TimeSeries
     */
    TimeSeriesItem removeEarliestItem();

    /**
     * @return  the item removed, or null if there were no items in this TimeSeries
     */
    TimeSeriesItem removeLatestItem();

    /**
     * @return the earliest timestamp associated with a item in the series, or -1 if there are no items in the series.
     */
    long getEarliestTimestamp();

    /**
     * @return the latest timestamp associated with an item in the series, or -1 if there are no items in the series.
     */
    long getLatestTimestamp();

    /**
     * @return the first item in the series with a timestamp equal to or earlier than the supplied timestamp, or null if no such item exists
     */
    TimeSeriesItem getFirstItemAtOrBefore(long timestamp);

    /**
     * @return  first item in the series with a timestamp equal to or later than the supplied timestamp, or null if no such item exists
     */
    TimeSeriesItem getFirstItemAtOrAfter(long timestamp);

    /**
     * @return  first timestamp after the supplied timestamp for which there is an item in the series, or -1 if there is no item with a later timestamp
     */
    long getTimestampAfter(long timestamp);

    /**
     * @return  first timestamp before the supplied timestamp for which there is an item in the series, or -1 if there is no item with an earlier timestamp
     */
    long getTimestampBefore(long timestamp);

    /**
     * note, time series events are fired asynchronously on an event notification thread
     * @param l add a time series listener to receive change events from the time series
     */
    void addTimeSeriesListener(TimeSeriesListener l);

    /**
     * @param l remove a time series listener
     */
    void removeTimeSeriesListener(TimeSeriesListener l);


    Collection<TimeSeriesItem> getSnapshot();
}
