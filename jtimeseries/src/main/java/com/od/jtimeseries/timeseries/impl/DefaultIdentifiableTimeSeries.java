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

import com.od.jtimeseries.timeseries.*;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.identifiable.IdentifiableBase;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 06-Jan-2009
 * Time: 15:19:46
 *
 * A timeseries instance which implements the Identifiable interface
 * TimeSeries functions are delegated to a wrapped TimeSeries implementation, which can be passed into the constructor
 */
public class DefaultIdentifiableTimeSeries extends IdentifiableBase implements IdentifiableTimeSeries {

    private TimeSeries timeSeries;
    private ProxyTimeSeriesEventHandler eventHandler = new ProxyTimeSeriesEventHandler(this);

    public DefaultIdentifiableTimeSeries(String id, String description) {
        this(id, description, new DefaultTimeSeries());
    }

    public DefaultIdentifiableTimeSeries(Identifiable parent, String id, String description, TimeSeries timeSeries) {
        this(id, description, timeSeries);
        setParent(parent);
    }

    public DefaultIdentifiableTimeSeries(String id, String description, TimeSeries timeSeries) {
        super(id, description);
        this.timeSeries = timeSeries;
        timeSeries.addTimeSeriesListener(eventHandler);
    }

    //set an alternative handling for wrapped series events
    protected void setProxyEventHandler(ProxyTimeSeriesEventHandler l) {
        timeSeries.removeTimeSeriesListener(eventHandler);
        timeSeries.addTimeSeriesListener(l);
        eventHandler = l;
    }

    public synchronized boolean prepend(TimeSeriesItem item) {
        return timeSeries.prepend(item);
    }

    public synchronized boolean append(TimeSeriesItem value) {
        return timeSeries.append(value);
    }

    public synchronized Collection<TimeSeriesItem> getSnapshot() {
        return timeSeries.getSnapshot();
    }

    public synchronized TimeSeries getSubSeries(long timestamp) {
        return timeSeries.getSubSeries(timestamp);
    }

    public synchronized TimeSeries getSubSeries(long startTimestamp, long endTimestamp) {
        return timeSeries.getSubSeries(startTimestamp, endTimestamp);
    }

    public synchronized TimeSeriesItem getLatestItem() {
        return timeSeries.getLatestItem();
    }

    public synchronized TimeSeriesItem removeLatestItem() {
        return timeSeries.removeLatestItem();
    }

    public synchronized long getLatestTimestamp() {
        return timeSeries.getLatestTimestamp();
    }

    public synchronized TimeSeriesItem getEarliestItem() {
        return timeSeries.getEarliestItem();
    }

    public synchronized TimeSeriesItem removeEarliestItem() {
        return timeSeries.removeEarliestItem();
    }

    public synchronized long getEarliestTimestamp() {
        return timeSeries.getEarliestTimestamp();
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

    public synchronized boolean isEmpty() {
        return timeSeries.isEmpty();
    }

    public synchronized boolean contains(Object o) {
        return timeSeries.contains(o);
    }

    public synchronized Iterator<TimeSeriesItem> iterator() {
        return timeSeries.iterator();
    }

    public synchronized Object[] toArray() {
        return timeSeries.toArray();
    }

    public synchronized <T> T[] toArray(T[] a) {
        return timeSeries.toArray(a);
    }

    public synchronized boolean add(TimeSeriesItem o) {
        return timeSeries.add(o);
    }

    public synchronized boolean remove(Object o) {
        return timeSeries.remove(o);
    }

    public synchronized boolean containsAll(Collection<?> c) {
        return timeSeries.containsAll(c);
    }

    public synchronized boolean addAll(Collection<? extends TimeSeriesItem> c) {
        return timeSeries.addAll(c);
    }

    public synchronized boolean removeAll(Collection<?> c) {
        return timeSeries.removeAll(c);
    }

    public synchronized boolean retainAll(Collection<?> c) {
        return timeSeries.retainAll(c);
    }

    public synchronized void clear() {
        timeSeries.clear();
    }

    public synchronized TimeSeriesItem getFirstItemAtOrBefore(long timestamp) {
        return timeSeries.getFirstItemAtOrBefore(timestamp);
    }

    public synchronized TimeSeriesItem getFirstItemAtOrAfter(long timestamp) {
        return timeSeries.getFirstItemAtOrAfter(timestamp);
    }

    public synchronized long getTimestampAfter(long timestamp) {
        return timeSeries.getTimestampAfter(timestamp);
    }

    public synchronized long getTimestampBefore(long timestamp) {
        return timeSeries.getTimestampBefore(timestamp);
    }

    public synchronized long getModCount() {
        return timeSeries.getModCount();
    }

    public String toString() {
        return "TimeSeries " + getPath();
    }

}
