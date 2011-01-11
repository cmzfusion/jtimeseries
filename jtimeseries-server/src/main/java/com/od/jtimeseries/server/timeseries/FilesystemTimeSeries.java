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
package com.od.jtimeseries.server.timeseries;

import com.od.jtimeseries.server.serialization.FileHeader;
import com.od.jtimeseries.server.serialization.RoundRobinSerializer;
import com.od.jtimeseries.server.serialization.SerializationException;
import com.od.jtimeseries.timeseries.*;
import com.od.jtimeseries.timeseries.impl.RoundRobinTimeSeries;
import com.od.jtimeseries.timeseries.impl.TimeSeriesOrderingException;
import com.od.jtimeseries.timeseries.impl.WrappedTimeSeriesEventHandler;
import com.od.jtimeseries.util.NamedExecutors;
import com.od.jtimeseries.util.TimeSeriesExecutorFactory;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.identifiable.IdentifiableBase;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.util.time.TimePeriod;

import java.lang.ref.SoftReference;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18-May-2009
 * Time: 13:10:01
 *
 * A series which represents time series data stored on disk.
 * The actual serialization is carried out by RoundRobinSerializer
 *
 * TimeSeriesItem appended using s.append() can be stored in a local cache, and a delayed write performed, to avoid having
 * to keep all the data for the timeseries in memory at all times. It is expected that the number of appends will vastly
 * outnumber all other operations. Other operations (e.g. iterator) in general require the time series to be deserialized).
 *
 * TODO - getLatestItem should probably make use of the cached values where available, instead of kicking off
 * deserialization. There may be other functions we can optimize in this way but need to add tests when we do it.
 */
public class FilesystemTimeSeries extends IdentifiableBase implements IdentifiableTimeSeries, ListTimeSeries {

    private static final LogMethods logMethods = LogUtils.getLogMethods(FilesystemTimeSeries.class);

    private static ScheduledExecutorService clearCacheExecutor = NamedExecutors.newSingleThreadScheduledExecutor("FilesystemTimeSeriesClearCache");
    private Executor eventExecutor = TimeSeriesExecutorFactory.getExecutorForTimeSeriesEvents(this);
    private SoftReference<RoundRobinTimeSeries> softSeriesReference = new SoftReference<RoundRobinTimeSeries>(null);
    private RoundRobinSerializer roundRobinSerializer;
    private TimePeriod appendPeriod;
    private TimePeriod rewritePeriod;
    private FileHeader fileHeader;
    private WrappedTimeSeriesEventHandler timeSeriesEventHandler = new WrappedTimeSeriesEventHandler(this);
    private WriteBehindCache writeBehindCache;
    private long lastTimestamp = -1;
    private ScheduledFuture nextFlushTask;
    private volatile boolean persistenceStopped = false;

    public FilesystemTimeSeries(Identifiable parentContext, String id, String description, RoundRobinSerializer roundRobinSerializer, int seriesLength, TimePeriod appendPeriod, TimePeriod rewritePeriod) throws SerializationException {
        super(parentContext, id, description);
        this.roundRobinSerializer = roundRobinSerializer;
        this.appendPeriod = appendPeriod;
        this.rewritePeriod = rewritePeriod;
        this.fileHeader = new FileHeader(getPath(), description, seriesLength);
        checkOrCreateFileAndUpdateHeader(fileHeader);
        this.writeBehindCache = new WriteBehindCache();
    }

    private void checkOrCreateFileAndUpdateHeader(FileHeader fileHeader) throws SerializationException {
        if ( roundRobinSerializer.fileExists(fileHeader) ) {
            roundRobinSerializer.updateHeader(fileHeader);
        } else {
            roundRobinSerializer.createFile(fileHeader);
        }
        lastTimestamp = fileHeader.getMostRecentItemTimestamp();
    }

    public synchronized boolean add(TimeSeriesItem i) {
        boolean result = doAppend(i);
        if ( ! result ) {
            throw new TimeSeriesOrderingException(size(), i.getTimestamp());
        } else {
            return true;
        }
    }

    /**
     * Stop persistence for this Filesystem times series
     */
    public synchronized void stopPersistence() {
        persistenceStopped = true;
    }

    /**
     * @return is persistence stopped
     */
    public boolean isPersistenceStopped() {
        return persistenceStopped;
    }

    /**
     * Note, appending data does not require deserialization of the filesystem timeseries, thus preventing the
     * peformance overhead of having to deserialize to add items. If the series is not already in memory we will append
     * the item to our write behind cache only (eventually the item will get persisted, depending on the cache flush
     * scheduling)
     */
    public synchronized boolean append(TimeSeriesItem i) {
        return doAppend(i);
    }

    private boolean doAppend(final TimeSeriesItem i) {
        //if the round robin series is in memory, we add the item to the series, but we also add the item to cache's list
        //this is because the round robin series is our primary source for the series data while in memory, but it is soft
        //referenced and it may be gc'd at any time. If this happens we still need a record of the items which were added
        //since it was last persisted.
        boolean result = false;
        if ( i.getTimestamp() >= lastTimestamp) {
            writeBehindCache.addItemForAppend(i);
            result = true;
            RoundRobinTimeSeries s = getRoundRobinSeries(false);
            if ( s != null) {
                s.append(i);
            } else {
                //the series has been collected, along with listener registrations, so we need to fire event ourselves
                fireAddEvent(i);
            }
            lastTimestamp = i.getTimestamp();
        }
        return result;
    }

    private void fireAddEvent(final TimeSeriesItem i) {
        final TimeSeriesEvent e = TimeSeriesEvent.createItemsAddedEvent(
                FilesystemTimeSeries.this, size() -1, size() -1, Collections.singletonList(i)
        );

        eventExecutor.execute(new Runnable() {
            public void run() {
                timeSeriesEventHandler.fireItemsAdded(e);
            }
        });
    }


    public synchronized void add(int index, TimeSeriesItem i) {
        RoundRobinTimeSeries r = getRoundRobinSeries();
        r.add(index, i);
        writeBehindCache.cacheSeriesForRewrite(r);
        lastTimestamp = r.getLatestTimestamp();
    }

    public synchronized boolean addAll(int index, Collection<? extends TimeSeriesItem> i) {
        RoundRobinTimeSeries r = getRoundRobinSeries();
        boolean change = r.addAll(index, i);
        if ( change ) {
            writeBehindCache.cacheSeriesForRewrite(r);
        }
        lastTimestamp = r.getLatestTimestamp();
        return change;
    }

    public synchronized boolean addAll(Collection<? extends TimeSeriesItem> i) {
        RoundRobinTimeSeries r = getRoundRobinSeries();
        boolean change = r.addAll(i);
        if ( change ) {
            writeBehindCache.cacheSeriesForRewrite(r);
        }
        lastTimestamp = r.getLatestTimestamp();
        return change;
    }

    public synchronized boolean prepend(TimeSeriesItem i) {
        RoundRobinTimeSeries r = getRoundRobinSeries();
        boolean change = r.prepend(i);
        if ( change ) {
            writeBehindCache.cacheSeriesForRewrite(r);
        }
        return change;
    }

    public synchronized int getMaxSize() {
        return fileHeader.getSeriesLength();
    }

    public synchronized List<TimeSeriesItem> getSnapshot() {
        return getRoundRobinSeries().getSnapshot();
    }

    public synchronized TimeSeriesItem getLatestItem() {
        return getRoundRobinSeries().getLatestItem();
    }

    public long getLatestTimestamp() {
        return lastTimestamp;
    }

    public synchronized TimeSeriesItem removeLatestItem() {
        RoundRobinTimeSeries r = getRoundRobinSeries();
        TimeSeriesItem i = r.removeLatestItem();
        if ( i != null ) {
            writeBehindCache.cacheSeriesForRewrite(r);
        }
        lastTimestamp = r.getLatestTimestamp();
        return i;
    }

    public synchronized TimeSeriesItem getEarliestItem() {
        return getRoundRobinSeries().getEarliestItem();
    }

    public synchronized long getEarliestTimestamp() {
        return getRoundRobinSeries().getEarliestTimestamp();
    }

    public synchronized TimeSeriesItem removeEarliestItem() {
        RoundRobinTimeSeries r = getRoundRobinSeries();
        TimeSeriesItem i = r.removeEarliestItem();
        if ( i != null ) {
            writeBehindCache.cacheSeriesForRewrite(r);
        }
        lastTimestamp = r.getLatestTimestamp();
        return i;
    }

    public synchronized boolean isEmpty() {
        return size() == 0;
    }

    public synchronized int size() {
        RoundRobinTimeSeries r = getRoundRobinSeries(false);
        if ( r != null ) {
            return r.size();
        } else {
            //when the cache is flushed, if the cache contains more items than can fit in the series, we will lose the earlist due to round robin
            //so we will end up with maxSize items in the series
            return Math.min(getMaxSize(), fileHeader.getCurrentSize() + writeBehindCache.getAppendItems().size());
        }
    }

    public synchronized TimeSeriesItem get(int index) {
        return getRoundRobinSeries().get(index);
    }

    public synchronized void clear() {
        RoundRobinTimeSeries r = getRoundRobinSeries();
        r.clear();
        lastTimestamp = -1;
        writeBehindCache.cacheSeriesForRewrite(r);
    }

    public synchronized List<TimeSeriesItem> subList(int fromIndex, int toIndex) {
        return getRoundRobinSeries().subList(fromIndex, toIndex);
    }

    public synchronized ListIterator<TimeSeriesItem> listIterator() {
        return getRoundRobinSeries().listIterator();
    }

    public synchronized ListIterator<TimeSeriesItem> listIterator(int index) {
        return getRoundRobinSeries().listIterator(index);
    }

    public synchronized int lastIndexOf(Object o) {
        return getRoundRobinSeries().lastIndexOf(o);
    }

    public synchronized int indexOf(Object o) {
        return getRoundRobinSeries().indexOf(o);
    }

    public synchronized boolean remove(Object o) {
        RoundRobinTimeSeries r = getRoundRobinSeries();
        boolean change = r.remove(o);
        if ( change ) {
            writeBehindCache.cacheSeriesForRewrite(r);
        }
        lastTimestamp = r.getLatestTimestamp();
        return change;
    }

    public synchronized TimeSeriesItem remove(int index) {
        RoundRobinTimeSeries r = getRoundRobinSeries();
        TimeSeriesItem i = r.remove(index);
        if ( i != null ) {
            writeBehindCache.cacheSeriesForRewrite(r);
        }
        lastTimestamp = r.getLatestTimestamp();
        return i;
    }

    public synchronized TimeSeriesItem set(int index, TimeSeriesItem item) {
        RoundRobinTimeSeries r = getRoundRobinSeries();
        TimeSeriesItem i = r.set(index, item);
        if ( i != null ) {
            writeBehindCache.cacheSeriesForRewrite(r);
        }
        lastTimestamp = r.getLatestTimestamp();
        return i;
    }

    public synchronized boolean retainAll(Collection<?> c) {
        RoundRobinTimeSeries r = getRoundRobinSeries();
        boolean change = r.retainAll(c);
        if ( change ) {
            writeBehindCache.cacheSeriesForRewrite(r);
        }
        lastTimestamp = r.getLatestTimestamp();
        return change;
    }

    public synchronized boolean removeAll(Collection<?> c) {
        RoundRobinTimeSeries r = getRoundRobinSeries();
        boolean change = r.removeAll(c);
        if ( change ) {
            writeBehindCache.cacheSeriesForRewrite(r);
        }
        lastTimestamp = r.getLatestTimestamp();
        return change;
    }

    public synchronized boolean containsAll(Collection<?> c) {
        return getRoundRobinSeries().containsAll(c);
    }

    public synchronized Object[] toArray() {
        return getRoundRobinSeries().toArray();
    }

    public synchronized <T> T[] toArray(T[] a) {
        return getRoundRobinSeries().toArray(a);
    }

    public synchronized Iterator<TimeSeriesItem> iterator() {
        return getRoundRobinSeries().iterator();
    }

    public synchronized boolean contains(Object o) {
        return getRoundRobinSeries().contains(o);
    }

    public synchronized boolean equals(Object o) {
        return getRoundRobinSeries().equals(o);
    }

    public synchronized int hashCode() {
        //it may seem a bit odd to deserialize for this, but a ListTimeseries is a List of TimeSeriesItem, and the
        //contract of List requires us to base hashcode on each element. In practise, it is unlikely that we will
        //need to compute a hashcode for FilesystemTimeSeries frequently
        return getRoundRobinSeries().hashCode();
    }

    public synchronized TimeSeries getSubSeries(long startTimestamp, long endTimestamp) {
        return getRoundRobinSeries().getSubSeries(startTimestamp, endTimestamp);
    }

    public synchronized TimeSeries getSubSeries(long timestamp) {
        TimeSeries result;
        //we can try to satisfy this from the list of append items in memory if
        //possible, to avoid having to deserialize the series every tome
        //for common 'most recent values' queries which result from polling
        if ( writeBehindCache.getAppendItems().size() > 0 && writeBehindCache.getAppendItems().getEarliestTimestamp() < timestamp) {
            result = writeBehindCache.getAppendItems().getSubSeries(timestamp);
        } else {
            result = getRoundRobinSeries().getSubSeries(timestamp);
        }
        return result;
    }

    public synchronized long getTimestampAfter(long timestamp) {
        return getRoundRobinSeries().getTimestampAfter(timestamp);
    }

    public synchronized long getTimestampBefore(long timestamp) {
        return getRoundRobinSeries().getTimestampBefore(timestamp);
    }

    public synchronized int getIndexOfFirstItemAtOrAfter(long timestamp) {
        return getRoundRobinSeries().getIndexOfFirstItemAtOrAfter(timestamp);
    }

    public synchronized int getIndexOfFirstItemAtOrBefore(long timestamp) {
        return getRoundRobinSeries().getIndexOfFirstItemAtOrBefore(timestamp);
    }

    public synchronized TimeSeriesItem getFirstItemAtOrBefore(long timestamp) {
        return getRoundRobinSeries().getFirstItemAtOrBefore(timestamp);
    }

    public synchronized TimeSeriesItem getFirstItemAtOrAfter(long timestamp) {
        return getRoundRobinSeries().getFirstItemAtOrAfter(timestamp);
    }

    public FileHeader getFileHeader() {
        return fileHeader;
    }

    public void addTimeSeriesListener(TimeSeriesListener l) {
        timeSeriesEventHandler.addTimeSeriesListener(l);
    }

    public void removeTimeSeriesListener(TimeSeriesListener l) {
        timeSeriesEventHandler.removeTimeSeriesListener(l);
    }

    private RoundRobinTimeSeries getRoundRobinSeries() {
        return getRoundRobinSeries(true);
    }

    private RoundRobinTimeSeries getRoundRobinSeries(boolean deserializeIfRequired) {
        RoundRobinTimeSeries s = isSeriesInWriteCache() ? writeBehindCache.getSeries() : softSeriesReference.get();
        if ( s == null && deserializeIfRequired ) {
            try {
                s = roundRobinSerializer.deserialize(fileHeader);

                //there may be items in our cache we need to add to
                //bring the filesystem series into sync. We will have to call a special
                //method to add the items in the local cache to the deserialized series without firing
                //events. This is because once it is in memory we are adding a listener to the deserialized series
                //to propagate events to our listeners whenever an item is added, and events for the items in the local
                //cache have already been fired to our listeners.
                //Due to the asynchronous event firing, when we call s.add() we could end
                //up firing duplicate events when we add these items, even though our propagating listener has not yet
                //been added - we will probably not receive the events back until the listener has been added.
                s.addWithoutFiringEvent(writeBehindCache.getAppendItems());
                //nb. the items stay in the cache append list, so we keep track that we still haven't written them to disk

                s.addTimeSeriesListener(timeSeriesEventHandler);
                softSeriesReference = new SoftReference<RoundRobinTimeSeries>(s);
            } catch (SerializationException e) {
                throw new RuntimeException("Could not load timeseries values", e);
            }
        }
        return s;
    }

    /**
     * Holds the data which has changed, which can either be the most recently appended items
     * or a reference to the whole round robin series for changes which were not appends.
     * In either case, the wrapped round robin series in local memory should have been updated
     * already to reflect the changes - the cache just represents what we need to write to the
     * filesystem to bring the fs up to date.
     *
     * In the case we're holding onto a list of appended items, the wrapped series is free to be
     * reclaimed via the SoftReference (the list contains all the deltas in this case - we have no need
     * to rewrite the whole series, and there's no urgency to flush the cache.)
     * If other changes to the wrapped series have been made (not just appends) we hold a reference
     * to the whole series to prevent it being collected, and add a task to try to bring forward the
     * cache flush operation.
     */
    private class WriteBehindCache {

        //when changes to series are not just appends, keep the whole series in memory until flush by holding this reference
        private RoundRobinTimeSeries roundRobinSeries;

        //there is never any point in appending more items than the max series size, so use a round robin series to store until flush
        private RoundRobinTimeSeries itemsToAppend = new RoundRobinTimeSeries(getMaxSize());

        public void cacheSeriesForRewrite(RoundRobinTimeSeries roundRobinSeries) {
            this.roundRobinSeries = roundRobinSeries;
            itemsToAppend.clear(); //clear the append items list, we don't need it, we will now rewrite the whole series instead
            scheduleFlushCacheTask(rewritePeriod.getLengthInMillis());
        }

        public void addItemForAppend(TimeSeriesItem timeSeriesItem) {
            if ( roundRobinSeries == null) { //only if we are not already going to rewrite the whole series
                itemsToAppend.add(timeSeriesItem);
                scheduleFlushCacheTask(appendPeriod.getLengthInMillis());
            }
        }

        public TimeSeries getAppendItems() {
            return itemsToAppend;
        }

        public void flush() {
            if ( ! persistenceStopped) {
                try {
                    if ( roundRobinSeries != null) {
                        //we have a local series which contains other changes, as well as possibly some appends
                        roundRobinSerializer.serialize(fileHeader, roundRobinSeries);
                    } else {
                        //only changes are appends
                        roundRobinSerializer.append(fileHeader, itemsToAppend);
                    }

                    //clear cache if no exception / write succeeded
                    //otherwise hold on to changes until we try the write again
                    clearCache();

                } catch (Throwable t) {
                    logMethods.logError("Failed to write to timeseries file " + fileHeader + ", cannot bring this series up to date, I'll keep trying");
                    logMethods.logDebug("Failed to write to timeseries file " + fileHeader, t);
                    scheduleFlushCacheTask(appendPeriod.getLengthInMillis());
                }
            }
        }

        private void clearCache() {
            roundRobinSeries = null;
            itemsToAppend.clear();
        }

        private boolean isFlushed() {
            return roundRobinSeries == null && itemsToAppend.size() == 0;
        }

        private boolean isSeriesInCache() {
            return roundRobinSeries != null;
        }

        private int getAppendListSize() {
            return itemsToAppend.size();
        }

        public RoundRobinTimeSeries getSeries() {
            return roundRobinSeries;
        }
    }

    private void scheduleFlushCacheTask(long delayMillis) {
        //cancel the next flush, and schedule a new one sooner
        if ( nextFlushTask == null || nextFlushTask.isDone())  {
            scheduleNewTask(delayMillis);
        } else if ( nextFlushTask.getDelay(TimeUnit.MILLISECONDS) > delayMillis ) {
            //bring forward by cancelling and scheduling a new task
            nextFlushTask.cancel(false);
            scheduleNewTask(delayMillis);
        }
    }

    private void scheduleNewTask(long delayMillis) {
        nextFlushTask = clearCacheExecutor.schedule(
            new Runnable() {
                public void run() {
                    synchronized( FilesystemTimeSeries.this) {
                        writeBehindCache.flush();
                    }
                }
            },
            delayMillis,
            TimeUnit.MILLISECONDS
        );
    }


    //testing hook
    public synchronized void flush() {
        writeBehindCache.flush();
    }

    //testing hook
    synchronized boolean isCacheFlushed() {
        return writeBehindCache.isFlushed();
    }

    synchronized boolean isSeriesInWriteCache() {
        return writeBehindCache.isSeriesInCache();
    }

    synchronized int getCacheAppendListSize() {
        return writeBehindCache.getAppendListSize();
    }

    //testing hook, trigger the garbage collection of the soft referenced series
    synchronized void triggerGarbageCollection() {
        softSeriesReference.clear();
    }

    //testing hook
    synchronized boolean isSeriesCollected() {
        return softSeriesReference.get() == null;
    }

}
