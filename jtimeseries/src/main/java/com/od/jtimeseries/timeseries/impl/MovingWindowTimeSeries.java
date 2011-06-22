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
import com.od.jtimeseries.util.NamedExecutors;
import com.od.jtimeseries.util.time.FixedTimeSource;
import com.od.jtimeseries.util.time.TimePeriod;
import com.od.jtimeseries.util.time.TimeSource;

import javax.swing.*;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 30/01/11
 * Time: 19:15
 *
 * A TimeSeries which exposes to view only those TimeSeriesItem which fall within an adjustable window of time
 *
 * Although the series will store items with timestamps which fall outside the view period, these are not visible
 * unless the start and end times of the window are adjusted in such a way that they will be included in the view.
 *
 * This behaviour requires some of the usual behaviour of List to be broken -
 * In particular, the methods to add items do no necessarily result in those items being visible via iteration or
 * get() methods, for example, and adding or removing an item may not affect the size(), unless that item falls within
 * the current time window.
 *
 * n.b. List methods to insert or remove items which use an index, interpret the index as the real index on the underlying
 * structure, rather than the index within the current window.
 *
 * The start and end time can be specified as a TimeSource, rather than absolute time value, and the MovingWindowTimeSeries
 * can be set to periodically adjust the window based on the new timestamp values supplied by the TimeSource for each end.
 * This means it is very easy, for example, to define a window which shows only the most recent 10 minutes worth of data,
 * the window being dynamically adjusted as we move on into the future.
 */
public class MovingWindowTimeSeries extends AbstractListTimeSeries implements ModCountList<TimeSeriesItem> {

    private static ScheduledExecutorService scheduledExecutorService = NamedExecutors.newScheduledThreadPool(MovingWindowTimeSeries.class.getSimpleName(), 2);

    public static final TimeSource OPEN_END_TIME = new FixedTimeSource(Long.MAX_VALUE); //ending at the end of the epoch
    public static final TimeSource OPEN_START_TIME = new FixedTimeSource(0);  //starting at the start of the epoch

    //use this privately owned series to store the items in the full series
    //this MovingWindowTimeSeries provides a movable window onto the items on this wrapped series
    private DefaultTimeSeries wrappedTimeSeries = new DefaultTimeSeries();

    private long startTime;
    private int startIndex = -1;
    private TimeSource startTimeSource;
    private long endTime;
    private int endIndex = -1;
    private TimeSource endTimeSource;
    private final AtomicLong modCount = new AtomicLong(0);
    private ScheduledFuture windowCheckFuture;
    private boolean checkWindowInSwingThread;

    public MovingWindowTimeSeries() {
        this(OPEN_START_TIME, OPEN_END_TIME, null);
    }

    public MovingWindowTimeSeries(TimeSource startTimeSource, TimeSource endTimeSource, TimePeriod frequencyToCheckWindow) {
        this.startTimeSource = startTimeSource;
        this.endTimeSource = endTimeSource;
        findStartAndEndAndFireChange(false);
        if ( frequencyToCheckWindow != null) {
            UpdateWindowTask task = new UpdateWindowTask(this);
            windowCheckFuture = scheduledExecutorService.scheduleWithFixedDelay(
                task,
                frequencyToCheckWindow.getLengthInMillis(),
                frequencyToCheckWindow.getLengthInMillis(),
                TimeUnit.MILLISECONDS
            );
            task.setFuture(windowCheckFuture);
        }
    }

    private void findStartAndEndAndFireChange(final boolean forceChangeEvent) {
        Runnable runnable = new Runnable() {
            public void run() {
                synchronized (MovingWindowTimeSeries.this) {
                    long oldStartTime = startTime;
                    long oldEndTime = endTime;
                    int oldStartIndex = startIndex;
                    int oldEndIndex = endIndex;

                    startTime = startTimeSource.getTime();
                    endTime = endTimeSource.getTime();
                    startIndex = wrappedTimeSeries.getIndexOfFirstItemAtOrAfter(startTime);
                    endIndex = wrappedTimeSeries.getIndexOfFirstItemAtOrBefore(endTime);

                    if ( forceChangeEvent || oldStartIndex != startIndex || oldEndIndex != endIndex || oldStartTime != startTime || oldEndTime != endTime ) {
                        //need to do this to invalidate iterators even if no event fired
                        long newModCount = modCount.incrementAndGet();
                        queueSeriesChangedEvent(ListTimeSeriesEvent.createSeriesChangedEvent(MovingWindowTimeSeries.this, getSnapshot(), newModCount ));
                    }
                }
            }
        };
        if ( checkWindowInSwingThread && ! SwingUtilities.isEventDispatchThread()) {
            try {
                SwingUtilities.invokeAndWait(runnable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }  else {
            runnable.run();
        }
    }

    public void setCheckWindowInSwingThread(boolean checkWindowInSwingThread) {
        this.checkWindowInSwingThread = checkWindowInSwingThread;
    }

    public synchronized List<TimeSeriesItem> getSnapshot() {
        return wrappedTimeSeries.getSubSeries(startTime, endTime);
    }

    public synchronized int getIndexOfFirstItemAtOrBefore(long timestamp) {
        int index = wrappedTimeSeries.getIndexOfFirstItemAtOrBefore(timestamp);
        return index >= startIndex && index <= endIndex ? getViewIndex(index) : -1;
    }

    public synchronized int getIndexOfFirstItemAtOrAfter(long timestamp) {
        int index = wrappedTimeSeries.getIndexOfFirstItemAtOrAfter(timestamp);
        return index >= startIndex && index <= endIndex ? getViewIndex(index) : -1;
    }

    public synchronized int size() {
        return startIndex >= 0 && endIndex >= 0 ? (endIndex - startIndex) + 1 : 0;
    }

    public synchronized boolean isEmpty() {
        return size() == 0;
    }

    public synchronized boolean contains(Object o) {
        boolean result = false;
        for ( int index = startIndex; index <= endIndex; index++) {
            if ( wrappedTimeSeries.get(index).equals(o)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public synchronized Iterator<TimeSeriesItem> iterator() {
        return new WindowIterator();
    }

    public synchronized Object[] toArray() {
        return getSnapshot().toArray();
    }

    public synchronized <T> T[] toArray(T[] a) {
        return getSnapshot().toArray(a);
    }

    public synchronized boolean add(TimeSeriesItem timeSeriesItem) {
        boolean added = wrappedTimeSeries.add(timeSeriesItem);
        if (added && isInWindow(timeSeriesItem)) {

            endIndex = wrappedTimeSeries.size() - 1;
            if ( startIndex == -1 ) {
                startIndex = endIndex;
            }

            queueItemsAddedOrInsertedEvent(
                    ListTimeSeriesEvent.createItemsAddedOrInsertedEvent(
                            this,
                            getViewIndex(endIndex),
                            getViewIndex(endIndex),
                            Collections.singletonList(timeSeriesItem),
                            modCount.incrementAndGet()
                    )
            );
        }
        return added;
    }

    public synchronized boolean remove(Object o) {
        int realIndex = wrappedTimeSeries.indexOf(o);
        int viewIndex = getViewIndex(realIndex);
        if ( realIndex != -1) {
            doRemove(realIndex, viewIndex);
        }
        return realIndex != -1;
    }

    public synchronized TimeSeriesItem remove(int realIndex) {
        int viewIndex = getViewIndex(realIndex);
        return doRemove(realIndex, viewIndex);
    }

    private TimeSeriesItem doRemove(int realIndex, int viewIndex) {
        TimeSeriesItem removed = wrappedTimeSeries.remove(realIndex);
        if ( isInWindow(realIndex)) {
            endIndex--;
            if ( endIndex < startIndex ) {
                startIndex = endIndex = -1;
            }

            queueItemsRemovedEvent(
                ListTimeSeriesEvent.createItemsRemovedEvent(
                        this,
                        viewIndex,
                        viewIndex,
                        Collections.singletonList(removed),
                        modCount.incrementAndGet()
                )
            );
        } else if ( realIndex < startIndex ) {
            endIndex--;
            startIndex--;
        }
        return removed;
    }

    public synchronized boolean containsAll(Collection<?> c) {
        return getSnapshot().containsAll(c);
    }

    public synchronized boolean addAll(Collection<? extends TimeSeriesItem> c) {
        int realIndexOfInsert = wrappedTimeSeries.size();
        boolean added = wrappedTimeSeries.addAll(c);
        if ( added ) {
            int firstVisibleInsertIndex = -1;

            List<TimeSeriesItem> inView = new LinkedList<TimeSeriesItem>();
            for (TimeSeriesItem timeSeriesItem : c ) {
                if ( isInWindow(timeSeriesItem)) {
                    endIndex = realIndexOfInsert;
                    if ( startIndex == -1 ) {
                        startIndex = endIndex;
                    }
                    if ( firstVisibleInsertIndex == -1 ) {
                        firstVisibleInsertIndex = endIndex;
                    }
                    inView.add(timeSeriesItem);
                }
                realIndexOfInsert++;
            }

            if ( inView.size() > 0 ) {
                queueItemsAddedOrInsertedEvent(
                    ListTimeSeriesEvent.createItemsAddedOrInsertedEvent(
                        this,
                        getViewIndex(firstVisibleInsertIndex),
                        getViewIndex(endIndex),
                        inView,
                        modCount.incrementAndGet()
                    )
                );
            }
        }

        return added;
    }

    //n.b, the add index is expressed in terms of the wrapped series with the complete set of values,
    //rather than the indexes in the current view window
    public synchronized boolean addAll(int index, Collection<? extends TimeSeriesItem> c) {
        boolean result = wrappedTimeSeries.addAll(index, c);
        //TODO - could be more efficient
        findStartAndEndAndFireChange(false);
        return result;
    }

    public synchronized boolean removeAll(Collection<?> c) {
        boolean result = wrappedTimeSeries.removeAll(c);
        findStartAndEndAndFireChange(false);
        return result;
    }

    public synchronized boolean retainAll(Collection<?> c) {
        boolean result = wrappedTimeSeries.retainAll(c);
        findStartAndEndAndFireChange(false);
        return result;
    }

    public synchronized void clear() {
        wrappedTimeSeries.clear();
        findStartAndEndAndFireChange(false);
    }

    public synchronized TimeSeriesItem get(int index) {
        if (index >= -1 && index < size() ) {
            return wrappedTimeSeries.get(getRealIndex(index));
        } else {
            throw new IndexOutOfBoundsException("Index " + index + " must be between 0 and " + (size() - 1));
        }
    }

    public synchronized TimeSeriesItem set(int index, TimeSeriesItem element) {
        TimeSeriesItem result = wrappedTimeSeries.set(index, element);
        //TODO - could be more efficient
        findStartAndEndAndFireChange(true);
        return result;
    }

    public synchronized void add(int realIndex, TimeSeriesItem item) {
        wrappedTimeSeries.add(realIndex, item);
        //is within current window by index, or just outside by index but within time range
        if ( isInWindow(item)) {
            endIndex++;
            if ( startIndex == -1) {
                startIndex = endIndex;
            }

            int viewIndex = getViewIndex(realIndex);
            queueItemsAddedOrInsertedEvent(
                ListTimeSeriesEvent.createItemsAddedOrInsertedEvent(
                    this,
                    viewIndex,
                    viewIndex,
                    Collections.singletonList(item),
                    modCount.incrementAndGet()
                )
            );
        } else if ( item.getTimestamp() < startTime ) {
            startIndex++;
            endIndex++;
        }
    }

    public synchronized int indexOf(Object o) {
        int realIndex = wrappedTimeSeries.indexOf(o);
        return getViewIndex(realIndex);
    }

    public synchronized int lastIndexOf(Object o) {
        int realIndex = wrappedTimeSeries.lastIndexOf(o);
        return getViewIndex(realIndex);
    }

    public synchronized ListIterator<TimeSeriesItem> listIterator() {
        return new WindowIterator();
    }

    public synchronized ListIterator<TimeSeriesItem> listIterator(int index) {
        return new WindowIterator(index);
    }

    public synchronized List<TimeSeriesItem> subList(int fromIndex, int toIndex) {
        if ( fromIndex < 0 || toIndex > size() || fromIndex > toIndex) {
            throw new ArrayIndexOutOfBoundsException();
        } else {
            return new ModCountSubList<TimeSeriesItem>(new ModCountWrappedList(), getRealIndex(fromIndex), getRealIndex(toIndex));
        }
    }

    public synchronized boolean prepend(TimeSeriesItem item) {
        boolean result = item.getTimestamp() <= wrappedTimeSeries.getEarliestTimestamp();
        if ( result ) {
            add(0, item);
        }
        return result;
    }

    public synchronized boolean append(TimeSeriesItem item) {
        boolean result = item.getTimestamp() >= wrappedTimeSeries.getLatestTimestamp();
        if ( result ) {
            add(item);
        }
        return result;
    }

    public synchronized ListTimeSeries getSubSeries(long timestamp) {
        long start = Math.max(timestamp, startTime);
        return wrappedTimeSeries.getSubSeries(start, endTime);
    }

    public synchronized ListTimeSeries getSubSeries(long startTimestamp, long endTimestamp) {
        long start = Math.max(startTimestamp, startTime);
        long end = Math.min(endTimestamp, endTime);
        return wrappedTimeSeries.getSubSeries(start, end);
    }

    public synchronized TimeSeriesItem getEarliestItem() {
        return isEmpty() ? null : wrappedTimeSeries.get(startIndex);
    }

    public synchronized TimeSeriesItem getLatestItem() {
        return isEmpty() ? null : wrappedTimeSeries.get(endIndex);
    }

    /**
     * as do all methods which add/remove data, this operates on the complete series, and will remove the
     * earliest from the wrapped series not the current window
     */
    public synchronized TimeSeriesItem removeEarliestItem() {
        return wrappedTimeSeries.size() > 0 ? remove(0) : null;
    }

    /**
     * as do all methods which add/remove data, this operates on the complete series, and will remove the
     * earliest from the wrapped series not the current window
     */
    public synchronized TimeSeriesItem removeLatestItem() {
        return wrappedTimeSeries.size() > 0 ? remove(wrappedTimeSeries.size() - 1) : null;
    }

    public synchronized long getEarliestTimestamp() {
        return isEmpty() ? -1 : getEarliestItem().getTimestamp();
    }

    public synchronized long getLatestTimestamp() {
        return isEmpty() ? -1 : getLatestItem().getTimestamp();
    }

    public synchronized TimeSeriesItem getFirstItemAtOrBefore(long timestamp) {
        TimeSeriesItem i = wrappedTimeSeries.getFirstItemAtOrBefore(timestamp);
        return i == null ? null : isInWindow(i) ? i : null;
    }

    public synchronized TimeSeriesItem getFirstItemAtOrAfter(long timestamp) {
        TimeSeriesItem i = wrappedTimeSeries.getFirstItemAtOrAfter(timestamp);
        return i == null ? null : isInWindow(i) ? i : null;
    }

    public synchronized long getTimestampAfter(long timestamp) {
        long time = wrappedTimeSeries.getTimestampAfter(timestamp);
        return time == -1 ? -1 : isTimestampInWindow(time) ? time : -1;
    }

    public synchronized long getTimestampBefore(long timestamp) {
        long time = wrappedTimeSeries.getTimestampBefore(timestamp);
        return time == -1 ? -1 : isTimestampInWindow(time) ? time : -1;    }

    public synchronized void addTimeSeriesListener(TimeSeriesListener l) {
        //we manage our own listeners and events, don't delegate this
        //to the wrapped series
        super.addTimeSeriesListener(l);
    }

    public synchronized void removeTimeSeriesListener(TimeSeriesListener l) {
        //we manage our own listeners and events, don't delegate this
        //to the wrapped series
        super.removeTimeSeriesListener(l);
    }

    //the apparent modCount of the window/view
    public synchronized long getModCount() {
        return modCount.get();
    }

    public synchronized TimeSource getEndTime() {
        return endTimeSource;
    }

    public synchronized void setEndTime(TimeSource endTimeSource) {
        this.endTimeSource = endTimeSource;
        findStartAndEndAndFireChange(false);
    }

    public synchronized TimeSource getStartTime() {
        return startTimeSource;
    }

    public synchronized void setStartTime(TimeSource startTimeSource) {
        this.startTimeSource = startTimeSource;
        findStartAndEndAndFireChange(false);
    }

    public synchronized void setStartTime(long startTime) {
        setStartTime(new FixedTimeSource(startTime));
    }

    public synchronized void setEndTime(long endTime) {
        setEndTime(new FixedTimeSource(endTime));
    }

    /**
     * Changing this data in any way may corrupt the windowed view
     * @return the underlying series data for this windowed time series
     */
    public TimeSeries getWrappedSeries() {
        return wrappedTimeSeries;
    }

    public synchronized boolean equals(Object o) {
        if (this == o) return true;
        return subList(0, size()).equals(o);
    }

    //from abstractList
    public synchronized int hashCode() {
        int hashCode = 1;
        Iterator i = iterator();
            while (i.hasNext()) {
            Object obj = i.next();
            hashCode = 31*hashCode + (obj==null ? 0 : obj.hashCode());
        }
        return hashCode;
    }

    //an iterator backed by the wrapped series, which exposes only the items currently in the window
    private class WindowIterator implements ListIterator<TimeSeriesItem> {

        private final ListIterator<TimeSeriesItem> i;

        public WindowIterator() {
            this.i = new ModCountListIterator<TimeSeriesItem>(new ModCountWrappedList(), startIndex);
        }

        public WindowIterator(int viewIndex) {
            this.i = new ModCountListIterator<TimeSeriesItem>(new ModCountWrappedList(), getRealIndex(viewIndex));
        }

        public boolean hasNext() {
            return i.hasNext() && i.nextIndex() <= endIndex;
        }

        public TimeSeriesItem next() {
            if ( ! hasNext()) {
                throw new NoSuchElementException("No next element");
            }
            return i.next();
        }

        public boolean hasPrevious() {
            return i.hasPrevious() && i.previousIndex() >= startIndex;
        }

        public TimeSeriesItem previous() {
            if ( ! hasPrevious()) {
                throw new NoSuchElementException("No previous element");
            }
            return i.previous();
        }

        public int nextIndex() {
            if (! hasNext()) {
                return size();
            }
            return getViewIndex(i.nextIndex());
        }

        public int previousIndex() {
            if (! hasPrevious()) {
                return -1;
            }
            return getViewIndex(i.previousIndex());
        }

        public void remove() {
            i.remove();
        }

        public void set(TimeSeriesItem item) {
            i.set(item);
        }

        public void add(TimeSeriesItem item) {
            i.add(item);
        }
    }

    //expose the wrapped series data to ModCountListIterator iterator
    //the add/remove methods which modify the data use the local moving series
    //add/remove methods so that events are fired and local start and end is updated
    //Only the methods required by ModCountListIterator are supported
    private class ModCountWrappedList implements ModCountList<TimeSeriesItem> {

        public long getModCount() {
            return MovingWindowTimeSeries.this.getModCount();
        }

        public int size() {
            return wrappedTimeSeries.size();
        }

        public boolean isEmpty() {
            throw new UnsupportedOperationException();
        }

        public boolean contains(Object o) {
            throw new UnsupportedOperationException();
        }

        public Iterator<TimeSeriesItem> iterator() {
            throw new UnsupportedOperationException();
        }

        public Object[] toArray() {
            throw new UnsupportedOperationException();
        }

        public <T> T[] toArray(T[] a) {
            throw new UnsupportedOperationException();
        }

        public boolean add(TimeSeriesItem item) {
            throw new UnsupportedOperationException();
        }

        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        public boolean containsAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        public boolean addAll(Collection<? extends TimeSeriesItem> c) {
            throw new UnsupportedOperationException();
        }

        public boolean addAll(int index, Collection<? extends TimeSeriesItem> c) {
            return MovingWindowTimeSeries.this.addAll(index, c);
        }

        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        public void clear() {
            throw new UnsupportedOperationException();
        }

        public boolean equals(Object o) {
            throw new UnsupportedOperationException();
        }

        public int hashCode() {
            throw new UnsupportedOperationException();
        }

        public TimeSeriesItem get(int index) {
            return wrappedTimeSeries.get(index);
        }

        //use the local set, to fire the events from moving series
        public TimeSeriesItem set(int index, TimeSeriesItem element) {
            return MovingWindowTimeSeries.this.set(index, element);
        }

        //use the local add, to fire the events from moving series
        public void add(int index, TimeSeriesItem element) {
            MovingWindowTimeSeries.this.add(index, element);
        }

        //use the local remove, to fire the events from moving series
        public TimeSeriesItem remove(int index) {
            return MovingWindowTimeSeries.this.remove(index);
        }

        public int indexOf(Object o) {
            throw new UnsupportedOperationException();
        }

        public int lastIndexOf(Object o) {
            throw new UnsupportedOperationException();
        }

        public ListIterator<TimeSeriesItem> listIterator() {
            throw new UnsupportedOperationException();
        }

        public ListIterator<TimeSeriesItem> listIterator(int index) {
            return new WindowIterator(getViewIndex(index)) {
                public int nextIndex() {
                    return getRealIndex(super.nextIndex());
                }

                public int previousIndex() {
                    return getRealIndex(super.previousIndex());
                }
            };
        }

        public List<TimeSeriesItem> subList(int fromIndex, int toIndex) {
            throw new UnsupportedOperationException();
        }
    }

    private boolean isTimestampInWindow(long timestamp) {
        return timestamp >= startTime && timestamp <= endTime;
    }

    private boolean isInWindow(int realIndex) {
        return realIndex >= startIndex && realIndex <= endIndex;
    }

    private boolean isInWindow(TimeSeriesItem item) {
        long timeStamp = item.getTimestamp();
        return timeStamp >= startTime && timeStamp <= endTime;
    }

    private int getViewIndex(int realIndex) {
        return realIndex == -1 ? -1 :
            isInWindow(realIndex) ?  realIndex - startIndex : -1;
    }

    private int getRealIndex(int viewIndex) {
        return viewIndex + startIndex;
    }

    private static class UpdateWindowTask implements Runnable {

        private WeakReference<MovingWindowTimeSeries> series;
        private volatile ScheduledFuture future;

        public UpdateWindowTask(MovingWindowTimeSeries s) {
            this.series = new WeakReference<MovingWindowTimeSeries>(s);
        }

        public void setFuture(ScheduledFuture future) {
            this.future = future;
        }

        public void run() {
            final MovingWindowTimeSeries s = series.get();
            if ( s != null ) {
               s.findStartAndEndAndFireChange(false);
            } else {
                if ( future != null ) {
                    future.cancel(false);
                } else {
                    //throwing an exception should also cancel the task
                    throw new RuntimeException("Cannot find future to cancel task");
                }
            }
        }
    }
}
