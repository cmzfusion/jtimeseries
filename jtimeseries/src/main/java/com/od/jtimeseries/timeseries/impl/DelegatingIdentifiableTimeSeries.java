package com.od.jtimeseries.timeseries.impl;

import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.timeseries.TimeSeriesListener;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.identifiable.IdentifiableBase;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 25-Nov-2010
 * Time: 17:07:26
 *
 */
public class DelegatingIdentifiableTimeSeries extends IdentifiableBase implements IdentifiableTimeSeries {

    private IdentifiableTimeSeries wrappedSeries;

    private WrappedTimeSeriesEventHandler eventHandler = new WrappedTimeSeriesEventHandler(this);

    public DelegatingIdentifiableTimeSeries(IdentifiableTimeSeries wrappedSeries) {
        super(wrappedSeries.getId(), wrappedSeries.getDescription());
        this.wrappedSeries = wrappedSeries;
        wrappedSeries.addTimeSeriesListener(eventHandler);
    }

    public boolean prepend(TimeSeriesItem item) {
        return wrappedSeries.prepend(item);
    }

    public boolean append(TimeSeriesItem value) {
        return wrappedSeries.append(value);
    }

    public TimeSeries getSubSeries(long timestamp) {
        return wrappedSeries.getSubSeries(timestamp);
    }

    public TimeSeries getSubSeries(long startTimestamp, long endTimestamp) {
        return wrappedSeries.getSubSeries(startTimestamp, endTimestamp);
    }

    public TimeSeriesItem getEarliestItem() {
        return wrappedSeries.getEarliestItem();
    }

    public TimeSeriesItem getLatestItem() {
        return wrappedSeries.getLatestItem();
    }

    public TimeSeriesItem removeEarliestItem() {
        return wrappedSeries.removeEarliestItem();
    }

    public TimeSeriesItem removeLatestItem() {
        return wrappedSeries.removeLatestItem();
    }

    public long getEarliestTimestamp() {
        return wrappedSeries.getEarliestTimestamp();
    }

    public long getLatestTimestamp() {
        return wrappedSeries.getLatestTimestamp();
    }

    public TimeSeriesItem getFirstItemAtOrBefore(long timestamp) {
        return wrappedSeries.getFirstItemAtOrBefore(timestamp);
    }

    public TimeSeriesItem getFirstItemAtOrAfter(long timestamp) {
        return wrappedSeries.getFirstItemAtOrAfter(timestamp);
    }

    public long getTimestampAfter(long timestamp) {
        return wrappedSeries.getTimestampAfter(timestamp);
    }

    public long getTimestampBefore(long timestamp) {
        return wrappedSeries.getTimestampBefore(timestamp);
    }

    public Collection<TimeSeriesItem> getSnapshot() {
        return wrappedSeries.getSnapshot();
    }

    public int size() {
        return wrappedSeries.size();
    }

    public boolean isEmpty() {
        return wrappedSeries.isEmpty();
    }

    public boolean contains(Object o) {
        return wrappedSeries.contains(o);
    }

    public Iterator<TimeSeriesItem> iterator() {
        return wrappedSeries.iterator();
    }

    public Object[] toArray() {
        return wrappedSeries.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return wrappedSeries.toArray(a);
    }

    public boolean add(TimeSeriesItem o) {
        return wrappedSeries.add(o);
    }

    public boolean remove(Object o) {
        return wrappedSeries.remove(o);
    }

    public boolean containsAll(Collection<?> c) {
        return wrappedSeries.containsAll(c);
    }

    public boolean addAll(Collection<? extends TimeSeriesItem> c) {
        return wrappedSeries.addAll(c);
    }

    public boolean removeAll(Collection<?> c) {
        return wrappedSeries.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return wrappedSeries.retainAll(c);
    }

    public void clear() {
        wrappedSeries.clear();
    }

    public void addTimeSeriesListener(TimeSeriesListener l) {
        eventHandler.addTimeSeriesListener(l);
    }

    public void removeTimeSeriesListener(TimeSeriesListener l) {
        eventHandler.removeTimeSeriesListener(l);
    }

}
