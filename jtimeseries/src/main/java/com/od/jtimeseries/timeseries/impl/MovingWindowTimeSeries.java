package com.od.jtimeseries.timeseries.impl;

import com.od.jtimeseries.timeseries.ListTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.timeseries.TimeSeriesListener;
import com.od.jtimeseries.util.NamedExecutors;
import com.od.jtimeseries.util.time.TimePeriod;
import com.od.jtimeseries.util.time.TimeSource;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 30/01/11
 * Time: 10:27
 * To change this template use File | Settings | File Templates.
 */
public class MovingWindowTimeSeries extends AbstractListTimeSeries {

    private static ScheduledExecutorService scheduledExecutorService = NamedExecutors.newScheduledThreadPool(MovingWindowTimeSeries.class.getSimpleName(), 2);
    private int startIndex;
    private int endIndex;
    private TimeSource startTimeSource;
    private TimeSource endTimeSource;
    private TimePeriod frequencyToCheckWindow;
    
    private ListTimeSeries listTimeSeries;

    public MovingWindowTimeSeries(TimeSource startTimeSource, TimeSource endTimeSource, TimePeriod frequencyToCheckWindow) {
        super(new DefaultTimeSeries());
        this.startTimeSource = startTimeSource;
        this.endTimeSource = endTimeSource;
        this.frequencyToCheckWindow = frequencyToCheckWindow;
    }

    public List<TimeSeriesItem> getSnapshot() {
        return listTimeSeries.getSnapshot();
    }

    public int getIndexOfFirstItemAtOrBefore(long timestamp) {
        return listTimeSeries.getIndexOfFirstItemAtOrBefore(timestamp);
    }

    public int getIndexOfFirstItemAtOrAfter(long timestamp) {
        return listTimeSeries.getIndexOfFirstItemAtOrAfter(timestamp);
    }

    public int size() {
        return listTimeSeries.size();
    }

    public boolean isEmpty() {
        return listTimeSeries.isEmpty();
    }

    public boolean contains(Object o) {
        return listTimeSeries.contains(o);
    }

    public Iterator<TimeSeriesItem> iterator() {
        return listTimeSeries.iterator();
    }

    public Object[] toArray() {
        return listTimeSeries.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return listTimeSeries.toArray(a);
    }

    public boolean add(TimeSeriesItem timeSeriesItem) {
        return listTimeSeries.add(timeSeriesItem);
    }

    public boolean remove(Object o) {
        return listTimeSeries.remove(o);
    }

    public boolean containsAll(Collection<?> c) {
        return listTimeSeries.containsAll(c);
    }

    public boolean addAll(Collection<? extends TimeSeriesItem> c) {
        return listTimeSeries.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends TimeSeriesItem> c) {
        return listTimeSeries.addAll(index, c);
    }

    public boolean removeAll(Collection<?> c) {
        return listTimeSeries.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return listTimeSeries.retainAll(c);
    }

    public void clear() {
        listTimeSeries.clear();
    }

    public boolean equals(Object o) {
        return listTimeSeries.equals(o);
    }

    public int hashCode() {
        return listTimeSeries.hashCode();
    }

    public TimeSeriesItem get(int index) {
        return listTimeSeries.get(index);
    }

    public TimeSeriesItem set(int index, TimeSeriesItem element) {
        return listTimeSeries.set(index, element);
    }

    public void add(int index, TimeSeriesItem element) {
        listTimeSeries.add(index, element);
    }

    public TimeSeriesItem remove(int index) {
        return listTimeSeries.remove(index);
    }

    public int indexOf(Object o) {
        return listTimeSeries.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return listTimeSeries.lastIndexOf(o);
    }

    public ListIterator<TimeSeriesItem> listIterator() {
        return listTimeSeries.listIterator();
    }

    public ListIterator<TimeSeriesItem> listIterator(int index) {
        return listTimeSeries.listIterator(index);
    }

    public List<TimeSeriesItem> subList(int fromIndex, int toIndex) {
        return listTimeSeries.subList(fromIndex, toIndex);
    }

    public boolean prepend(TimeSeriesItem item) {
        return listTimeSeries.prepend(item);
    }

    public boolean append(TimeSeriesItem value) {
        return listTimeSeries.append(value);
    }

    public TimeSeries getSubSeries(long timestamp) {
        return listTimeSeries.getSubSeries(timestamp);
    }

    public TimeSeries getSubSeries(long startTimestamp, long endTimestamp) {
        return listTimeSeries.getSubSeries(startTimestamp, endTimestamp);
    }

    public TimeSeriesItem getEarliestItem() {
        return listTimeSeries.getEarliestItem();
    }

    public TimeSeriesItem getLatestItem() {
        return listTimeSeries.getLatestItem();
    }

    public TimeSeriesItem removeEarliestItem() {
        return listTimeSeries.removeEarliestItem();
    }

    public TimeSeriesItem removeLatestItem() {
        return listTimeSeries.removeLatestItem();
    }

    public long getEarliestTimestamp() {
        return listTimeSeries.getEarliestTimestamp();
    }

    public long getLatestTimestamp() {
        return listTimeSeries.getLatestTimestamp();
    }

    public TimeSeriesItem getFirstItemAtOrBefore(long timestamp) {
        return listTimeSeries.getFirstItemAtOrBefore(timestamp);
    }

    public TimeSeriesItem getFirstItemAtOrAfter(long timestamp) {
        return listTimeSeries.getFirstItemAtOrAfter(timestamp);
    }

    public long getTimestampAfter(long timestamp) {
        return listTimeSeries.getTimestampAfter(timestamp);
    }

    public long getTimestampBefore(long timestamp) {
        return listTimeSeries.getTimestampBefore(timestamp);
    }

    public void addTimeSeriesListener(TimeSeriesListener l) {
        listTimeSeries.addTimeSeriesListener(l);
    }

    public void removeTimeSeriesListener(TimeSeriesListener l) {
        listTimeSeries.removeTimeSeriesListener(l);
    }

    public long getModCount() {
        return listTimeSeries.getModCount();
    }
}
