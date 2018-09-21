package com.od.jtimeseries.component.util.cache;

import com.od.jtimeseries.source.Counter;
import com.od.jtimeseries.source.ValueRecorder;
import com.od.jtimeseries.source.impl.DefaultCounter;
import com.od.jtimeseries.util.NamedExecutors;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.util.time.Time;
import com.od.jtimeseries.util.time.TimePeriod;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 03/04/12
 * Time: 09:10
 *
 * Hold references to timeseries in a strong referenced LRU cache which may increase in size until
 * a configurable percentage of available memory is used
 */
public class LRUCache<K,E> implements TimeSeriesCache<K,E> {

    private static LogMethods logMethods = LogUtils.getLogMethods(LRUCache.class);

    private Counter cacheSizeCounter = DefaultCounter.NULL_COUNTER;
    private Counter cacheItemCount = DefaultCounter.NULL_COUNTER;
    private Counter cacheRemoves = DefaultCounter.NULL_COUNTER;

    private CalculateHitRatioTask calcHitRatioTask = new CalculateHitRatioTask();

    /**
     * Initial max size of cache
     */
    private static final int DEFAULT_INITIAL_SIZE = Integer.parseInt(System.getProperty("JTS_INITIAL_LRU_CACHE_SIZE", "128"));

    /**
     * Minimum size for cache
     */
    private static final int DEFAULT_MIN_CACHE_SIZE = Integer.parseInt(System.getProperty("JTS_MIN_LRU_CACHE_SIZE", "128"));

    /**
     * percentage by which to increase or decrease cache size when the cache is expanded / shrunk
     */
    private final int increaseDecreasePercent;

    /**
     * percentage of max memory at which cache size will be reduced
     */
    private final double cacheShrinkThresholdPercent;

    /**
     * memory usage percentage after which cache expansions are denied
     */
    private final int maxMemoryForCacheExpansionPercent;


    volatile int currentMaxSize;
    private final int minSize;

    /**
     * Minimum intervals between cache size increases
     */
    private TimePeriod minimumExpansionInterval = Time.milliseconds(1000);


    private static final int ROLLING_AVERAGE_MEMORY_USE_MAX_SAMPLES = 8;
    private final LinkedList<Double> memoryUseSamples = new LinkedList<>();


    private long lastSizeCheck;


    private final LinkedHashMap<K,E> cache = new LinkedHashMap<K,E>(Math.max(DEFAULT_INITIAL_SIZE, DEFAULT_MIN_CACHE_SIZE), 0.75f, true) {
        protected boolean removeEldestEntry(Map.Entry eldest) {
            boolean result = size() > currentMaxSize;
            if ( result ) {
                cacheRemoves.incrementCount();
            }
            return result;
        }
    };

    private ScheduledExecutorService cacheExecutorService = NamedExecutors.newSingleThreadScheduledExecutor(getClass().getSimpleName());

    public LRUCache() {
        this(DEFAULT_INITIAL_SIZE, DEFAULT_MIN_CACHE_SIZE, 20, 60, Time.seconds(10), 90);
    }

    public LRUCache(int initialMaxSize) {
        this(initialMaxSize, DEFAULT_MIN_CACHE_SIZE, 20, 60, Time.seconds(10), 90);
    }

    public LRUCache(int initialMaxSize, int minSize, int increaseDecreasePercent, int maxMemoryForCacheExpansionPercent, TimePeriod minimumExpansionInterval, double cacheShrinkThresholdPercent) {
        this.currentMaxSize = Math.max(initialMaxSize, minSize);
        this.minSize = minSize;
        this.increaseDecreasePercent = increaseDecreasePercent;
        this.maxMemoryForCacheExpansionPercent = maxMemoryForCacheExpansionPercent;
        this.minimumExpansionInterval = minimumExpansionInterval;
        this.cacheShrinkThresholdPercent = cacheShrinkThresholdPercent;
        scheduleShrinkCacheTask();
        scheduleCacheHitPercentageCalculation();
    }

    private void scheduleCacheHitPercentageCalculation() {
        cacheExecutorService.scheduleWithFixedDelay(calcHitRatioTask, 60, 60, TimeUnit.SECONDS);
    }

    private void scheduleShrinkCacheTask() {
        cacheExecutorService.scheduleWithFixedDelay(new DecreaseCacheSizeTask(), 60, 60, TimeUnit.SECONDS);
    }

    public E get(K key) {
        synchronized(cache)  {
            calcHitRatioTask.incrementRequests();

            E result = cache.get(key);
            if ( result != null) {
                calcHitRatioTask.incrementHits();
            }
            return result;
        }
    }

    public E put(K key, E value) {
        synchronized(cache) {
            //since we are using LinkedHashMap in LRU cache mode, this may also cause oldest item to be dropped from map
            E result = cache.put(key, value);

            cacheItemCount.setCount(cache.size());
            if ( cache.size() == currentMaxSize) {
                scheduleCacheSizeIncrease();
            }
            return result;
        }
    }

    public E remove(K key) {
        synchronized(cache) {
            return cache.remove(key);
        }
    }

    public void setCacheSizeCounter(Counter cacheSizeCounter) {
        cacheSizeCounter.setCount(currentMaxSize);
        this.cacheSizeCounter = cacheSizeCounter;
    }

    public void setCacheOccupancyCounter(Counter cacheItemCount) {
        this.cacheItemCount = cacheItemCount;
    }

    public void setCacheRemovesCounter(Counter cacheRemoves) {
        this.cacheRemoves = cacheRemoves;
    }

    public void setCacheHitPercentageValueRecorder(ValueRecorder cacheHitPercentage) {
        this.calcHitRatioTask.setCacheHitPercentageValueRecorder(cacheHitPercentage);
    }

    public void setCacheRequestCounter(Counter cacheRequestCounter) {
        this.calcHitRatioTask.setCacheRequestCounter(cacheRequestCounter);
    }

    private void scheduleCacheSizeIncrease() {
        if ( System.currentTimeMillis() - lastSizeCheck >= minimumExpansionInterval.getLengthInMillis())  {
            logMethods.debug("Cache is full, checking whether a size increase is possible");
            lastSizeCheck = System.currentTimeMillis();
            IncreaseSizeCheckTask t = new IncreaseSizeCheckTask();
            cacheExecutorService.execute(t);
        }
    }

    private class IncreaseSizeCheckTask implements Runnable {

        public void run() {
            synchronized(cache) {
                //while we are using less than half the max memory
                //allow the cache size to keep increasing
                double utilisationPercent = getMemoryUtilisationPercent();

                if ( utilisationPercent < maxMemoryForCacheExpansionPercent) {
                    currentMaxSize *= ( 100 + increaseDecreasePercent) / 100f;
                    logMethods.info("Used memory " + utilisationPercent + " percent, max for increase " +
                            maxMemoryForCacheExpansionPercent + ", will increase cache size to " + currentMaxSize);
                    cacheSizeCounter.setCount(currentMaxSize);
                }
            }
        }
    }

    private class DecreaseCacheSizeTask implements Runnable {

        public void run() {
            synchronized(cache) {
                try {
                    double utilisationPercent = getMemoryUtilisationPercent();
                    if ( utilisationPercent > cacheShrinkThresholdPercent) {
                        int newSize = (int)(currentMaxSize * ( 100 - increaseDecreasePercent) / 100f);
                        currentMaxSize = Math.max(newSize, minSize);
                        cacheSizeCounter.setCount(currentMaxSize);
                        logMethods.info("Used memory " + utilisationPercent + " percent, will decrease cache size by " +
                                increaseDecreasePercent + " percent to " + currentMaxSize);
                        int toRemove = cache.size() - currentMaxSize;
                        removeFromCache(toRemove);
                    } else {
                        logMethods.info("Used memory " + utilisationPercent + " no decrease in LRU cache size required");
                    }
                } catch (Exception e) {
                    logMethods.error("Failed while removing series from LRU cache", e);
                }
            }
        }

        private void removeFromCache(int toRemove) {
            Iterator i = cache.entrySet().iterator();
            int removed = 0;
            while ( i.hasNext() && removed < toRemove) {
                i.next();
                i.remove();
                removed++;
            }
        }
    }

    private double getMemoryUtilisationPercent() {
        double maxAvailable = Runtime.getRuntime().maxMemory();
        double total = Runtime.getRuntime().totalMemory();
        double free = Runtime.getRuntime().freeMemory();

        double utilisedMemory = total - free;
        double utilisationRatio = utilisedMemory / maxAvailable;
        double percentage = utilisationRatio * 100;

        return getRollingAveragePercentage(percentage);
    }

    private double getRollingAveragePercentage(double percentage) {
        memoryUseSamples.add(percentage);
        if ( memoryUseSamples.size() > ROLLING_AVERAGE_MEMORY_USE_MAX_SAMPLES) {
            memoryUseSamples.remove(0);
        }

        DoubleSummaryStatistics stats = memoryUseSamples.stream().collect(Collectors.summarizingDouble(Double::doubleValue));
        return stats.getAverage();
    }


    //testing hook
    Map.Entry<K,E> getLeastRecentlyUsed() {
        synchronized (cache) {
            Map.Entry<K,E> result = null;
            Iterator<Map.Entry<K,E>> i = cache.entrySet().iterator();
            if(i.hasNext()) {
                result = i.next();
            }
            return result;
        }
    }

    //testing hook
    Map.Entry<K,E> getMostRecentlyUsed() {
        synchronized (cache) {
            Map.Entry<K,E> result = null;
            Iterator<Map.Entry<K,E>> i = cache.entrySet().iterator();
            while(i.hasNext()) {
                result = i.next();
            }
            return result;
        }
    }

}
