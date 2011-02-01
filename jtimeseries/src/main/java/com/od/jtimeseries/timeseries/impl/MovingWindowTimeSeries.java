package com.od.jtimeseries.timeseries.impl;

import com.od.jtimeseries.timeseries.*;
import com.od.jtimeseries.util.NamedExecutors;
import com.od.jtimeseries.util.time.FixedTimeSource;
import com.od.jtimeseries.util.time.Time;
import com.od.jtimeseries.util.time.TimePeriod;
import com.od.jtimeseries.util.time.TimeSource;

import javax.naming.OperationNotSupportedException;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
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
    private TimePeriod frequencyToCheckWindow;
    private final AtomicLong modCount = new AtomicLong(0);

    public MovingWindowTimeSeries() {
        this(OPEN_START_TIME, OPEN_END_TIME, Time.seconds(10));
    }

    public MovingWindowTimeSeries(TimeSource startTimeSource, TimeSource endTimeSource, TimePeriod frequencyToCheckWindow) {
        this.startTimeSource = startTimeSource;
        this.endTimeSource = endTimeSource;
        this.frequencyToCheckWindow = frequencyToCheckWindow;
        findStartAndEndAndFireChange(false);
    }

    private synchronized void findStartAndEndAndFireChange(boolean forceChangeEvent) {
        startTime = startTimeSource.getTime();
        endTime = endTimeSource.getTime();

        int oldStartIndex = startIndex;
        int oldEndIndex = endIndex;

        startIndex = wrappedTimeSeries.getIndexOfFirstItemAtOrAfter(startTime);
        endIndex = wrappedTimeSeries.getIndexOfFirstItemAtOrBefore(endTime);

        if ( oldStartIndex != startIndex || oldEndIndex != endIndex) {
            queueSeriesChangedEvent(ListTimeSeriesEvent.createSeriesChangedEvent(this, getSnapshot(), modCount.incrementAndGet() ));
        }
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

    public boolean remove(Object o) {
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

    public TimeSeriesItem set(int index, TimeSeriesItem element) {
        TimeSeriesItem result = wrappedTimeSeries.set(index, element);
        //TODO - could be more efficient
        findStartAndEndAndFireChange(true);
        return result;
    }

    public void add(int realIndex, TimeSeriesItem item) {
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

    public int indexOf(Object o) {
        int realIndex = wrappedTimeSeries.indexOf(o);
        return getViewIndex(realIndex);
    }

    public int lastIndexOf(Object o) {
        int realIndex = wrappedTimeSeries.lastIndexOf(o);
        return getViewIndex(realIndex);
    }

    public ListIterator<TimeSeriesItem> listIterator() {
        return new WindowIterator();
    }

    public ListIterator<TimeSeriesItem> listIterator(int index) {
        return new WindowIterator(index);
    }

    public List<TimeSeriesItem> subList(int fromIndex, int toIndex) {
        if ( fromIndex < 0 || toIndex > size() || fromIndex > toIndex) {
            throw new ArrayIndexOutOfBoundsException();
        } else {
            return new ModCountSubList<TimeSeriesItem>(new ModCountWrappedList(), getRealIndex(fromIndex), getRealIndex(toIndex));
        }
    }

    public boolean prepend(TimeSeriesItem item) {
        return wrappedTimeSeries.prepend(item);
    }

    public boolean append(TimeSeriesItem value) {
        return wrappedTimeSeries.append(value);
    }

    public ListTimeSeries getSubSeries(long timestamp) {
        return wrappedTimeSeries.getSubSeries(timestamp);
    }

    public ListTimeSeries getSubSeries(long startTimestamp, long endTimestamp) {
        return wrappedTimeSeries.getSubSeries(startTimestamp, endTimestamp);
    }

    public TimeSeriesItem getEarliestItem() {
        return wrappedTimeSeries.getEarliestItem();
    }

    public TimeSeriesItem getLatestItem() {
        return wrappedTimeSeries.getLatestItem();
    }

    public TimeSeriesItem removeEarliestItem() {
        return wrappedTimeSeries.removeEarliestItem();
    }

    public TimeSeriesItem removeLatestItem() {
        return wrappedTimeSeries.removeLatestItem();
    }

    public long getEarliestTimestamp() {
        return wrappedTimeSeries.getEarliestTimestamp();
    }

    public long getLatestTimestamp() {
        return wrappedTimeSeries.getLatestTimestamp();
    }

    public TimeSeriesItem getFirstItemAtOrBefore(long timestamp) {
        return wrappedTimeSeries.getFirstItemAtOrBefore(timestamp);
    }

    public TimeSeriesItem getFirstItemAtOrAfter(long timestamp) {
        return wrappedTimeSeries.getFirstItemAtOrAfter(timestamp);
    }

    public long getTimestampAfter(long timestamp) {
        return wrappedTimeSeries.getTimestampAfter(timestamp);
    }

    public long getTimestampBefore(long timestamp) {
        return wrappedTimeSeries.getTimestampBefore(timestamp);
    }

    public void addTimeSeriesListener(TimeSeriesListener l) {
        //we manage our own listeners and events, don't delegate this
        //to the wrapped series
        super.addTimeSeriesListener(l);
    }

    public void removeTimeSeriesListener(TimeSeriesListener l) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        MovingWindowTimeSeries that = (MovingWindowTimeSeries) o;

        if (endTimeSource != null ? !endTimeSource.equals(that.endTimeSource) : that.endTimeSource != null)
            return false;
        if (frequencyToCheckWindow != null ? !frequencyToCheckWindow.equals(that.frequencyToCheckWindow) : that.frequencyToCheckWindow != null)
            return false;
        if (startTimeSource != null ? !startTimeSource.equals(that.startTimeSource) : that.startTimeSource != null)
            return false;
        if (wrappedTimeSeries != null ? !wrappedTimeSeries.equals(that.wrappedTimeSeries) : that.wrappedTimeSeries != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (wrappedTimeSeries != null ? wrappedTimeSeries.hashCode() : 0);
        result = 31 * result + (startTimeSource != null ? startTimeSource.hashCode() : 0);
        result = 31 * result + (endTimeSource != null ? endTimeSource.hashCode() : 0);
        result = 31 * result + (frequencyToCheckWindow != null ? frequencyToCheckWindow.hashCode() : 0);
        return result;
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
            return wrappedTimeSeries.getModCount();
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
}
