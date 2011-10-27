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

import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.timeseries.TimeSeriesListener;
import com.od.jtimeseries.util.identifiable.IdentifiableBase;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 06-Jan-2009
 * Time: 15:19:46
 *
 * A timeseries instance which implements the Identifiable interface
 *
 * This class is implemented as a wrapper around another TimeSeries delegate, to which it delegates all the Timeseries
 * interface method calls, but provides the additional implementation required to support the Identifiable interface.
 *
 * The default implementation for the wrapped series is DefaultTimeSeries, but a RoundRobinTimeSeries could equally
 * well be used, to make a RoundRobinTimeSeries usable within an Identifiable tree, for example.
 *
 * DefaultIdentifiableTimeSeries shares the ReadWriteLock of its wrapped TimeSeries
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
    protected void setProxyEventHandler(ProxyTimeSeriesEventHandler l) {
        try {
            this.writeLock().lock();
            if (eventHandler != null) {
                wrappedSeries.removeTimeSeriesListener(eventHandler);
            }
            //add as a weak reference listener, in general we don't want the
            //wrapped series to retain a strong reference to the wrapper.
            WeakReferenceTimeSeriesListener weakReferenceTimeSeriesListener = new
                    WeakReferenceTimeSeriesListener(wrappedSeries, l);
            wrappedSeries.addTimeSeriesListener(weakReferenceTimeSeriesListener);
            eventHandler = l; //make sure we keep a strong reference to the real listener
        } finally {
            this.writeLock().unlock();
        }

    }

    public TimeSeriesItem getLatestItem() {
        return wrappedSeries.getLatestItem();
    }

    public TimeSeriesItem getEarliestItem() {
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

    public void removeTimeSeriesListener(TimeSeriesListener l) {
        try {
            this.writeLock().lock();
            if (eventHandler != null) {
                eventHandler.removeTimeSeriesListener(l);
            }
        } finally {
            this.writeLock().unlock();
        }

    }

    public int size() {
        return wrappedSeries.size();
    }

    public Iterator<TimeSeriesItem> iterator() {
        return wrappedSeries.iterator();
    }

    public Iterable<TimeSeriesItem> unsafeIterable() {
        return wrappedSeries.unsafeIterable();
    }

    public void addItem(TimeSeriesItem i) {
        wrappedSeries.addItem(i);
    }

    public boolean removeItem(TimeSeriesItem i) {
        return wrappedSeries.removeItem(i);
    }

    public void addAll(Iterable<TimeSeriesItem> items) {
        wrappedSeries.addAll(items);
    }

    public void removeAll(Iterable<TimeSeriesItem> items) {
        wrappedSeries.removeAll(items);
    }

    public void clear() {
        wrappedSeries.clear();
    }

    public TimeSeriesItem getItem(int index) {
        return wrappedSeries.getItem(index);
    }

    public long getModCount() {
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

    public Lock readLock() {
        return wrappedSeries.readLock();
    }

    public Lock writeLock() {
        return wrappedSeries.writeLock();
    }

    public String toString() {
        return "TimeSeries " + getPath();
    }

}
