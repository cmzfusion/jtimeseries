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
package com.od.jtimeseries.timeseries.impl;

import com.od.jtimeseries.timeseries.TimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.timeseries.TimeSeriesListener;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 01-Mar-2009
 * Time: 18:22:35
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractDelegatingTimeSeries implements TimeSeries {

    private TimeSeries wrappedTimeSeries;
    private WrappedTimeSeriesEventHandler eventHandler = new WrappedTimeSeriesEventHandler(this);

    public AbstractDelegatingTimeSeries(TimeSeries wrappedTimeSeries) {
        this.wrappedTimeSeries = wrappedTimeSeries;
        wrappedTimeSeries.addTimeSeriesListener(eventHandler);        
    }

    public void addTimeSeriesListener(TimeSeriesListener l) {
        eventHandler.addTimeSeriesListener(l);
    }

    public void removeTimeSeriesListener(TimeSeriesListener l) {
        eventHandler.removeTimeSeriesListener(l);
    }

    public boolean append(TimeSeriesItem item) {
        return wrappedTimeSeries.append(item);
    }

    public boolean prepend(TimeSeriesItem item) {
        return wrappedTimeSeries.prepend(item);
    }

    public Collection<TimeSeriesItem> getSnapshot() {
        return wrappedTimeSeries.getSnapshot();
    }

    public TimeSeriesItem getLatestItem() {
        return wrappedTimeSeries.getLatestItem();
    }

    public long getLatestTimestamp() {
        return wrappedTimeSeries.getLatestTimestamp();
    }

    public TimeSeriesItem removeLatestItem() {
        return wrappedTimeSeries.removeLatestItem();
    }

    public TimeSeriesItem getEarliestItem() {
        return wrappedTimeSeries.getEarliestItem();
    }

    public long getEarliestTimestamp() {
        return wrappedTimeSeries.getEarliestTimestamp();
    }

    public TimeSeriesItem removeEarliestItem() {
        return wrappedTimeSeries.removeEarliestItem();
    }

    public boolean isEmpty() {
        return wrappedTimeSeries.isEmpty();
    }

    public int size() {
        return wrappedTimeSeries.size();
    }

    public void clear() {
        wrappedTimeSeries.clear();
    }

    public boolean remove(Object o) {
        return wrappedTimeSeries.remove(o);
    }

    public boolean add(TimeSeriesItem timeSeriesItem) {
        return wrappedTimeSeries.add(timeSeriesItem);
    }

    public boolean retainAll(Collection<?> c) {
        return wrappedTimeSeries.retainAll(c);
    }

    public boolean removeAll(Collection<?> c) {
        return wrappedTimeSeries.removeAll(c);
    }

    public boolean addAll(Collection<? extends TimeSeriesItem> c) {
        return wrappedTimeSeries.addAll(c);
    }

    public boolean containsAll(Collection<?> c) {
        return wrappedTimeSeries.containsAll(c);
    }

    public Object[] toArray() {
        return wrappedTimeSeries.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return wrappedTimeSeries.toArray(a);
    }

    public Iterator<TimeSeriesItem> iterator() {
        return wrappedTimeSeries.iterator();
    }

    public boolean contains(Object o) {
        return wrappedTimeSeries.contains(o);
    }

    public boolean equals(Object o) {
        return wrappedTimeSeries.equals(o);
    }

    public int hashCode() {
        return wrappedTimeSeries.hashCode();
    }

    public TimeSeries getSubSeries(long startTimestamp, long endTimestamp) {
        return wrappedTimeSeries.getSubSeries(startTimestamp, endTimestamp);
    }

    public TimeSeries getSubSeries(long timestamp) {
        return wrappedTimeSeries.getSubSeries(timestamp);
    }

    public TimeSeriesItem getFirstItemAtOrBefore(long timestamp) {
        return wrappedTimeSeries.getFirstItemAtOrBefore(timestamp);
    }

    public TimeSeriesItem getFirstItemAtOrAfter(long timestamp) {
        return wrappedTimeSeries.getFirstItemAtOrAfter(timestamp);
    }

    public long getTimestampAfter(long timestamp) {
        return wrappedTimeSeries.getTimestampAfter(timestamp);
    }

    public long getTimestampBefore(long timestamp) {
        return wrappedTimeSeries.getTimestampBefore(timestamp);
    }
}
