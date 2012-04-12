package com.od.jtimeseries.component.util.cache;

import com.od.jtimeseries.source.Counter;
import com.od.jtimeseries.source.ValueRecorder;
import com.od.jtimeseries.source.impl.DefaultCounter;

import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 04/04/12
 * Time: 09:31
 *
 * A TimeSeriesCache which uses soft references to hold on to cached instances
 */
public class SoftReferencedCache<K,V> implements TimeSeriesCache<K,V> {

    private ConcurrentHashMap<K, SoftReference<V>> cache = new ConcurrentHashMap<K, SoftReference<V>>();

    private CalculateHitRatioTask calculateHitRatioTask = new CalculateHitRatioTask();

    public V get(K key) {
        calculateHitRatioTask.incrementRequests();

        V result = null;
        SoftReference<V> s = cache.get(key);
        if ( s != null ) {
            result = s.get();
        }
        if ( result != null) {
            calculateHitRatioTask.incrementHits();
        }
        return result;
    }

    public V put(K key, V value) {
        SoftReference<V> old = cache.put(key, new SoftReference<V>(value));
        return old == null ? null : old.get();
    }

    public V remove(K key) {
        SoftReference<V> value = cache.remove(key);
        return value == null ? null : value.get();
    }

    //this metric is not supported for SoftReferencedCache
    public void setCacheSizeCounter(Counter cacheSizeCounter) {
    }

    //this metric is not supported for SoftReferencedCache
    public void setCacheOccupancyCounter(Counter cacheItemCount) {
    }

    //this metric is not supported for SoftReferencedCache
    public void setCacheRemovesCounter(Counter cacheRemoves) {
    }

    public void setCacheHitPercentageValueRecorder(ValueRecorder cacheHitPercentage) {
        calculateHitRatioTask.setCacheHitPercentageValueRecorder(cacheHitPercentage);
    }

    public void setCacheRequestCounter(Counter cacheRequestCounter) {
        calculateHitRatioTask.setCacheRequestCounter(cacheRequestCounter);
    }
}
