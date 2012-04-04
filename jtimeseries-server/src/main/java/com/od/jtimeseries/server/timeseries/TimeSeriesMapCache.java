package com.od.jtimeseries.server.timeseries;

import com.od.jtimeseries.util.NamedExecutors;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.util.time.Time;
import com.od.jtimeseries.util.time.TimePeriod;
import org.omg.CORBA.TIMEOUT;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 03/04/12
 * Time: 09:10
 *
 * Hold references to timeseries in a strong referenced map which may increase in size until
 * a configurable percentage of available memory is used
 *
 * A periodic task
 */
public class TimeSeriesMapCache<K,E> implements TimeSeriesCache<K,E> {

    private static LogMethods logMethods = LogUtils.getLogMethods(TimeSeriesMapCache.class);

    /**
     * Initial max size of cache
     */
    private int DEFAULT_INITIAL_SIZE = 256;

    /**
     * percentage of max cache size at which least utilised items will start to be removed
     */
    public double cacheRemovalThresholdPercent = 0.95;

    /**
     * percentage of least utilised items to remove on each cache removal
     */
    public int removalPercentage = 10;

    /**
     * memory usage percentage after which cache increases are denied
     */
    public int maxCacheHeapUtilisationPercent = 50;


    private volatile int maxSize = DEFAULT_INITIAL_SIZE;

    /**
     * Minimum intervals between cache size increases
     */
    private TimePeriod minimumExpansionInterval = Time.milliseconds(1000);

    /**
     * Cache removal task period
     */
    private TimePeriod cacheRemovalPeriod;

    private long lastSizeCheck;

    private Map<K,CacheUsageCounter<E>> cache = new ConcurrentHashMap<K,CacheUsageCounter<E>>(DEFAULT_INITIAL_SIZE);

    private ScheduledExecutorService cacheExecutorService = NamedExecutors.newSingleThreadScheduledExecutor(getClass().getSimpleName());

    public TimeSeriesMapCache() {
        this(256, 50, Time.seconds(10), 95, 5, Time.seconds(60));
    }

    public TimeSeriesMapCache(int maxInitialSize) {
        this(maxInitialSize, 50, Time.seconds(10), 95, 5, Time.seconds(60));
    }

    public TimeSeriesMapCache(int maxInitialSize, int maxCacheHeapUtilisationPercent, TimePeriod minimumExpansionInterval, double cacheRemovalThresholdPercent, int removalPercentage, TimePeriod removalPeriod) {
        this.maxSize = maxInitialSize;
        this.maxCacheHeapUtilisationPercent = maxCacheHeapUtilisationPercent;
        this.minimumExpansionInterval = minimumExpansionInterval;
        this.cacheRemovalThresholdPercent = cacheRemovalThresholdPercent;
        this.removalPercentage = removalPercentage;
        this.cacheRemovalPeriod = removalPeriod;
        scheduleRemoveLeastUtilisedItems();
    }

    private void scheduleRemoveLeastUtilisedItems() {
        cacheExecutorService.scheduleWithFixedDelay(new RemoveLeastActiveItemsTask(), cacheRemovalPeriod.getLengthInMillis(), cacheRemovalPeriod.getLengthInMillis(), TimeUnit.MILLISECONDS);
    }

    public E get(K key) {
        CacheUsageCounter<E> c = cache.get(key);
        c.usageCount.incrementAndGet();
        return c.value;
    }

    public E put(K key, E value) {
        E result = null;
        if ( cache.size() < maxSize ) {
            CacheUsageCounter<E> v = cache.put(key, new CacheUsageCounter<E>(value));
            result = v == null ? null : v.value;
        } else {
            scheduleCacheSizeIncrease();
        }
        return result;
    }

    public void remove(K key) {
        cache.remove(key);
    }

    public void setMinimumExpansionInterval(TimePeriod minimumExpansionInterval) {
        this.minimumExpansionInterval = minimumExpansionInterval;
    }

    public void setRemovalPercentage(int removalPercentage) {
        this.removalPercentage = removalPercentage;
    }

    public void setCacheRemovalThresholdPercent(double cacheRemovalThresholdPercent) {
        this.cacheRemovalThresholdPercent = cacheRemovalThresholdPercent;
    }

    public void setMaxCacheHeapUtilisationPercent(int maxCacheHeapUtilisationPercent) {
        this.maxCacheHeapUtilisationPercent = maxCacheHeapUtilisationPercent;
    }

    public void setCacheRemovalPeriod(TimePeriod cacheRemovalPeriod) {
        this.cacheRemovalPeriod = cacheRemovalPeriod;
    }

    private void scheduleCacheSizeIncrease() {
        if ( System.currentTimeMillis() - lastSizeCheck > minimumExpansionInterval.getLengthInMillis())  {
            logMethods.debug("Cache is full, checking whether a size increase is possible");
            lastSizeCheck = System.currentTimeMillis();
            IncreaseSizeCheckTask t = new IncreaseSizeCheckTask();
            cacheExecutorService.execute(t);
        }
    }

    private static class CacheUsageCounter<E> {
        private AtomicInteger usageCount = new AtomicInteger();
        private E value;

        public CacheUsageCounter(E value) {
            this.value = value;
        }
    }

    private class IncreaseSizeCheckTask implements Runnable {

        public void run() {
            //while we are using less than half the max memory
            //allow the cache size to keep increasing
            double maxAvailable = Runtime.getRuntime().maxMemory();
            double total = Runtime.getRuntime().totalMemory();
            double free = Runtime.getRuntime().freeMemory();

            double utilisedMemory = total - free;
            double utilisationRatio = utilisedMemory / maxAvailable;


            if ( utilisationRatio < maxCacheHeapUtilisationPercent / 100f) {
                maxSize *= 2;
                logMethods.debug("Avail memory " + utilisationRatio * 100 + " percent, will increase cache size to " + maxSize);
            }
        }
    }

    private class RemoveLeastActiveItemsTask implements Runnable {

        public void run() {
            float cacheUsageRatio = ((float) cache.size()) / maxSize;
            if ( cacheUsageRatio > cacheRemovalThresholdPercent / 100f) {
                int itemsToRemove = (int)(cache.size() * (removalPercentage / 100f));
                logMethods.debug("Cache usage " + cacheUsageRatio * 100 + " percent, removing least utilised " + itemsToRemove + " items from " + cache.size());
                removeFromCache(itemsToRemove);
            }
        }

        private void removeFromCache(int itemsToRemove) {
            List<Map.Entry<K, CacheUsageCounter<E>>> entries = getLeastActiveItems();
            Iterator<Map.Entry<K, CacheUsageCounter<E>>> i = entries.iterator();
            int removeCount = 0;
            while(i.hasNext() && removeCount++ < itemsToRemove) {
                Map.Entry<K, CacheUsageCounter<E>> o = i.next();
                cache.remove(o.getKey());
            }
        }

        /**
         * @return  a list of items ordered by cache usage count, lowest usage first
         */
        private List<Map.Entry<K, CacheUsageCounter<E>>> getLeastActiveItems() {
            List<Map.Entry<K, CacheUsageCounter<E>>> entries = new LinkedList<Map.Entry<K, CacheUsageCounter<E>>>(cache.entrySet());
            Collections.sort(entries, new Comparator<Map.Entry<K, CacheUsageCounter<E>>>() {

                public int compare(Map.Entry<K, CacheUsageCounter<E>> o1, Map.Entry<K, CacheUsageCounter<E>> o2) {
                    Integer l1 = o1.getValue().usageCount.get();
                    Integer l2 = o2.getValue().usageCount.get();
                    return l2.compareTo(l1);
                }
            });
            return entries;
        }
    }
}
