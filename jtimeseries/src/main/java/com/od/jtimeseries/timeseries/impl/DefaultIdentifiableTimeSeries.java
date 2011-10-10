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
import com.od.jtimeseries.util.identifiable.IdentifiableBase;

import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 06-Jan-2009
 * Time: 15:19:46
 *
 * A timeseries instance which implements the Identifiable interface
 *
 * This class is implemented as a wrapper around another TimeSeries delegate, to which it delegates all the timeseries
 * method calls, but provides the additional implementation required to support the Identifiable interface.
 *
 * The default implementation for the wrapped series is DefaultTimeSeries, but a RoundRobinTimeSeries could equally
 * well be used, to make a RoundRobinTimeSeries usable within an Identifiable context tree, for example.
 */
public class DefaultIdentifiableTimeSeries extends IdentifiableBase implements IdentifiableTimeSeries {

    private TimeSeries wrappedSeries;
    private ProxyTimeSeriesEventHandler eventHandler;

    public DefaultIdentifiableTimeSeries(String id, String description) {
        this(id, description, new DefaultTimeSeries());
    }

    public DefaultIdentifiableTimeSeries(String id, String description, TimeSeries wrappedSeries) {
        super(id, description);
        this.wrappedSeries = wrappedSeries;
    }

    /**
     * Set the proxy event handler instance which processes events from the wrapped timeseries
     * and forwards them on to any locally registered listeners with the event source updated
     */
    protected synchronized void setProxyEventHandler(ProxyTimeSeriesEventHandler l) {
        if ( eventHandler != null) {
            wrappedSeries.removeTimeSeriesListener(eventHandler);
        }
        //add as a weak reference listener, in general we don't want the
        //wrapped series to retain a strong reference to the wrapper.
        WeakReferenceTimeSeriesListener weakReferenceTimeSeriesListener = new
                WeakReferenceTimeSeriesListener(wrappedSeries, l);
        wrappedSeries.addTimeSeriesListener(weakReferenceTimeSeriesListener);
        eventHandler = l; //make sure we keep a strong reference to the real listener
    }

    public synchronized TimeSeriesItem getLatestItem() {
        return wrappedSeries.getLatestItem();
    }

    public synchronized TimeSeriesItem getEarliestItem() {
        return wrappedSeries.getEarliestItem();
    }

    public long getEarliestTimestamp() {
        return wrappedSeries.getEarliestTimestamp();
    }

    public long getLatestTimestamp() {
        return wrappedSeries.getLatestTimestamp();
    }

    public synchronized void addTimeSeriesListener(TimeSeriesListener l) {
        lazyAddEventHandler(l);
    }

    //lazily add a listener to the wrapped series
    //this is so the wrapped series implementation does not start to propagate events unless a listener is actually added to
    //the identifiable series which wraps it
    private void lazyAddEventHandler(TimeSeriesListener l) {
        if ( eventHandler == null) {
            eventHandler = new ProxyTimeSeriesEventHandler(this);
            setProxyEventHandler(eventHandler);
        }
        eventHandler.addTimeSeriesListener(l);
    }

    public synchronized void removeTimeSeriesListener(TimeSeriesListener l) {
        if (eventHandler != null) {
            eventHandler.removeTimeSeriesListener(l);
        }
    }

    public synchronized int size() {
        return wrappedSeries.size();
    }

    public synchronized Iterator<TimeSeriesItem> iterator() {
        return wrappedSeries.iterator();
    }

    public synchronized void addItem(TimeSeriesItem i) {
        wrappedSeries.addItem(i);
    }

    public synchronized boolean removeItem(TimeSeriesItem i) {
        return wrappedSeries.removeItem(i);
    }

    public void addAll(Iterable<TimeSeriesItem> items) {
        wrappedSeries.addAll(items);
    }

    public void removeAll(Iterable<TimeSeriesItem> items) {
        wrappedSeries.removeAll(items);
    }

    public synchronized void clear() {
        wrappedSeries.clear();
    }

    public TimeSeriesItem getItem(int index) {
        return wrappedSeries.getItem(index);
    }

    public synchronized long getModCount() {
        return wrappedSeries.getModCount();
    }

    public List<TimeSeriesItem> getSnapshot() {
        return wrappedSeries.getSnapshot();
    }

    public TimeSeriesItem getFirstItemAtOrBefore(long timestamp) {
        return wrappedSeries.getFirstItemAtOrBefore(timestamp);
    }

    public TimeSeriesItem getFirstItemAtOrAfter(long timestamp) {
        return wrappedSeries.getFirstItemAtOrAfter(timestamp);
    }

    public List<TimeSeriesItem> getItemsInRange(long startTime, long endTime) {
        return wrappedSeries.getItemsInRange(startTime, endTime);
    }

    /**
     * @return the wrappedSeries, which holds the data for this DefaultIdentifiableTimeSeries
     */
    public TimeSeries getDelegateTimeSeries() {
        return wrappedSeries;
    }

    public String toString() {
        return "TimeSeries " + getPath();
    }

}
