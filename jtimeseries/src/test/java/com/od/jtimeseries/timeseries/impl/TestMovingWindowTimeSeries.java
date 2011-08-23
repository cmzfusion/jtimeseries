package com.od.jtimeseries.timeseries.impl;

import com.od.jtimeseries.timeseries.TimeSeriesEvent;
import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.timeseries.util.SeriesUtils;
import com.od.jtimeseries.util.time.TimeSource;
import junit.framework.TestCase;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 31/01/11
 * Time: 08:08
 */
public class TestMovingWindowTimeSeries extends TestCase {

    private MovingWindowTimeSeries movingWindowSeries;

    public void setUp() {
        movingWindowSeries = new MovingWindowTimeSeries();

        SeriesUtils.addAll(TimeSeriesTestUtils.createItemsWithTimestamps(
            2, 4, 5, 5, 6, 8
        ), movingWindowSeries);

        setStartAndEndTime(4, 6);
    }

    public void testGetSnapshot() {
        assertEquals("testGetSnapshot", TimeSeriesTestUtils.createItemsWithTimestamps(4, 5, 5, 6), movingWindowSeries.getSnapshot());

        setStartAndEndTime(5, 5);
        assertEquals("testGetSnapshot", TimeSeriesTestUtils.createItemsWithTimestamps(5, 5), movingWindowSeries.getSnapshot());
    }

    public void testSize() {
        assertEquals("testSize", 4, movingWindowSeries.size());
        setStartAndEndTime(5, 5);
        assertEquals("testSize", 2, movingWindowSeries.size());
        setStartAndEndTime(0, Long.MAX_VALUE);
        assertEquals("testSize", 6, movingWindowSeries.size());
    }

    public void testIterator() {
        Iterator<TimeSeriesItem> i = movingWindowSeries.iterator();
        assertTrue(i.hasNext());
        assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(4), i.next());
        assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(5), i.next());
        i.remove();
        assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(5), i.next());
        assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(6), i.next());
        try {
            i.next();
            fail("Should not be able to iterate further");
        } catch ( NoSuchElementException nse ) {
            System.out.println("Caught NoSuchElementException correctly");
        }

        //test one 5 was removed
        assertEquals("testIterator",TimeSeriesTestUtils.createItemsWithTimestamps(4, 5, 6), movingWindowSeries.getSnapshot());
    }

    //If the window is set to open end, the addition affects
    //the view and an event is fired
    public void testAddWhenOpenEnded() {
        movingWindowSeries.setEndTime(TimeSource.OPEN_END_TIME);
        CountDownLatchSeriesListener countDownListener = addCountDownListener();
        TimeSeriesItem item9 = TimeSeriesTestUtils.createItemWithTimestamp(9);
        movingWindowSeries.addItem(item9);
        waitForCountdown(countDownListener);

        TimeSeriesEvent expectedEvent = TimeSeriesEvent.createItemsAddedOrInsertedEvent(
            movingWindowSeries,
            Collections.singletonList(item9),
            0
        );
        assertEquals("testAddWhenOpenEnded", expectedEvent, countDownListener.getEvents().get(0));
        assertEquals("testAddWhenOpenEnded", 6, movingWindowSeries.size());
        movingWindowSeries.removeTimeSeriesListener(countDownListener);
    }

    //If the window is set to an earlier end time, the addition does not affect
    //the view, until the view is set wider, and no event is fired
    public void testAddWhenEndRestricted() {
        movingWindowSeries.setEndTime(6);
        CountDownLatchSeriesListener countDownListener = addCountDownListener();
        movingWindowSeries.addItem(TimeSeriesTestUtils.createItemWithTimestamp(10));

        failOnCountdownEvent(countDownListener);
        assertEquals("testAddWhenEndRestricted", 4, movingWindowSeries.size());

        movingWindowSeries.setEndTime(10);
        assertEquals("testAddWhenEndRestricted", 6, movingWindowSeries.size());
    }

    public void testAddWhenNoItemsInWindow() {
        movingWindowSeries.setStartTime(10);
        movingWindowSeries.setEndTime(10);
        assertEquals("testAddWhenNoItemsInWindow", 0, movingWindowSeries.size());

        long modCount = movingWindowSeries.getModCount();
        TimeSeriesItem item9 = TimeSeriesTestUtils.createItemWithTimestamp(9);
        movingWindowSeries.addItem(item9);
        assertEquals("testAddWhenNoItemsInWindow", modCount, movingWindowSeries.getModCount());

        CountDownLatchSeriesListener countDownListener = addCountDownListener();
        TimeSeriesItem item10 = TimeSeriesTestUtils.createItemWithTimestamp(10);
        movingWindowSeries.addItem(item10);

        waitForCountdown(countDownListener);

        TimeSeriesEvent expectedEvent = TimeSeriesEvent.createItemsAddedOrInsertedEvent(
            movingWindowSeries,
            Collections.singletonList(item10),
            0
        );
        assertEquals("testAddWhenNoItemsInWindow", expectedEvent, countDownListener.getEvents().get(0));
        assertEquals("testAddWhenNoItemsInWindow", 1, movingWindowSeries.size());
        assertEquals("testAddWhenNoItemsInWindow", item10, movingWindowSeries.getItem(0));
    }

    public void testRemove() {
        CountDownLatchSeriesListener countDownListener = addCountDownListener();
        TimeSeriesItem item4 = TimeSeriesTestUtils.createItemWithTimestamp(4);
        movingWindowSeries.removeItem(item4);
        waitForCountdown(countDownListener);
        TimeSeriesEvent expectedEvent = TimeSeriesEvent.createItemsRemovedEvent(
                movingWindowSeries,
                Collections.singletonList(item4),
                0
        );
        assertEquals("testRemove", expectedEvent, countDownListener.getEvents().get(0));
        movingWindowSeries.removeTimeSeriesListener(countDownListener);

        countDownListener = addCountDownListener();
        TimeSeriesItem item6 = TimeSeriesTestUtils.createItemWithTimestamp(6);
        movingWindowSeries.removeItem(item6);
        waitForCountdown(countDownListener);
        expectedEvent = TimeSeriesEvent.createItemsRemovedEvent(
            movingWindowSeries,
            Collections.singletonList(item6),
            0
        );
        assertEquals("testRemove", expectedEvent, countDownListener.getEvents().get(0));
        movingWindowSeries.removeTimeSeriesListener(countDownListener);

        countDownListener = addCountDownListener();
        TimeSeriesItem item5 = TimeSeriesTestUtils.createItemWithTimestamp(5);
        movingWindowSeries.removeItem(item5);
        waitForCountdown(countDownListener);
        expectedEvent = TimeSeriesEvent.createItemsRemovedEvent(
            movingWindowSeries,
            Collections.singletonList(item5),
            0
        );
        assertEquals("testRemove", expectedEvent, countDownListener.getEvents().get(0));
    }

    //the remove affects the wrapped data, but does impact the view unless
    //or trigger an event an item in the view window is affected
    public void testRemoveOutsideWindow() {
        movingWindowSeries.setStartTime(2);
        assertEquals("testRemoveOutsideWindow", 5, movingWindowSeries.size());
        movingWindowSeries.setStartTime(4);
        assertEquals("testRemoveOutsideWindow", 4, movingWindowSeries.size());

        //remove 2, it should cause an event
        CountDownLatchSeriesListener countDownListener = addCountDownListener();
        TimeSeriesItem item2 = TimeSeriesTestUtils.createItemWithTimestamp(2);
        movingWindowSeries.removeItem(item2);
        failOnCountdownEvent(countDownListener);

        //following remove, 2 is no longer present, even when we enlarge the window to include it
        movingWindowSeries.setStartTime(2);
        assertEquals("testRemoveOutsideWindow", 4, movingWindowSeries.size());
    }

    public void testClear() {
        CountDownLatchSeriesListener countDownListener = addCountDownListener();
        movingWindowSeries.clear();
        waitForCountdown(countDownListener);
        TimeSeriesEvent expectedEvent = TimeSeriesEvent.createSeriesChangedEvent(
            movingWindowSeries,
            TimeSeriesTestUtils.createItemsWithTimestamps(),
            0
        );
        assertEquals("testClear", expectedEvent, countDownListener.getEvents().get(0));
    }

    public void testChangingStartAndEndInvalidateIterator() {
        Iterator<TimeSeriesItem> li = movingWindowSeries.iterator();
        li.next();
        movingWindowSeries.setStartTime(1000);
        try {
            li.next();
            fail("Should cause concurrent mod");
        } catch(ConcurrentModificationException c) {}

        setStartAndEndTime(4, 6);
        li = movingWindowSeries.iterator();
        li.next();
        movingWindowSeries.setEndTime(1000);
        try {
            li.next();
            fail("Should cause concurrent mod");
        } catch(ConcurrentModificationException c) {}
    }

    public void testEarliestItem() {
        assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(4), movingWindowSeries.getEarliestItem());
    }

    public void testLatestItem() {
        assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(6), movingWindowSeries.getLatestItem());
    }

    private void waitForCountdown(CountDownLatchSeriesListener countDownListener) {
        try {
            countDownListener.getLatch().await(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("waitForCountdown " + e);
        }
    }

    private void failOnCountdownEvent(CountDownLatchSeriesListener countDownListener) {
        try {
            boolean success = countDownListener.getLatch().await(50, TimeUnit.MILLISECONDS);
            if ( success ) {
                fail("should not receive an event");
            }
        } catch (InterruptedException e) {}
    }

    private CountDownLatchSeriesListener addCountDownListener() {
        CountDownLatchSeriesListener countDownListener = new CountDownLatchSeriesListener(1);
        movingWindowSeries.addTimeSeriesListener(countDownListener);
        return countDownListener;
    }

    private void setStartAndEndTime(long startTime, long endTime) {
        movingWindowSeries.setStartTime(startTime);
        movingWindowSeries.setEndTime(endTime);
    }

}
