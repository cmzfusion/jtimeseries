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
import com.od.jtimeseries.timeseries.util.SeriesUtils;
import com.od.jtimeseries.util.TimeSeriesExecutorFactory;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.util.logging.LogMethods;

import java.util.*;
import java.util.concurrent.Executor;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 04-Dec-2008
 * Time: 12:19:35
 *
 * Abstract superclass for list based TimeSeries
 * Provides a mechanism to queue up change events and notify listeners in a separate thread
 */
abstract class AbstractIndexedTimeSeries implements IndexedTimeSeries {

    private static final LogMethods logMethods = LogUtils.getLogMethods(AbstractIndexedTimeSeries.class);
    
    private final RandomAccessDeque<TimeSeriesItem> series;
    private final TimeSeriesListenerSupport timeSeriesListenerSupport = new TimeSeriesListenerSupport();

    private long modCountOnLastHashcode = -1;
    private int hashCode;

    private final String toStringDescription = "Series-" + getClass().getSimpleName() + "(" + System.identityHashCode(this) + ")";

    protected AbstractIndexedTimeSeries() {
        series =new RandomAccessDeque<TimeSeriesItem>();
    }

    protected AbstractIndexedTimeSeries(Collection<TimeSeriesItem> items) {
        series = new RandomAccessDeque<TimeSeriesItem>(items);
    }

    public synchronized TimeSeriesItem getLatestItem() {
        return size() == 0 ? null : series.getLast();
    }

    public synchronized TimeSeriesItem getEarliestItem() {
        return size() == 0 ? null : series.getFirst();
    }

    public long getEarliestTimestamp() {
        return size() == 0 ? -1 : getEarliestItem().getTimestamp();
    }

    public long getLatestTimestamp() {
        return size() == 0 ? -1 : getLatestItem().getTimestamp();
    }

    public synchronized int size() {
        return series.size();
    }

    public synchronized TimeSeriesItem getItem(int index) {
        return series.get(index);
    }

    public synchronized void clear() {
        series.clear();
        TimeSeriesEvent e = TimeSeriesEvent.createSeriesChangedEvent(this, Collections.<TimeSeriesItem>emptyList(), getModCount());
        queueSeriesChangedEvent(e);
    }

    public synchronized boolean removeItem(TimeSeriesItem o) {
        boolean result = doRemove(o);
        if ( result ) {
            queueItemsRemovedEvent(TimeSeriesEvent.createItemsRemovedEvent(this, Collections.singletonList((TimeSeriesItem) o), getModCount()));
        }
        return result;
    }

    public synchronized void removeAll(Iterable<TimeSeriesItem> items) {
        List<TimeSeriesItem> removed = new ArrayList<TimeSeriesItem>();
        for (TimeSeriesItem i : items) {
            if ( removeItem(i)) {
                removed.add(i);
            }
        }
        if ( removed.size() > 0 ) {
            queueItemsRemovedEvent(TimeSeriesEvent.createItemsRemovedEvent(this, removed, getModCount()));
        }
    }

    private boolean doRemove(TimeSeriesItem o) {
        boolean result = false;
        if ( size() > 0) {
            int firstPossibleIndex = SeriesUtils.getIndexOfFirstItemAtOrAfter(o.getTimestamp(), this);
            if ( firstPossibleIndex > -1 ) {
                int index = firstPossibleIndex;
                TimeSeriesItem i = series.get(index);
                //only worth comparing while timestamp is the same
                while ( i.getTimestamp() == o.getTimestamp()) {
                    if (o.equals(i)) {
                        series.remove(index);
                        result = true;
                        break;
                    }
                    index++;
                    i = series.get(index);
                }
            }
        }
        return result;
    }

    public synchronized void addItem(TimeSeriesItem timeSeriesItem) {
        doAddItem(timeSeriesItem);
        queueItemsAddedOrInsertedEvent(TimeSeriesEvent.createItemsAddedOrInsertedEvent(this, Collections.singletonList(timeSeriesItem), getModCount()));
    }

    //add all, firing just one event
    public synchronized void addAll(Iterable<TimeSeriesItem> items) {
        List<TimeSeriesItem> itemsAdded = new ArrayList<TimeSeriesItem>();
        for (TimeSeriesItem i : items) {
            doAddItem(i);
            itemsAdded.add(i);
        }
        queueItemsAddedOrInsertedEvent(TimeSeriesEvent.createItemsAddedOrInsertedEvent(this, itemsAdded, getModCount()));
    }

    private void doAddItem(TimeSeriesItem timeSeriesItem) {
        if ( size() == 0 || timeSeriesItem.getTimestamp() >= getLatestTimestamp()) {
            series.add(timeSeriesItem);
        } else {
            //if there are already items with this timestamp, add to appear after those items
            //this will mean that this item is the last one before any item at timestamp + 1
            int indexToAdd = SeriesUtils.getIndexOfFirstItemAtOrAfter(timeSeriesItem.getTimestamp() + 1, this);
            series.add(indexToAdd, timeSeriesItem);
        }
    }

    public synchronized Iterator<TimeSeriesItem> iterator() {
        return series.iterator();
    }

    public List<TimeSeriesItem> getSnapshot() {
        return new ArrayList<TimeSeriesItem>(series);
    }

    public synchronized long getModCount() {
        return series.getModCount();
    }

    /**
     * @return starting with most recent item and moving back, return the first item in the series with a timestamp equal to or earlier than the supplied timestamp, or null if no such item exists
     */
    public synchronized TimeSeriesItem getFirstItemAtOrBefore(long timestamp) {
        return SeriesUtils.getFirstItemAtOrBefore(timestamp, this);
    }

    /**
     * @return starting with earliest item and moving forward, return first item in the series with a timestamp equal to or later than the supplied timestamp, or null if no such item exists
     */
    public synchronized TimeSeriesItem getFirstItemAtOrAfter(long timestamp) {
        return SeriesUtils.getFirstItemAtOrAfter(timestamp, this);
    }

    public synchronized List<TimeSeriesItem> getItemsInRange(long startTime, long endTime) {
        return SeriesUtils.getItemsInRange(startTime, endTime, this);
    }

    public synchronized void addTimeSeriesListener(final TimeSeriesListener l) {
        //add the listener on the event firing thread
        //this is so that the client doesn't receive any previously fired events
        //which are still on the event queue waiting to be processed
        getSeriesEventExecutor().execute(
            new Runnable() {
                public void run() {
                    timeSeriesListenerSupport.addTimeSeriesListener(l);
                }
            }
        );
    }

    public synchronized void removeTimeSeriesListener(TimeSeriesListener l) {
        timeSeriesListenerSupport.removeTimeSeriesListener(l);
    }

    protected Executor getSeriesEventExecutor() {
        return TimeSeriesExecutorFactory.getExecutorForTimeSeriesEvents(this);
    }

    protected void queueSeriesChangedEvent(final TimeSeriesEvent e) {
        getSeriesEventExecutor().execute(
            new Runnable() {
                public void run() {
                    logMethods.logDebug("Firing event " + e);
                    timeSeriesListenerSupport.fireSeriesChanged(e);
                    logMethods.logDebug("Finished firing event " + e);
                }
            }
        );
    }

    protected void queueItemsAddedOrInsertedEvent(final TimeSeriesEvent e) {
        getSeriesEventExecutor().execute(
            new Runnable() {
                public void run() {
                    logMethods.logDebug("Firing event " + e);
                    timeSeriesListenerSupport.fireItemsAddedOrInserted(e);
                    logMethods.logDebug("Finished firing event " + e);
                }
            }
        );
    }

    protected void queueItemsRemovedEvent(final TimeSeriesEvent e) {
        getSeriesEventExecutor().execute(
            new Runnable() {
                public void run() {
                    logMethods.logDebug("Firing event " + e);
                    timeSeriesListenerSupport.fireItemsRemoved(e);
                    logMethods.logDebug("Finished firing event " + e);
                }
            }
        );
    }

    //sometimes it is helpful to be able to add items without firing events to listeners.
    //(e.g. this might be as a performance optimization after construction and before any listeners
    //have been added.)
    protected synchronized void addAllWithoutFiringEvents(Collection<TimeSeriesItem> c) {
        series.addAll(c);
    }

    public String toString() {
        return toStringDescription;
    }

    public int hashCode() {
        if ( getModCount() != modCountOnLastHashcode) {
            hashCode = SeriesUtils.hashCodeByItems(this);
            modCountOnLastHashcode = getModCount();
        }
        return hashCode;
    }

    public boolean equals(Object o) {
        if ( o == this ) {
            return true;
        } else {
            return o instanceof TimeSeries && SeriesUtils.areTimeSeriesEqualByItems(this, (TimeSeries) o);
        }
    }

}
