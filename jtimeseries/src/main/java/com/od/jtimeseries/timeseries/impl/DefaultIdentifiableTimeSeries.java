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

import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.timeseries.TimeSeriesListener;
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
    private WrappedTimeSeriesEventHandler eventHandler = new WrappedTimeSeriesEventHandler(this);
    protected boolean remote;

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

    public boolean prepend(TimeSeriesItem item) {
        return timeSeries.prepend(item);
    }

    public boolean append(TimeSeriesItem value) {
        return timeSeries.append(value);
    }

    public Collection<TimeSeriesItem> getSnapshot() {
        return timeSeries.getSnapshot();
    }

    public TimeSeries getSubSeries(long timestamp) {
        return timeSeries.getSubSeries(timestamp);
    }

    public TimeSeries getSubSeries(long startTimestamp, long endTimestamp) {
        return timeSeries.getSubSeries(startTimestamp, endTimestamp);
    }

    public TimeSeriesItem getLatestItem() {
        return timeSeries.getLatestItem();
    }

    public TimeSeriesItem removeLatestItem() {
        return timeSeries.removeLatestItem();
    }

    public long getLatestTimestamp() {
        return timeSeries.getLatestTimestamp();
    }

    public TimeSeriesItem getEarliestItem() {
        return timeSeries.getEarliestItem();
    }

    public TimeSeriesItem removeEarliestItem() {
        return timeSeries.removeEarliestItem();
    }

    public long getEarliestTimestamp() {
        return timeSeries.getEarliestTimestamp();
    }

    public void addTimeSeriesListener(TimeSeriesListener l) {
        eventHandler.addTimeSeriesListener(l);
    }

    public void removeTimeSeriesListener(TimeSeriesListener l) {
        eventHandler.removeTimeSeriesListener(l);
    }

    public int size() {
        return timeSeries.size();
    }

    public boolean isEmpty() {
        return timeSeries.isEmpty();
    }

    public boolean contains(Object o) {
        return timeSeries.contains(o);
    }

    public Iterator<TimeSeriesItem> iterator() {
        return timeSeries.iterator();
    }

    public Object[] toArray() {
        return timeSeries.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return timeSeries.toArray(a);
    }

    public boolean add(TimeSeriesItem o) {
        return timeSeries.add(o);
    }

    public boolean remove(Object o) {
        return timeSeries.remove(o);
    }

    public boolean containsAll(Collection<?> c) {
        return timeSeries.containsAll(c);
    }

    public boolean addAll(Collection<? extends TimeSeriesItem> c) {
        return timeSeries.addAll(c);
    }

    public boolean removeAll(Collection<?> c) {
        return timeSeries.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return timeSeries.retainAll(c);
    }

    public void clear() {
        timeSeries.clear();
    }

    protected TimeSeries getTimeSeries() {
        return timeSeries;
    }

    public TimeSeriesItem getFirstItemAtOrBefore(long timestamp) {
        return timeSeries.getFirstItemAtOrBefore(timestamp);
    }

    public TimeSeriesItem getFirstItemAtOrAfter(long timestamp) {
        return timeSeries.getFirstItemAtOrAfter(timestamp);
    }

    public long getTimestampAfter(long timestamp) {
        return timeSeries.getTimestampAfter(timestamp);
    }

    public long getTimestampBefore(long timestamp) {
        return timeSeries.getTimestampBefore(timestamp);
    }

    public String toString() {
        return "TimeSeries " + getContextPath();
    }

}
