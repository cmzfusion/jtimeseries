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

import com.od.jtimeseries.timeseries.*;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.identifiable.IdentifiableBase;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 06-Jan-2009
 * Time: 15:19:46
 *
 * A timeseries instance which implements the Identifiable interface
 * TimeSeries functions are delegated to a wrapped TimeSeries implementation, which can be passed into the constructor
 */
public class DefaultIdentifiableTimeSeries extends IdentifiableBase implements IdentifiableTimeSeries, IndexedTimeSeries {

    private TimeSeries timeSeries;
    private ProxyTimeSeriesEventHandler eventHandler = new ProxyTimeSeriesEventHandler(this);
    public DefaultIdentifiableTimeSeries(String id, String description) {
        this(id, description, new DefaultTimeSeries());
    }

    public DefaultIdentifiableTimeSeries(String id, String description, TimeSeries timeSeries) {
        super(id, description);
        this.timeSeries = timeSeries;
        setProxyEventHandler(eventHandler);
    }

    //set an alternative handling for wrapped series events
    protected void setProxyEventHandler(ProxyTimeSeriesEventHandler l) {
        timeSeries.removeTimeSeriesListener(eventHandler);
        //add as a weak reference listener, in general we don't want the
        //wrapped series to retain a strong reference to the wrapper.
        WeakReferenceTimeSeriesListener weakReferenceTimeSeriesListener = new
                WeakReferenceTimeSeriesListener(timeSeries, l);
        timeSeries.addTimeSeriesListener(weakReferenceTimeSeriesListener);
        eventHandler = l;
    }

    public synchronized TimeSeriesItem getLatestItem() {
        return timeSeries.getLatestItem();
    }

    public synchronized TimeSeriesItem getEarliestItem() {
        return timeSeries.getEarliestItem();
    }

    public long getEarliestTimestamp() {
        return timeSeries.getEarliestTimestamp();
    }

    public long getLatestTimestamp() {
        return timeSeries.getLatestTimestamp();
    }

    public synchronized void addTimeSeriesListener(TimeSeriesListener l) {
        eventHandler.addTimeSeriesListener(l);
    }

    public synchronized void removeTimeSeriesListener(TimeSeriesListener l) {
        eventHandler.removeTimeSeriesListener(l);
    }

    public synchronized int size() {
        return timeSeries.size();
    }

    public synchronized Iterator<TimeSeriesItem> iterator() {
        return timeSeries.iterator();
    }

    public synchronized void addItem(TimeSeriesItem i) {
        timeSeries.addItem(i);
    }

    public synchronized boolean removeItem(TimeSeriesItem i) {
        return timeSeries.removeItem(i);
    }

    public void addAll(Iterable<TimeSeriesItem> items) {
        timeSeries.addAll(items);
    }

    public void removeAll(Iterable<TimeSeriesItem> items) {
        timeSeries.removeAll(items);
    }

    public synchronized void clear() {
        timeSeries.clear();
    }

    public synchronized long getModCount() {
        return timeSeries.getModCount();
    }

    public List<TimeSeriesItem> getSnapshot() {
        return timeSeries.getSnapshot();
    }

    public String toString() {
        return "TimeSeries " + getPath();
    }

    public TimeSeriesItem getItem(int index) {
        return timeSeries.getItem(index);
    }
}
