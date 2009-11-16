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
import com.od.jtimeseries.timeseries.impl.TimeSeriesOrderingException;
import com.od.jtimeseries.timeseries.impl.WrappedTimeSeriesEventHandler;
import com.od.jtimeseries.util.TimeSeriesExecutorFactory;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.identifiable.IdentifiableBase;
import com.od.jtimeseries.util.logging.LogDefaults;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.time.TimePeriod;

import java.lang.ref.SoftReference;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18-May-2009
 * Time: 13:10:01
 */
public class FilesystemTimeSeries extends IdentifiableBase implements IdentifiableTimeSeries, ListTimeSeries {

    private static ScheduledExecutorService clearCacheExecutor = Executors.newSingleThreadScheduledExecutor();
    private Executor eventExecutor = TimeSeriesExecutorFactory.getExecutorForTimeSeriesEvents(this);
    private SoftReference<RoundRobinTimeSeries> softSeriesReference = new SoftReference<RoundRobinTimeSeries>(null);
    private RoundRobinSerializer roundRobinSerializer;
    private TimePeriod appendPeriod;
    private TimePeriod rewritePeriod;
    private FileHeader fileHeader;
    private WrappedTimeSeriesEventHandler timeSeriesEventHandler = new WrappedTimeSeriesEventHandler(this);
    private WriteBehindCache writeBehindCache = new WriteBehindCache();
    private long lastTimestamp = -1;
    private long flushScheduled = Long.MAX_VALUE;
    private LogMethods logMethods = LogDefaults.getDefaultLogMethods(FilesystemTimeSeries.class);

    public FilesystemTimeSeries(Identifiable parent, String id, String description, RoundRobinSerializer roundRobinSerializer, FileHeader fileHeader, TimePeriod appendPeriod, TimePeriod rewritePeriod) throws SerializationException {
        super(parent, id, description);
        this.roundRobinSerializer = roundRobinSerializer;
        this.appendPeriod = appendPeriod;
        this.rewritePeriod = rewritePeriod;
        checkHeader(fileHeader);
    }

    public FilesystemTimeSeries(String id, String description, RoundRobinSerializer roundRobinSerializer, FileHeader fileHeader, TimePeriod appendPeriod, TimePeriod rewritePeriod) throws SerializationException {
        super(id, description);
        this.roundRobinSerializer = roundRobinSerializer;
        this.appendPeriod = appendPeriod;
        this.rewritePeriod = rewritePeriod;
        checkHeader(fileHeader);
    }

    private void checkHeader(FileHeader fileHeader) throws SerializationException {
        //protective copy for our private access
        if ( roundRobinSerializer.fileExists(fileHeader) ) {
            roundRobinSerializer.updateHeader(fileHeader);
        } else {
            roundRobinSerializer.createFile(fileHeader);
        }
        this.fileHeader = new FileHeader(fileHeader);
    }

    public synchronized boolean add(TimeSeriesItem i) {
        boolean result = doAppend(i);
        if ( ! result ) {
            throw new TimeSeriesOrderingException(size(), i.getTimestamp());
        } else {
            return true;
        }
    }

    public synchronized boolean append(TimeSeriesItem i) {
        return doAppend(i);
    }

    private boolean doAppend(final TimeSeriesItem i) {
        boolean result = false;
        if ( i.getTimestamp() >= lastTimestamp) {
            writeBehindCache.addItemForAppend(i);
            result = true;
            RoundRobinTimeSeries s = softSeriesReference.get();
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
        lastTimestamp = getLatestTimestamp();
    }

    public synchronized boolean addAll(int index, Collection<? extends TimeSeriesItem> i) {
        RoundRobinTimeSeries r = getRoundRobinSeries();
        boolean change = r.addAll(index, i);
        if ( change ) {
            writeBehindCache.cacheSeriesForRewrite(r);
        }
        lastTimestamp = getLatestTimestamp();
        return change;
    }

    public synchronized boolean addAll(Collection<? extends TimeSeriesItem> i) {
        RoundRobinTimeSeries r = getRoundRobinSeries();
        boolean change = r.addAll(i);
        if ( change ) {
            writeBehindCache.cacheSeriesForRewrite(r);
        }
        lastTimestamp = getLatestTimestamp();
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
        RoundRobinTimeSeries r = softSeriesReference.get();
        if ( r != null ) {
            return r.size();
        } else {
            return fileHeader.getCurrentSize() + writeBehindCache.getItems().size();
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
        return getRoundRobinSeries().hashCode();
    }

    public synchronized TimeSeries getSubSeries(long startTimestamp, long endTimestamp) {
        return getRoundRobinSeries().getSubSeries(startTimestamp, endTimestamp);
    }

    public synchronized TimeSeries getSubSeries(long timestamp) {
        return getRoundRobinSeries().getSubSeries(timestamp);
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

    public void addTimeSeriesListener(TimeSeriesListener l) {
        timeSeriesEventHandler.addTimeSeriesListener(l);
    }

    public void removeTimeSeriesListener(TimeSeriesListener l) {
        timeSeriesEventHandler.removeTimeSeriesListener(l);
    }

    private RoundRobinTimeSeries getRoundRobinSeries() {
        RoundRobinTimeSeries s = softSeriesReference.get();
        if ( s == null) {
            try {
                s = roundRobinSerializer.deserialize(fileHeader);

                //there may be appended items in our cache we need to add to
                //bring the filesystem series into sync
                s.addWithoutFiringEvent(writeBehindCache.getItems());

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

        private RoundRobinTimeSeries roundRobinSeries;
        private List<TimeSeriesItem> items = new ArrayList<TimeSeriesItem>();

        public void cacheSeriesForRewrite(RoundRobinTimeSeries roundRobinSeries) {
            this.roundRobinSeries = roundRobinSeries;
            scheduleFlushCacheTask(rewritePeriod.getLengthInMillis());
        }

        public synchronized void addItemForAppend(TimeSeriesItem timeSeriesItem) {
            items.add(timeSeriesItem);
            scheduleFlushCacheTask(appendPeriod.getLengthInMillis());
        }

        public List<TimeSeriesItem> getItems() {
            return items;
        }

        public synchronized void flush() {
            try {
                if ( roundRobinSeries != null) {
                        roundRobinSerializer.serialize(fileHeader, roundRobinSeries);
                } else {
                    roundRobinSerializer.append(fileHeader, items);
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

        private void clearCache() {
            roundRobinSeries = null;
            items.clear();
        }
    }

    public synchronized void flush() {
        writeBehindCache.flush();
    }

    private void scheduleFlushCacheTask(long delayMillis) {
        long newFlushTime = System.currentTimeMillis() + delayMillis;

        //flush scheduled should not be less than current time but lets be safe
        if ( newFlushTime < flushScheduled || flushScheduled < System.currentTimeMillis()) {
            flushScheduled = newFlushTime;
            clearCacheExecutor.schedule(
                    new Runnable() {
                        public void run() {
                            synchronized( FilesystemTimeSeries.this) {
                                writeBehindCache.flush();
                                flushScheduled = Long.MAX_VALUE;
                            }
                        }
                    },
                    delayMillis,
                    TimeUnit.MILLISECONDS
            );
        }
    }

}
