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
import com.od.jtimeseries.util.TimeSeriesExecutorFactory;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.numeric.LongNumeric;

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
abstract class AbstractListTimeSeries implements ListTimeSeries {

    private static final LogMethods logMethods = LogUtils.getLogMethods(AbstractListTimeSeries.class);
    private final OrderValidatingRandomAccessDeque series;
    private final TimeSeriesListenerSupport timeSeriesListenerSupport = new TimeSeriesListenerSupport();

    protected AbstractListTimeSeries() {
        series = new OrderValidatingRandomAccessDeque(new RandomAccessDeque<TimeSeriesItem>());
    }

    protected AbstractListTimeSeries(ListTimeSeries l) {
        this(l.getSnapshot());
    }

    protected AbstractListTimeSeries(Collection<TimeSeriesItem> items) {
        series = new OrderValidatingRandomAccessDeque(new RandomAccessDeque<TimeSeriesItem>(items));
    }

    public synchronized boolean append(TimeSeriesItem item) {
        boolean changed = false;
        if ( size() == 0 || item.getTimestamp() >= getLatestTimestamp()) {
            changed = true;
            series.addLast(item);
            int index = series.size() - 1;
            TimeSeriesEvent t = TimeSeriesEvent.createItemsAddedEvent(this, index, index, Collections.singletonList(item));
            queueItemsAddedEvent(t);
        }
        return changed;
    }

    public synchronized boolean prepend(TimeSeriesItem item) {
        boolean changed = false;
        if ( size() == 0 || item.getTimestamp() <= getEarliestTimestamp()) {
            changed = true;
            series.addFirst(item);
            TimeSeriesEvent t = TimeSeriesEvent.createItemsAddedEvent(this, 0, 0, Collections.singletonList(item));
            queueItemsAddedEvent(t);
        }
        return changed;
    }

    public synchronized List<TimeSeriesItem> getSnapshot() {
        //return a defensive copy of the timeseries maintained by this logger
        return new ArrayList<TimeSeriesItem>(series);
    }

    public synchronized TimeSeriesItem getLatestItem() {
        return isEmpty() ? null : series.getLast();
    }

    public synchronized long getLatestTimestamp() {
        return isEmpty() ? -1 : series.getLast().getTimestamp();
    }

    public synchronized TimeSeriesItem removeLatestItem() {
        TimeSeriesItem result = null;
        if ( series.size() > 0) {
            result = series.removeLast();
            int indexRemoved = series.size();
            queueItemsRemovedEvent(TimeSeriesEvent.createItemsRemovedEvent(this, indexRemoved, indexRemoved, Collections.singletonList(result)));
        }
        return result;
    }

    public synchronized TimeSeriesItem getEarliestItem() {
        return isEmpty() ? null : series.getFirst();
    }

    public synchronized long getEarliestTimestamp() {
        return isEmpty() ? -1 : series.getFirst().getTimestamp();
    }

    public synchronized TimeSeriesItem removeEarliestItem() {
        TimeSeriesItem result = null;
        if ( series.size() > 0) {
            result = series.removeFirst();
        }
        return result;
    }

    public synchronized boolean isEmpty() {
        return series.size() == 0;
    }

    public synchronized int size() {
        return series.size();
    }

    public synchronized TimeSeriesItem get(int index) {
        return series.get(index);
    }

    public synchronized void clear() {
        series.clear();
        TimeSeriesEvent e = TimeSeriesEvent.createSeriesChangedEvent(this, getSnapshot());
        queueSeriesChangedEvent(e);
    }

    public synchronized List<TimeSeriesItem> subList(int fromIndex, int toIndex) {
        return series.subList(fromIndex, toIndex);
    }

    public synchronized ListIterator<TimeSeriesItem> listIterator() {
        return series.listIterator();
    }

    public synchronized ListIterator<TimeSeriesItem> listIterator(int index) {
        return series.listIterator(index);
    }

    public synchronized int lastIndexOf(Object o) {
        return series.lastIndexOf(o);
    }

    public synchronized int indexOf(Object o) {
        return series.indexOf(o);
    }

    public synchronized boolean remove(Object o) {
        int index = indexOf(o);
        if ( index != -1) {
            series.remove(o);
            queueItemsRemovedEvent(TimeSeriesEvent.createItemsRemovedEvent(this, index, index, Collections.singletonList((TimeSeriesItem)o)));
        }
        return index != -1;
    }

    //n.b to obey Collections contract this must throw an IllegalArgumentException rather than handling the case
    //where the timestamp is out of order and returning false as append() does
    public synchronized boolean add(TimeSeriesItem timeSeriesItem) {
        boolean added = series.add(timeSeriesItem);
        if ( added ) {
            int index = size() - 1;
            queueItemsAddedEvent(TimeSeriesEvent.createItemsAddedEvent(this, index, index, Collections.singletonList(timeSeriesItem)));
        }
        return added;
    }

    public synchronized TimeSeriesItem remove(int index) {
        TimeSeriesItem removed = series.remove(index);
        queueItemsRemovedEvent(TimeSeriesEvent.createItemsRemovedEvent(this, index, index, Collections.singletonList(removed)));
        return removed;
    }

    public synchronized void add(int index, TimeSeriesItem item) {
        series.add(index, item);
        queueItemsAddedEvent(TimeSeriesEvent.createItemsAddedEvent(this, index, index, Collections.singletonList(item)));
    }

    public synchronized TimeSeriesItem set(int index, TimeSeriesItem item) {
        TimeSeriesItem replaced = series.set(index, item);
        queueItemsChangedEvent(TimeSeriesEvent.createItemsChangedEvent(this, index, index, Collections.singletonList(item)));
        return replaced;
    }

    public synchronized boolean retainAll(Collection<?> c) {
        boolean changed = series.retainAll(c);
        queueSeriesChangeEventIfChanged(changed);
        return changed;
    }

    public synchronized boolean removeAll(Collection<?> c) {
        boolean changed = series.removeAll(c);
        queueSeriesChangeEventIfChanged(changed);
        return changed;
    }

    public synchronized boolean addAll(Collection<? extends TimeSeriesItem> c) {
        int startIndex = size();
        boolean result = series.addAll(c);
        if ( result ) {
           queueItemsAddedEvent(TimeSeriesEvent.createItemsAddedEvent(this, startIndex, startIndex + c.size() - 1, new ArrayList<TimeSeriesItem>(c)));
        }
        return result;
    }

    public synchronized boolean addAll(int index, Collection<? extends TimeSeriesItem> c) {
        boolean result = series.addAll(index, c);
         if ( result ) {
           queueItemsAddedEvent(TimeSeriesEvent.createItemsAddedEvent(this, index, index + c.size() - 1, new ArrayList<TimeSeriesItem>(c)));
        }
        return result;

    }

    public synchronized boolean containsAll(Collection<?> c) {
        return series.containsAll(c);
    }

    public synchronized Object[] toArray() {
        return series.toArray();
    }

    public synchronized <T> T[] toArray(T[] a) {
        return series.toArray(a);
    }

    public synchronized Iterator<TimeSeriesItem> iterator() {
        return series.iterator();
    }

    public synchronized boolean contains(Object o) {
        return series.contains(o);
    }

    public synchronized boolean equals(Object o) {
        return series.equals(o);
    }

    public synchronized int hashCode() {
        return series.hashCode();
    }

    public synchronized TimeSeries getSubSeries(long startTimestamp, long endTimestamp) {
        return getItemsInRange(startTimestamp, endTimestamp);
    }

    public synchronized TimeSeries getSubSeries(long timestamp) {
        return getItemsInRange(timestamp, Long.MAX_VALUE);
    }

    private TimeSeries getItemsInRange(long earliest, long latest) {
        int startIndex = getIndexOfFirstItemAtOrAfter(earliest);
        int endIndex = getIndexOfFirstItemAtOrBefore(latest);
        TimeSeries result;
        if ( startIndex != -1 && endIndex != -1) {
            result = new DefaultTimeSeries(subList(startIndex,endIndex + 1));
        } else {
            result = new DefaultTimeSeries();
        }
        return result;
    }

    int binarySearchForItemWithTimestamp(long timestamp) {
        return Collections.binarySearch(
            this,
            new TimeSeriesItem(timestamp, new LongNumeric(0)),
            new Comparator<TimeSeriesItem>() {
                public int compare(TimeSeriesItem o1, TimeSeriesItem o2) {
                    return o1.getTimestamp() == o2.getTimestamp() ? 0 :
                        o1.getTimestamp() < o2.getTimestamp() ? -1 : 1;
                }
            }
        );
    }

    public synchronized long getTimestampAfter(long timestamp) {
        TimeSeriesItem item = getFirstItemAtOrAfter(timestamp + 1);
        return item != null ? item.getTimestamp() : -1;
    }

    public synchronized long getTimestampBefore(long timestamp) {
        TimeSeriesItem item = getFirstItemAtOrBefore(timestamp - 1);
        return item != null ? item.getTimestamp() : -1;
    }
    
    /**
     * @return  index of the first item in the series with a timestamp equal to or later than the supplied timestamp
     */
    public synchronized int getIndexOfFirstItemAtOrAfter(long timestamp) {
        int index = binarySearchForItemWithTimestamp(timestamp);
        if ( index >= 0) {
            index = findLowestIndexSharingTimestamp(timestamp, index);
        } else {
            index = -index-1;
            index = index < size() ? index : -1;
        }
        return index;
    }

    /**
     * @return  index of the first item in the series with a timestamp equal to or earlier than the supplied timestamp
     */
    public synchronized int getIndexOfFirstItemAtOrBefore(long timestamp) {
        int index = binarySearchForItemWithTimestamp(timestamp);
        if ( index >= 0) {
            index = findHighestIndexSharingTimestamp(timestamp, index);
        } else {
            index = -index - 2;
        }
        return index;
    }

    public synchronized TimeSeriesItem getFirstItemAtOrBefore(long timestamp) {
        TimeSeriesItem result = null;
        int index = getIndexOfFirstItemAtOrBefore(timestamp);
        if ( index > -1 ) {
            result = get(index);
        }
        return result;
    }

    public synchronized TimeSeriesItem getFirstItemAtOrAfter(long timestamp) {
        TimeSeriesItem result = null;
        int index = getIndexOfFirstItemAtOrAfter(timestamp);
        if ( index > -1 ) {
            result = get(index);
        }
        return result;
    }

    private int findLowestIndexSharingTimestamp(long timestamp, int index) {
        while ( index > 0) {
            if ( get(index - 1).getTimestamp() == timestamp) {
                index--;
            } else {
                break;
            }
        }
        return index;
    }

    private int findHighestIndexSharingTimestamp(long timestamp, int index) {
        while ( index + 1 < size()) {
            if ( get(index + 1).getTimestamp() == timestamp) {
                index++;
            } else {
                break;
            }
        }
        return index;
    }

    public synchronized void addTimeSeriesListener(TimeSeriesListener l) {
        timeSeriesListenerSupport.addTimeSeriesListener(l);
    }

    public synchronized void removeTimeSeriesListener(TimeSeriesListener l) {
        timeSeriesListenerSupport.removeTimeSeriesListener(l);
    }

    protected Executor getSeriesEventExecutor() {
        return TimeSeriesExecutorFactory.getExecutorForTimeSeriesEvents(this);
    }

    private void queueSeriesChangeEventIfChanged(boolean changed) {
        if ( changed ) {
            queueSeriesChangedEvent(TimeSeriesEvent.createSeriesChangedEvent(this, getSnapshot()));
        }
    }

    private void queueSeriesChangedEvent(final TimeSeriesEvent e) {
        getSeriesEventExecutor().execute(
            new Runnable() {
                public void run() {
                    logMethods.logDebug("Firing event " + e);
                    timeSeriesListenerSupport.fireTimeSeriesChanged(e);
                    logMethods.logDebug("Finished firing event " + e);
                }
            }
        );
    }

    private void queueItemsAddedEvent(final TimeSeriesEvent e) {
        getSeriesEventExecutor().execute(
            new Runnable() {
                public void run() {
                    logMethods.logDebug("Firing event " + e);
                    timeSeriesListenerSupport.fireItemsAdded(e);
                    logMethods.logDebug("Finished firing event " + e);
                }
            }
        );
    }

    private void queueItemsRemovedEvent(final TimeSeriesEvent e) {
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

    private void queueItemsChangedEvent(final TimeSeriesEvent e) {
        getSeriesEventExecutor().execute(
            new Runnable() {
                public void run() {
                    logMethods.logDebug("Firing event " + e);
                    timeSeriesListenerSupport.fireItemsChanged(e);
                    logMethods.logDebug("Finished firing event " + e);
                }
            }
        );
    }

    //sometimes it is helpful to be able to add items without firing events to listeners.
    //(e.g. this might be as a performance optimization after construction and before any listners
    //have been added.)
    protected synchronized void addAllWithoutFiringEvents(Collection<TimeSeriesItem> c) {
        series.addAll(c);
    }
}
