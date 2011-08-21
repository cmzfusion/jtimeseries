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
package com.od.jtimeseries.timeseries.impl;

import com.od.jtimeseries.timeseries.TimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.timeseries.TimeSeriesListener;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 01-Mar-2009
 * Time: 18:22:35
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractProxyTimeSeries implements TimeSeries {

    private TimeSeries wrappedTimeSeries;
    private ProxyTimeSeriesEventHandler eventHandler = new ProxyTimeSeriesEventHandler(this);

    public AbstractProxyTimeSeries(TimeSeries wrappedTimeSeries) {
        this.wrappedTimeSeries = wrappedTimeSeries;
        wrappedTimeSeries.addTimeSeriesListener(eventHandler);        
    }

    public synchronized void addTimeSeriesListener(TimeSeriesListener l) {
        eventHandler.addTimeSeriesListener(l);
    }

    public synchronized void removeTimeSeriesListener(TimeSeriesListener l) {
        eventHandler.removeTimeSeriesListener(l);
    }

    public synchronized TimeSeriesItem getLatestItem() {
        return wrappedTimeSeries.getLatestItem();
    }

    public synchronized TimeSeriesItem getEarliestItem() {
        return wrappedTimeSeries.getEarliestItem();
    }

    public long getEarliestTimestamp() {
        return wrappedTimeSeries.getEarliestTimestamp();
    }

    public long getLatestTimestamp() {
        return wrappedTimeSeries.getLatestTimestamp();
    }

    public void addItem(TimeSeriesItem timeSeriesItem) {
        wrappedTimeSeries.addItem(timeSeriesItem);
    }

    public boolean removeItem(TimeSeriesItem timeSeriesItem) {
        return wrappedTimeSeries.removeItem(timeSeriesItem);
    }

    public void addAll(Iterable<TimeSeriesItem> items) {
        wrappedTimeSeries.addAll(items);
    }

    public void removeAll(Iterable<TimeSeriesItem> items) {
        wrappedTimeSeries.removeAll(items);
    }

    public synchronized int size() {
        return wrappedTimeSeries.size();
    }

    public synchronized void clear() {
        wrappedTimeSeries.clear();
    }

    public synchronized Iterator<TimeSeriesItem> iterator() {
        return wrappedTimeSeries.iterator();
    }

    public synchronized long getModCount() {
        return wrappedTimeSeries.getModCount();
    }

    public List<TimeSeriesItem> getSnapshot() {
        return wrappedTimeSeries.getSnapshot();
    }

    public TimeSeriesItem getFirstItemAtOrAfter(long timestamp) {
        return wrappedTimeSeries.getFirstItemAtOrAfter(timestamp);
    }

    public TimeSeriesItem getFirstItemAtOrBefore(long timestamp) {
        return wrappedTimeSeries.getFirstItemAtOrBefore(timestamp);
    }

    public List<TimeSeriesItem> getItemsInRange(long startTime, long endTime) {
        return wrappedTimeSeries.getItemsInRange(startTime, endTime);
    }
}
