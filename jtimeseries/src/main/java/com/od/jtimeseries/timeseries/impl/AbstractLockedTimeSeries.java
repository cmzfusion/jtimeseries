package com.od.jtimeseries.timeseries.impl;

import com.od.jtimeseries.timeseries.TimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.timeseries.TimeSeriesListener;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 24/10/11
 * Time: 17:27
 */
public abstract class AbstractLockedTimeSeries implements TimeSeries {

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public Lock readLock() {
        return lock.readLock();
    }

    public Lock writeLock() {
        return lock.writeLock();
    }

    public final TimeSeriesItem getLatestItem() {
        try {
            this.readLock().lock();
            return locked_getLatestItem();
        } finally {
            this.readLock().unlock();
        }

    }

    protected abstract TimeSeriesItem locked_getLatestItem();

    public final TimeSeriesItem getEarliestItem() {
        try {
            this.readLock().lock();
            return locked_getEarliestItem();
        } finally {
            this.readLock().unlock();
        }

    }

    protected abstract TimeSeriesItem locked_getEarliestItem();

    public final long getEarliestTimestamp() {
        try {
            this.readLock().lock();
            return locked_getEarliestTimestamp();
        } finally {
            this.readLock().unlock();
        }

    }

    protected abstract long locked_getEarliestTimestamp();

    public final long getLatestTimestamp() {
        try {
            this.readLock().lock();
            return locked_getLatestTimestamp();
        } finally {
            this.readLock().unlock();
        }

    }

    protected abstract long locked_getLatestTimestamp();

    public final int size() {
        try {
            this.readLock().lock();
            return locked_size();
        } finally {
            this.readLock().unlock();
        }

    }

    protected abstract int locked_size();

    public final TimeSeriesItem getItem(int index) {
        try {
            this.readLock().lock();
            return locked_getItem(index);
        } finally {
            this.readLock().unlock();
        }

    }

    protected abstract TimeSeriesItem locked_getItem(int index);

    public final void clear() {
        try {
            this.writeLock().lock();
            locked_clear();
        } finally {
            this.writeLock().unlock();
        }

    }

    protected abstract void locked_clear();

    public final boolean removeItem(TimeSeriesItem o) {
        try {
            this.writeLock().lock();
            return locked_removeItem(o);
        } finally {
            this.writeLock().unlock();
        }

    }

    protected abstract boolean locked_removeItem(TimeSeriesItem o);

    public final void removeAll(Iterable<TimeSeriesItem> items) {
        try {
            this.writeLock().lock();
            locked_removeAll(items);
        } finally {
            this.writeLock().unlock();
        }

    }

    protected abstract void locked_removeAll(Iterable<TimeSeriesItem> items);

    public final void addItem(TimeSeriesItem timeSeriesItem) {
        try {
            this.writeLock().lock();
            locked_addItem(timeSeriesItem);
        } finally {
            this.writeLock().unlock();
        }

    }

    protected abstract void locked_addItem(TimeSeriesItem timeSeriesItem);

    //add all, firing just one event
    public final void addAll(Iterable<TimeSeriesItem> items) {
        try {
            this.writeLock().lock();
            locked_addAll(items);
        } finally {
            this.writeLock().unlock();
        }

    }

    protected abstract void locked_addAll(Iterable<TimeSeriesItem> items);

    public final Iterator<TimeSeriesItem> iterator() {
        try {
            this.readLock().lock();
            return locked_iterator();
        } finally {
            this.readLock().unlock();
        }

    }

    protected abstract Iterator<TimeSeriesItem> locked_iterator();

    public final List<TimeSeriesItem> getSnapshot() {
        try {
            this.readLock().lock();
            return locked_getSnapshot();
        } finally {
            this.readLock().unlock();
        }

    }

    protected abstract List<TimeSeriesItem> locked_getSnapshot();

    public final long getModCount() {
        try {
            this.readLock().lock();
            return locked_getModCount();
        } finally {
            this.readLock().unlock();
        }

    }

    protected abstract long locked_getModCount();

    /**
     * @return starting with most recent item and moving back, return the first item in the series with a timestamp equal to or earlier than the supplied timestamp, or null if no such item exists
     */
    public final TimeSeriesItem getFirstItemAtOrBefore(long timestamp) {
        try {
            this.readLock().lock();
            return locked_getFirstItemAtOrBefore(timestamp);
        } finally {
            this.readLock().unlock();
        }

    }

    protected abstract TimeSeriesItem locked_getFirstItemAtOrBefore(long timestamp);

    /**
     * @return starting with earliest item and moving forward, return first item in the series with a timestamp equal to or later than the supplied timestamp, or null if no such item exists
     */
    public final TimeSeriesItem getFirstItemAtOrAfter(long timestamp) {
        try {
            this.readLock().lock();
            return locked_getFirstItemAtOrAfter(timestamp);
        } finally {
            this.readLock().unlock();
        }

    }

    protected abstract TimeSeriesItem locked_getFirstItemAtOrAfter(long timestamp);

    public final List<TimeSeriesItem> getItemsInRange(long startTime, long endTime) {
        try {
            this.readLock().lock();
            return locked_getItemsInRange(startTime, endTime);
        } finally {
            this.readLock().unlock();
        }

    }

    protected abstract List<TimeSeriesItem> locked_getItemsInRange(long startTime, long endTime);

    public final void addTimeSeriesListener(final TimeSeriesListener l) {
        try {
            this.writeLock().lock();
            locked_addTimeSeriesListener(l);
        } finally {
            this.writeLock().unlock();
        }

    }

    protected abstract void locked_addTimeSeriesListener(TimeSeriesListener l);

    public final void removeTimeSeriesListener(final TimeSeriesListener l) {
        try {
            this.writeLock().lock();
            locked_removeTimeSeriesListener(l);
        } finally {
            this.writeLock().unlock();
        }

    }

    protected abstract void locked_removeTimeSeriesListener(TimeSeriesListener l);

    //sometimes it is helpful to be able to add items without firing events to listeners.
    //(e.g. this might be as a performance optimization after construction and before any listeners
    //have been added.)
    public final void addAllWithoutFiringEvents(Collection<TimeSeriesItem> c) {
        try {
            this.writeLock().lock();
            locked_addAllWithoutFiringEvents(c);
        } finally {
            this.writeLock().unlock();
        }

    }

    protected abstract void locked_addAllWithoutFiringEvents(Collection<TimeSeriesItem> c);

    public final String toString() {
        try {
            this.readLock().lock();
            return locked_toString();
        } finally {
            this.readLock().unlock();
        }

    }

    protected abstract String locked_toString();

    public final int hashCode() {
        try {
            this.readLock().lock();
            return locked_hashCode();
        } finally {
            this.readLock().unlock();
        }

    }

    protected abstract int locked_hashCode();

    public final boolean equals(Object o) {
        try {
            this.readLock().lock();
            return locked_equals(o);
        } finally {
            this.readLock().unlock();
        }

    }

    protected abstract boolean locked_equals(Object o);
}
