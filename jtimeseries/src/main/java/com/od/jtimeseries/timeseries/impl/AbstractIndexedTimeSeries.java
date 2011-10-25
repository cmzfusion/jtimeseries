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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 04-Dec-2008
 * Time: 12:19:35
 *
 * Abstract superclass for IndexedTimeSeries based around an array datastructure
 * Provides a mechanism to queue up change events and notify listeners in a separate thread
 */
abstract class AbstractIndexedTimeSeries extends AbstractLockedTimeSeries implements IndexedTimeSeries {

    private static final LogMethods logMethods = LogUtils.getLogMethods(AbstractIndexedTimeSeries.class);
    
    private final RandomAccessDeque<TimeSeriesItem> series;
    private final TimeSeriesListenerSupport timeSeriesListenerSupport = new TimeSeriesListenerSupport();

    private long modCountOnLastHashcode = -1;
    private int hashCode;

    private final String toStringDescription = "Series-" + getClass().getSimpleName() + "(" + System.identityHashCode(this) + ")";
    private boolean listenerAdded;

    protected AbstractIndexedTimeSeries() {
        series =new RandomAccessDeque<TimeSeriesItem>();
    }

    protected AbstractIndexedTimeSeries(Collection<TimeSeriesItem> items) {
        series = new RandomAccessDeque<TimeSeriesItem>(items);
    }

    protected TimeSeriesItem locked_getLatestItem() {
        return size() == 0 ? null : series.getLast();
    }

    protected TimeSeriesItem locked_getEarliestItem() {
        return size() == 0 ? null : series.getFirst();
    }

    protected long locked_getEarliestTimestamp() {
        return size() == 0 ? -1 : getEarliestItem().getTimestamp();
    }

    protected long locked_getLatestTimestamp() {
        return size() == 0 ? -1 : getLatestItem().getTimestamp();
    }

    protected int locked_size() {
        return series.size();
    }

    protected TimeSeriesItem locked_getItem(int index) {
        return series.get(index);
    }

    protected void locked_clear() {
        series.clear();
        TimeSeriesEvent e = TimeSeriesEvent.createSeriesChangedEvent(this, Collections.<TimeSeriesItem>emptyList(), getModCount());
        queueSeriesChangedEvent(e);
    }

    protected boolean locked_removeItem(TimeSeriesItem o) {
        boolean result = doRemove(o);
        if ( result ) {
            queueItemsRemovedEvent(TimeSeriesEvent.createItemsRemovedEvent(this, Collections.singletonList((TimeSeriesItem) o), getModCount()));
        }
        return result;
    }

    protected void locked_removeAll(Iterable<TimeSeriesItem> items) {
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

    protected void locked_addItem(TimeSeriesItem timeSeriesItem) {
        doAddItem(timeSeriesItem);
        queueItemsAddedOrInsertedEvent(TimeSeriesEvent.createItemsAddedOrInsertedEvent(this, Collections.singletonList(timeSeriesItem), getModCount()));
    }

    protected void locked_addAll(Iterable<TimeSeriesItem> items) {
        List<TimeSeriesItem> itemsAdded = new ArrayList<TimeSeriesItem>();
        for (TimeSeriesItem i : items) {
            doAddItem(i);
            itemsAdded.add(i);
        }

        if ( itemsAdded.size() > 0) {
            queueItemsAddedOrInsertedEvent(TimeSeriesEvent.createItemsAddedOrInsertedEvent(this, itemsAdded, getModCount()));
        }
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

    protected Iterator<TimeSeriesItem> locked_iterator() {
        return series.iterator();
    }

    protected List<TimeSeriesItem> locked_getSnapshot() {
        return new ArrayList<TimeSeriesItem>(series);
    }

    protected long locked_getModCount() {
        return series.getModCount();
    }

    protected TimeSeriesItem locked_getFirstItemAtOrBefore(long timestamp) {
        return SeriesUtils.getFirstItemAtOrBefore(timestamp, this);
    }

    protected TimeSeriesItem locked_getFirstItemAtOrAfter(long timestamp) {
        return SeriesUtils.getFirstItemAtOrAfter(timestamp, this);
    }

    protected List<TimeSeriesItem> locked_getItemsInRange(long startTime, long endTime) {
        return SeriesUtils.getItemsInRange(startTime, endTime, this);
    }

    protected void locked_addTimeSeriesListener(final TimeSeriesListener l) {
        listenerAdded = true;
        //add the listener on the event firing thread
        //this is so that the client doesn't receive any previously fired events
        //which are still on the event queue waiting to be processed
        Runnable t = new Runnable() {
            public void run() {
                timeSeriesListenerSupport.addTimeSeriesListener(l);
            }
        };
        fireEvent(t);
    }

    protected void locked_removeTimeSeriesListener(final TimeSeriesListener l) {
        if ( listenerAdded ) {
            Runnable t = new Runnable() {
                public void run() {
                    timeSeriesListenerSupport.removeTimeSeriesListener(l);
                }
            };
            fireEvent(t);
        }
    }

    protected void queueSeriesChangedEvent(final TimeSeriesEvent e) {
        if ( listenerAdded) {
            Runnable t = new Runnable() {
                public void run() {
                    logMethods.logDebug("Firing event " + e);
                    timeSeriesListenerSupport.fireSeriesChanged(e);
                    logMethods.logDebug("Finished firing event " + e);
                }
            };
            fireEvent(t);
        }
    }

    protected void queueItemsAddedOrInsertedEvent(final TimeSeriesEvent e) {
        if ( listenerAdded ) {
            Runnable t = new Runnable() {
                public void run() {
                    logMethods.logDebug("Firing event " + e);
                    timeSeriesListenerSupport.fireItemsAddedOrInserted(e);
                    logMethods.logDebug("Finished firing event " + e);
                }
            };
            fireEvent(t);
        }
    }

    protected void queueItemsRemovedEvent(final TimeSeriesEvent e) {
        if ( listenerAdded) {
            Runnable t = new Runnable() {
                public void run() {
                    logMethods.logDebug("Firing event " + e);
                    timeSeriesListenerSupport.fireItemsRemoved(e);
                    logMethods.logDebug("Finished firing event " + e);
                }
            };
            fireEvent(t);
        }
    }

    private void fireEvent(Runnable t) {
        //only fire event if there might be a listener to receive it
        getSeriesEventExecutor().execute(t);
    }

    protected Executor getSeriesEventExecutor() {
        return TimeSeriesExecutorFactory.getExecutorForTimeSeriesEvents(this);
    }

    protected void locked_addAllWithoutFiringEvents(Collection<TimeSeriesItem> c) {
        series.addAll(c);
    }

    protected String locked_toString() {
        return toStringDescription;
    }

    protected int locked_hashCode() {
        if ( getModCount() != modCountOnLastHashcode) {
            hashCode = SeriesUtils.hashCodeByItems(this);
            modCountOnLastHashcode = getModCount();
        }
        return hashCode;
    }

    protected boolean locked_equals(Object o) {
        if ( o == this ) {
            return true;
        } else {
            return o instanceof TimeSeries && SeriesUtils.areTimeSeriesEqualByItems(this, (TimeSeries) o);
        }
    }

}
