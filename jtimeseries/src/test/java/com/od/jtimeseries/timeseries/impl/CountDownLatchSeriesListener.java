package com.od.jtimeseries.timeseries.impl;

import com.od.jtimeseries.timeseries.TimeSeriesEvent;
import com.od.jtimeseries.timeseries.TimeSeriesListener;
import org.junit.Assert;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 31/01/11
* Time: 12:08
*/
class CountDownLatchSeriesListener implements TimeSeriesListener {

    private CountDownLatch latch;
    private List<TimeSeriesEvent> events;

    public CountDownLatchSeriesListener(CountDownLatch latch, List events) {
        this.latch = latch;
        this.events = events;
    }

    public CountDownLatchSeriesListener(int eventCount) {
        this.latch = new CountDownLatch(eventCount);
        this.events = new LinkedList<TimeSeriesEvent>();
    }

    public List<TimeSeriesEvent> getEvents() {
        return events;
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public void itemsAddedOrInserted(TimeSeriesEvent e) {
        handleCallback(e);
    }

    public void itemsRemoved(TimeSeriesEvent e) {
        handleCallback(e);
    }

    public void itemsChanged(TimeSeriesEvent e) {
        handleCallback(e);
    }

    public void seriesChanged(TimeSeriesEvent e) {
        handleCallback(e);
    }

    private void handleCallback(TimeSeriesEvent e) {
        events.add(e);
        if (latch.getCount() == 0) {
            //not sure fail will work on event notification thread
            System.err.println("Too many calls to item listener");
            Assert.fail("Called too many times");
        }
        latch.countDown();
    }
}
