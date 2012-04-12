package com.od.jtimeseries.component.util.cache;

import com.od.jtimeseries.source.Counter;
import com.od.jtimeseries.source.ValueRecorder;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 04/04/12
 * Time: 09:25
 *
 * Cache for timeseries instances
 */
public interface TimeSeriesCache<K, E> {

    /**
     * @return an item, if it exists in the cache
     */
    E get(K key);

    /**
     * add an item to the cache, if there is sufficient capacity
     */
    E put(K key, E value);

    /**
     * remove an item from the cache
     */
    E remove(K key);

    void setCacheSizeCounter(Counter cacheSizeCounter);

    void setCacheOccupancyCounter(Counter cacheItemCount);

    void setCacheRemovesCounter(Counter cacheRemoves);

    void setCacheHitPercentageValueRecorder(ValueRecorder cacheHitPercentage);

    void setCacheRequestCounter(Counter c);
}
