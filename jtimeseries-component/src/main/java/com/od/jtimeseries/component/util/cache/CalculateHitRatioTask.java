package com.od.jtimeseries.component.util.cache;

import com.od.jtimeseries.source.Counter;
import com.od.jtimeseries.source.ValueRecorder;
import com.od.jtimeseries.source.impl.DefaultCounter;
import com.od.jtimeseries.source.impl.DefaultValueRecorder;

import java.util.concurrent.atomic.AtomicLong;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 11/04/12
* Time: 18:02
*/
class CalculateHitRatioTask implements Runnable {

    private long lastRequestCount;
    private ValueRecorder cacheHitPercentage = DefaultValueRecorder.NULL_VALUE_RECORDER;
    private Counter cacheRequests = DefaultCounter.NULL_COUNTER;
    private AtomicLong cacheHits = new AtomicLong();

    public void setCacheHitPercentageValueRecorder(ValueRecorder cacheHitPercentage) {
        this.cacheHitPercentage = cacheHitPercentage;
    }

    public void setCacheRequestCounter(Counter cacheRequests) {
        this.cacheRequests = cacheRequests;
    }

    public void run() {
        long currentCount = cacheRequests.getCount();
        double cacheHitPercent = ((double) cacheHits.getAndSet(0)) / (currentCount - lastRequestCount) * 100;
        lastRequestCount = currentCount;
        cacheHitPercentage.newValue(cacheHitPercent);
    }

    public void incrementRequests() {
        cacheRequests.incrementCount();
    }

    public long incrementHits() {
        return cacheHits.incrementAndGet();
    }
}
