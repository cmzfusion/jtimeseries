package com.od.jtimeseries.ui.timeseries;

import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesItem;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 14/01/11
 * Time: 17:27
 */
public class FlexibleStartTimeProxySeries extends ProxyingPropertyChangeTimeseries {

    TimeSeries wrappedSeries;
    private long startTimestamp;

    public FlexibleStartTimeProxySeries(IdentifiableTimeSeries wrappedSeries) {
        super(wrappedSeries);
    }

    public boolean prepend(TimeSeriesItem item) {
        throw new UnsupportedOperationException("FlexibleStartTimeProxySeries does not yet support this operation");
    }

    public boolean append(TimeSeriesItem value) {
        return wrappedSeries.append(value);
    }

    public TimeSeries getSubSeries(long timestamp) {
        throw new UnsupportedOperationException("FlexibleStartTimeProxySeries does not yet support this operation");
    }

    public TimeSeries getSubSeries(long startTimestamp, long endTimestamp) {
        throw new UnsupportedOperationException("FlexibleStartTimeProxySeries does not yet support this operation");
    }

    public TimeSeriesItem getEarliestItem() {
        return super.getFirstItemAtOrAfter(startTimestamp);
    }

    public TimeSeriesItem getLatestItem() {
        return super.getLatestItem();
    }

    public TimeSeriesItem removeEarliestItem() {
        throw new UnsupportedOperationException("FlexibleStartTimeProxySeries does not yet support this operation");
    }

    public TimeSeriesItem removeLatestItem() {
       return super.removeLatestItem();
    }

    public long getEarliestTimestamp() {
        return startTimestamp;
    }

    public long getLatestTimestamp() {
        return super.getLatestTimestamp();
    }

    public TimeSeriesItem getFirstItemAtOrBefore(long timestamp) {
        TimeSeriesItem result = null;
        if ( timestamp >= this.startTimestamp) {
            result = super.getFirstItemAtOrBefore(timestamp);
            if ( result != null && result.getTimestamp() < startTimestamp) {
                result = null;
            }
        }
        return null;
    }

    public TimeSeriesItem getFirstItemAtOrAfter(long timestamp) {
        TimeSeriesItem result = null;
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

    public boolean add(TimeSeriesItem timeSeriesItem) {
        return wrappedSeries.add(timeSeriesItem);
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

}
