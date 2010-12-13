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

    public synchronized void addTimeSeriesListener(TimeSeriesListener l) {
        eventHandler.addTimeSeriesListener(l);
    }

    public synchronized void removeTimeSeriesListener(TimeSeriesListener l) {
        eventHandler.removeTimeSeriesListener(l);
    }

    public synchronized boolean append(TimeSeriesItem item) {
        return wrappedTimeSeries.append(item);
    }

    public synchronized boolean prepend(TimeSeriesItem item) {
        return wrappedTimeSeries.prepend(item);
    }

    public synchronized Collection<TimeSeriesItem> getSnapshot() {
        return wrappedTimeSeries.getSnapshot();
    }

    public synchronized TimeSeriesItem getLatestItem() {
        return wrappedTimeSeries.getLatestItem();
    }

    public synchronized long getLatestTimestamp() {
        return wrappedTimeSeries.getLatestTimestamp();
    }

    public synchronized TimeSeriesItem removeLatestItem() {
        return wrappedTimeSeries.removeLatestItem();
    }

    public synchronized TimeSeriesItem getEarliestItem() {
        return wrappedTimeSeries.getEarliestItem();
    }

    public synchronized long getEarliestTimestamp() {
        return wrappedTimeSeries.getEarliestTimestamp();
    }

    public synchronized TimeSeriesItem removeEarliestItem() {
        return wrappedTimeSeries.removeEarliestItem();
    }

    public synchronized boolean isEmpty() {
        return wrappedTimeSeries.isEmpty();
    }

    public synchronized int size() {
        return wrappedTimeSeries.size();
    }

    public synchronized void clear() {
        wrappedTimeSeries.clear();
    }

    public synchronized boolean remove(Object o) {
        return wrappedTimeSeries.remove(o);
    }

    public synchronized boolean add(TimeSeriesItem timeSeriesItem) {
        return wrappedTimeSeries.add(timeSeriesItem);
    }

    public synchronized boolean retainAll(Collection<?> c) {
        return wrappedTimeSeries.retainAll(c);
    }

    public synchronized boolean removeAll(Collection<?> c) {
        return wrappedTimeSeries.removeAll(c);
    }

    public synchronized boolean addAll(Collection<? extends TimeSeriesItem> c) {
        return wrappedTimeSeries.addAll(c);
    }

    public synchronized boolean containsAll(Collection<?> c) {
        return wrappedTimeSeries.containsAll(c);
    }

    public synchronized Object[] toArray() {
        return wrappedTimeSeries.toArray();
    }

    public synchronized <T> T[] toArray(T[] a) {
        return wrappedTimeSeries.toArray(a);
    }

    public synchronized Iterator<TimeSeriesItem> iterator() {
        return wrappedTimeSeries.iterator();
    }

    public synchronized boolean contains(Object o) {
        return wrappedTimeSeries.contains(o);
    }

    public synchronized TimeSeries getSubSeries(long startTimestamp, long endTimestamp) {
        return wrappedTimeSeries.getSubSeries(startTimestamp, endTimestamp);
    }

    public synchronized TimeSeries getSubSeries(long timestamp) {
        return wrappedTimeSeries.getSubSeries(timestamp);
    }

    public synchronized TimeSeriesItem getFirstItemAtOrBefore(long timestamp) {
        return wrappedTimeSeries.getFirstItemAtOrBefore(timestamp);
    }

    public synchronized TimeSeriesItem getFirstItemAtOrAfter(long timestamp) {
        return wrappedTimeSeries.getFirstItemAtOrAfter(timestamp);
    }

    public synchronized long getTimestampAfter(long timestamp) {
        return wrappedTimeSeries.getTimestampAfter(timestamp);
    }

    public synchronized long getTimestampBefore(long timestamp) {
        return wrappedTimeSeries.getTimestampBefore(timestamp);
    }

    @Override
    public boolean equals(Object o) {
        return wrappedTimeSeries.equals(o);
    }

    @Override
    public int hashCode() {
        return wrappedTimeSeries.hashCode();
    }
}
