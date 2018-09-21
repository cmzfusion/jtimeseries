package com.od.jtimeseries.component.util.cache;

import com.od.jtimeseries.source.Counter;
import com.od.jtimeseries.source.impl.DefaultCounter;
import com.od.jtimeseries.util.time.Time;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 11/04/12
 * Time: 18:06
 */
public class TestLRUCache extends TestCase {

    public Counter sizeCounter;
    private Counter removalCounter;

    public void testRemoval() {
        LRUCache<String, Object> l = new LRUCache<String, Object>(10, 10, 0, 0, Time.seconds(10), 99);
        addMetrics(l);
        addItems(l, 20, 0);
        assertEquals(sizeCounter.getCount(), 10);
        assertEquals(removalCounter.getCount(), 10);
        assertEquals("CacheEntry10", l.getLeastRecentlyUsed().getKey());
        assertEquals("CacheEntry19", l.getMostRecentlyUsed().getKey());
    }

    private void addItems(LRUCache<String, Object> l, int entryCount, int startCount) {
        for ( int loop= 0; loop < entryCount; loop++) {
            int next = startCount + loop;
            l.put("CacheEntry" + next, "CacheEntry" + next);
        }
    }

    private void addMetrics(LRUCache<String, Object> l) {
        sizeCounter = new DefaultCounter("Size");
        l.setCacheSizeCounter(sizeCounter);

        removalCounter = new DefaultCounter("Removal");
        l.setCacheRemovesCounter(removalCounter);
    }

    public void testExpansion() {
        LRUCache<String, Object> l = new LRUCache<String, Object>(10, 10, 20, 100, Time.seconds(0), 99);
        addMetrics(l);

        addItems(l, 10, 0);
        waitForExpansion();
        assertEquals(12, sizeCounter.getCount());

        addItems(l, 2, 10);
        waitForExpansion();
        assertEquals(14, sizeCounter.getCount());
        assertEquals(0, removalCounter.getCount());
    }

    private void waitForExpansion() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public void testInitialSizeCannotBeLessThanMinSize() {
        LRUCache<String, Object> l = new LRUCache<String, Object>(5, 10, 20, 100, Time.seconds(0), 99);
        assertEquals(10, l.currentMaxSize);
    }

}
