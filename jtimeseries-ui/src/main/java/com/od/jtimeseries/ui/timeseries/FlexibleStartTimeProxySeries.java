package com.od.jtimeseries.ui.timeseries;

import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.timeseries.impl.DefaultTimeSeries;

import java.lang.ref.SoftReference;
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
    private SoftReference<TimeSeries> localSnapshotSeries;

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
        TimeSeriesItem result;
        if ( timestamp >= this.startTimestamp) {
            result = super.getFirstItemAtOrBefore(timestamp);
            if ( result != null && result.getTimestamp() < startTimestamp) {
                result = null;
            }
        }
        return null;
    }

    public TimeSeriesItem getFirstItemAtOrAfter(long timestamp) {
        TimeSeriesItem result = super.getFirstItemAtOrAfter(timestamp);
        if ( result != null && result.getTimestamp() < startTimestamp ) {
            result = null;
        }
        return result;
    }

    public long getTimestampAfter(long timestamp) {
        return timestamp < startTimestamp ? startTimestamp : super.getTimestampAfter(timestamp);
    }

    public long getTimestampBefore(long timestamp) {
        return timestamp <= startTimestamp ? -1 : super.getTimestampBefore(timestamp);
    }

    public Collection<TimeSeriesItem> getSnapshot() {
        DefaultTimeSeries s = new DefaultTimeSeries();
        s.addAll(getLocalSnapshotSeries());
        return s;
    }

    public int size() {
        return getLocalSnapshotSeries().size();
    }

    public boolean isEmpty() {
        return getLocalSnapshotSeries().isEmpty();
    }

    public boolean contains(Object o) {
        return getLocalSnapshotSeries().contains(o);
    }

    public Iterator<TimeSeriesItem> iterator() {
        return getLocalSnapshotSeries().iterator();
    }

    public Object[] toArray() {
        return getLocalSnapshotSeries().toArray();
    }

    public <T> T[] toArray(T[] a) {
        return getLocalSnapshotSeries().toArray(a);
    }

    public boolean add(TimeSeriesItem timeSeriesItem) {
        throw new UnsupportedOperationException("FlexibleStartTimeProxySeries does not yet support this operation");
    }

    public boolean remove(Object o) {
        throw new UnsupportedOperationException("FlexibleStartTimeProxySeries does not yet support this operation");
    }

    public boolean containsAll(Collection<?> c) {
        return getLocalSnapshotSeries().containsAll(c);
    }

    public boolean addAll(Collection<? extends TimeSeriesItem> c) {
        throw new UnsupportedOperationException("FlexibleStartTimeProxySeries does not yet support this operation");
    }

    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("FlexibleStartTimeProxySeries does not yet support this operation");
    }

    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("FlexibleStartTimeProxySeries does not yet support this operation");
    }

    public void clear() {
        throw new UnsupportedOperationException("FlexibleStartTimeProxySeries does not yet support this operation");
    }

    private TimeSeries getLocalSnapshotSeries() {
        TimeSeries result = null;
        if ( localSnapshotSeries != null) {
            result = localSnapshotSeries.get();
        }
        if ( result == null ) {
            result = super.getSubSeries(startTimestamp);
            localSnapshotSeries = new SoftReference<TimeSeries>(result);
        }
        return result;
    }

}
