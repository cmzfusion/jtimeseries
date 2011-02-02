package com.od.jtimeseries.timeseries.impl;

import com.od.jtimeseries.timeseries.*;
import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 01-Mar-2009
 * Time: 20:01:08
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractListTimeSeriesTest<E extends ListTimeSeries> extends AbstractListTest {

    private volatile E timeSeries;
    private volatile List<ListTimeSeriesEvent> events;
    private volatile CountDownLatch countDownLatch;

    public final List<TimeSeriesItem> getListInstance() throws Exception {
        events = new ArrayList<ListTimeSeriesEvent>();
        timeSeries = getTimeSeriesInstance();
        return timeSeries;
    }

    @After
    public void tearDown() {
        super.tearDown();
        events = null;
        timeSeries = null;
    }

    public abstract E getTimeSeriesInstance() throws Exception;

    public E getTimeSeries() {
        return timeSeries;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  List methods - override the tests to add checking for ListTimeSeriesEvents

    @Test
    public void testAdd() {
        initializeCountdown(3);
        super.testAdd();
        waitForLatch();

        ListTimeSeriesEvent actual = events.get(0);
        ListTimeSeriesEvent itemsAddedEvent = ListTimeSeriesEvent.createItemsAddedOrInsertedEvent(timeSeries, 0, 0, TimeSeriesTestUtils.createItemsWithTimestamps(1), 0);
        assertEquals(itemsAddedEvent, actual);
        assertEquals(ListTimeSeriesEvent.createItemsAddedOrInsertedEvent(timeSeries, 1, 1, TimeSeriesTestUtils.createItemsWithTimestamps(3), 0), events.get(1));
        assertEquals(ListTimeSeriesEvent.createItemsAddedOrInsertedEvent(timeSeries, 1, 1, TimeSeriesTestUtils.createItemsWithTimestamps(2), 0), events.get(2));
    }

    @Test
    public void testAddAll() throws InterruptedException {
        initializeCountdown(4);
        super.testAddAll();
        waitForLatch();

        assertEquals(ListTimeSeriesEvent.createItemsAddedOrInsertedEvent(timeSeries, 0, 0, TimeSeriesTestUtils.createItemsWithTimestamps(1), 0), events.get(0));
        assertEquals(ListTimeSeriesEvent.createItemsAddedOrInsertedEvent(timeSeries, 1, 1, TimeSeriesTestUtils.createItemsWithTimestamps(4), 0), events.get(1));
        assertEquals(ListTimeSeriesEvent.createItemsAddedOrInsertedEvent(timeSeries, 1, 2, TimeSeriesTestUtils.createItemsWithTimestamps(2, 3), 0), events.get(2));
        assertEquals(ListTimeSeriesEvent.createItemsAddedOrInsertedEvent(timeSeries, 4, 5, TimeSeriesTestUtils.createItemsWithTimestamps(5, 6), 0), events.get(3));
    }

    @Test
    public void testRemove() {
        initializeCountdown(7);
        super.testRemove();
        waitForLatch();
        assertEquals(ListTimeSeriesEvent.createItemsRemovedEvent(timeSeries, 2, 2, TimeSeriesTestUtils.createItemsWithTimestamps(3), 0), events.get(5));
        assertEquals(ListTimeSeriesEvent.createItemsRemovedEvent(timeSeries, 3, 3, TimeSeriesTestUtils.createItemsWithTimestamps(5), 0), events.get(6));
    }

    @Test
    public void testRemoveAll() {
        initializeCountdown(8);
        super.testRemoveAll();
        waitForLatch();
        assertEquals(ListTimeSeriesEvent.createSeriesChangedEvent(timeSeries, TimeSeriesTestUtils.createItemsWithTimestamps(1, 2, 5, 6), 0), events.get(6));
    }

    @Test
    public void testSet() {
        initializeCountdown(5);
        super.testSet();
        waitForLatch();
        assertEquals(ListTimeSeriesEvent.createItemsRemovedEvent(timeSeries, 1, 1, TimeSeriesTestUtils.createItemsWithTimestamps(3), 0), events.get(3));
        assertEquals(ListTimeSeriesEvent.createItemsAddedOrInsertedEvent(timeSeries, 1, 1, TimeSeriesTestUtils.createItemsWithTimestamps(2), 0), events.get(4));
    }

    private void initializeCountdown(int count) {
        countDownLatch = new CountDownLatch(count);
        addCountdownListener();
    }


    /////////////////////////////////////////////////////////////////////////////////////////
    // ListTimeSeries methods not in List

    @Test
    public void testAppend() {
        initializeCountdown(2);
        assertTrue(timeSeries.append(TimeSeriesTestUtils.createItemWithTimestamp(1)));
        assertEquals(1, timeSeries.size());
        assertTrue(timeSeries.append(TimeSeriesTestUtils.createItemWithTimestamp(2)));
        testContainsConsecutiveNumbersStartingFromOne(timeSeries, 2);
        assertFalse(timeSeries.append(TimeSeriesTestUtils.createItemWithTimestamp(1)));
        testContainsConsecutiveNumbersStartingFromOne(timeSeries, 2);

        waitForLatch();
        assertEquals(ListTimeSeriesEvent.createItemsAddedOrInsertedEvent(timeSeries, 0, 0, TimeSeriesTestUtils.createItemsWithTimestamps(1), 0), events.get(0));
        assertEquals(ListTimeSeriesEvent.createItemsAddedOrInsertedEvent(timeSeries, 1, 1, TimeSeriesTestUtils.createItemsWithTimestamps(2), 0), events.get(1));
    }

    @Test
    public void testPrepend() {
        initializeCountdown(2);
        assertTrue(timeSeries.append(TimeSeriesTestUtils.createItemWithTimestamp(2)));
        assertEquals(1, timeSeries.size());
        assertTrue(timeSeries.prepend(TimeSeriesTestUtils.createItemWithTimestamp(1)));
        testContainsConsecutiveNumbersStartingFromOne(timeSeries, 2);
        assertFalse(timeSeries.prepend(TimeSeriesTestUtils.createItemWithTimestamp(2)));
        testContainsConsecutiveNumbersStartingFromOne(timeSeries, 2);

        waitForLatch();
        assertEquals(ListTimeSeriesEvent.createItemsAddedOrInsertedEvent(timeSeries, 0, 0, TimeSeriesTestUtils.createItemsWithTimestamps(2), 0), events.get(0));
        assertEquals(ListTimeSeriesEvent.createItemsAddedOrInsertedEvent(timeSeries, 0, 0, TimeSeriesTestUtils.createItemsWithTimestamps(1), 0), events.get(1));
    }

    @Test
    public void testGetEarliestTimestampAndGetLatestTimestamp() {
        addNewItemsForTimestamps(1000, 1000, 2000, 3000, 3000);
        assertEquals(5, timeSeries.size());
        assertEquals(1000, timeSeries.getEarliestTimestamp());
        assertEquals(3000, timeSeries.getLatestTimestamp());
    }

    @Test
    public void testGetEarliestAndLatestItem() {
        timeSeries.append(TimeSeriesTestUtils.createItemWithTimestamp(1000, 1));
        timeSeries.append(TimeSeriesTestUtils.createItemWithTimestamp(1000, 2));
        timeSeries.append(TimeSeriesTestUtils.createItemWithTimestamp(2000, 3));
        timeSeries.append(TimeSeriesTestUtils.createItemWithTimestamp(3000, 4));
        timeSeries.append(TimeSeriesTestUtils.createItemWithTimestamp(3000, 5));
        assertEquals(5, timeSeries.size());
        assertEquals(1, timeSeries.getEarliestItem().longValue());
        assertEquals(5, timeSeries.getLatestItem().longValue());
    }

    @Test
    public void testPrependProtectsTimestampOrdering() {
        addNewItemsForTimestamps(10);
        assertFalse(timeSeries.prepend(TimeSeriesTestUtils.createItemWithTimestamp(11)));
        assertEquals(1, timeSeries.size());
    }

    @Test
    public void testAppendProtectsTimestampOrdering() {
        addNewItemsForTimestamps(10);
        assertFalse(timeSeries.append(TimeSeriesTestUtils.createItemWithTimestamp(9)));
        assertEquals(1, timeSeries.size());
    }

    @Test
    public void testAddAtIndexProtectsTimestampOrdering() {
        addNewItemsForTimestamps(10);
        addNewItemsForTimestamps(20);
        try {
            timeSeries.add(1, TimeSeriesTestUtils.createItemWithTimestamp(9));
            fail("Should cause TimeSeriesOrderingException");
        } catch (TimeSeriesOrderingException e) {
            checkExceptionDetails(e, 1, 9);
        }

        try {
            timeSeries.add(0, TimeSeriesTestUtils.createItemWithTimestamp(11));
            fail("Should cause TimeSeriesOrderingException");
        } catch (TimeSeriesOrderingException e) {
            checkExceptionDetails(e, 0, 11);

        }

        try {
            timeSeries.add(2, TimeSeriesTestUtils.createItemWithTimestamp(19));
            fail("Should cause TimeSeriesOrderingException");
        } catch (TimeSeriesOrderingException e) {
            checkExceptionDetails(e, 2, 19);

        }
    }

    @Test
    public void testAddProtectsTimestampOrdering() {
        addNewItemsForTimestamps(10);
        try {
            timeSeries.add(TimeSeriesTestUtils.createItemWithTimestamp(9));
            fail("Should cause TimeSeriesOrderingException");
        } catch (TimeSeriesOrderingException e) {
            checkExceptionDetails(e, 1, 9);
        }
    }

    @Test
    public void testAddAllProtectsTimestampOrdering() {
        addNewItemsForTimestamps(10);
        try {
            timeSeries.addAll(TimeSeriesTestUtils.createItemsWithTimestamps(9, 10, 11));
            fail("Should cause TimeSeriesOrderingException");
        } catch (TimeSeriesOrderingException e) {
            checkExceptionDetails(e, 1, 9);
        }

        try {
            timeSeries.addAll(TimeSeriesTestUtils.createItemsWithTimestamps(11, 7, 12));
            fail("Should cause TimeSeriesOrderingException");
        } catch (TimeSeriesOrderingException e) {
            checkExceptionDetails(e, 1, 7);
        }
    }

    @Test
    public void testAddAllAtIndexProtectsTimestampOrdering() {
        addNewItemsForTimestamps(10, 20);
        try {
            timeSeries.addAll(1, TimeSeriesTestUtils.createItemsWithTimestamps(9, 11));
            fail("Should cause TimeSeriesOrderingException");
        } catch (TimeSeriesOrderingException e) {
            checkExceptionDetails(e, 1, 9);
        }

        try {
            timeSeries.addAll(1, TimeSeriesTestUtils.createItemsWithTimestamps(11, 21));
            fail("Should cause TimeSeriesOrderingException");
        } catch (TimeSeriesOrderingException e) {
            checkExceptionDetails(e, 1, 21);
        }
    }

    @Test
    public void testGetIndexAtOrBefore() {
        addNewItemsForTimestamps(10, 20, 20, 20, 20, 30, 30, 30, 30, 40);
        assertEquals(4, timeSeries.getIndexOfFirstItemAtOrBefore(25));
        assertEquals(4, timeSeries.getIndexOfFirstItemAtOrBefore(20)); //here must return the highest valid index of a 20
        assertEquals(0, timeSeries.getIndexOfFirstItemAtOrBefore(15));
        assertEquals(0, timeSeries.getIndexOfFirstItemAtOrBefore(10));
        assertEquals(-1, timeSeries.getIndexOfFirstItemAtOrBefore(9));
        assertEquals(-1, timeSeries.getIndexOfFirstItemAtOrBefore(-1));
        assertEquals(9, timeSeries.getIndexOfFirstItemAtOrBefore(50));
    }

    @Test
    public void testGetItemAtOrBefore() {
        TimeSeriesItem item20 = TimeSeriesTestUtils.createItemWithTimestamp(20);
        TimeSeriesItem item30 = TimeSeriesTestUtils.createItemWithTimestamp(30);
        addNewItemsForTimestamps(10, 20, 20);
        timeSeries.add(item20);
        timeSeries.add(item30);
        assertSame(item20, timeSeries.getFirstItemAtOrBefore(20));
        assertSame(item20, timeSeries.getFirstItemAtOrBefore(25));
        assertSame(item30, timeSeries.getFirstItemAtOrBefore(30));
    }

    @Test
    public void testGetFirstIndexAtOrAfter() {
        addNewItemsForTimestamps(10, 20, 20, 20, 20, 30, 30, 30, 30, 40);
        assertEquals(5, timeSeries.getIndexOfFirstItemAtOrAfter(25));
        assertEquals(5, timeSeries.getIndexOfFirstItemAtOrAfter(30)); //here must return the highest valid index of a 20
        assertEquals(9, timeSeries.getIndexOfFirstItemAtOrAfter(35));
        assertEquals(9, timeSeries.getIndexOfFirstItemAtOrAfter(40));
        assertEquals(-1, timeSeries.getIndexOfFirstItemAtOrAfter(50));
        assertEquals(0, timeSeries.getIndexOfFirstItemAtOrAfter(-1));
    }

    @Test
    public void testGetItemAtOrAfter() {
        TimeSeriesItem item20 = TimeSeriesTestUtils.createItemWithTimestamp(20);
        TimeSeriesItem item30 = TimeSeriesTestUtils.createItemWithTimestamp(30);
        timeSeries.add(item20);
        timeSeries.add(item30);
        addNewItemsForTimestamps(30, 40);
        assertSame(item20, timeSeries.getFirstItemAtOrAfter(20));
        assertSame(item30, timeSeries.getFirstItemAtOrAfter(25));
        assertSame(item30, timeSeries.getFirstItemAtOrAfter(30));
    }

    @Test
    public void testSubSeries() {
        addNewItemsForTimestamps(1, 2, 3, 3, 3, 4, 5, 6, 6, 6, 7, 8);
        assertEquals(TimeSeriesTestUtils.createSeriesWithItems(1, 2, 3, 3, 3, 4, 5, 6, 6, 6, 7, 8), timeSeries.getSubSeries(-1, 1000));
        assertEquals(TimeSeriesTestUtils.createSeriesWithItems(1, 2, 3, 3, 3, 4, 5, 6, 6, 6, 7, 8), timeSeries.getSubSeries(1, 8));
        assertEquals(TimeSeriesTestUtils.createSeriesWithItems(3, 3, 3, 4, 5, 6, 6, 6), timeSeries.getSubSeries(3, 6));
        assertEquals(TimeSeriesTestUtils.createSeriesWithItems(3, 3, 3, 4, 5), timeSeries.getSubSeries(3, 5));
        assertEquals(TimeSeriesTestUtils.createSeriesWithItems(4, 5), timeSeries.getSubSeries(4, 5));
        assertEquals(TimeSeriesTestUtils.createSeriesWithItems(5), timeSeries.getSubSeries(5, 5));
        assertEquals(TimeSeriesTestUtils.createSeriesWithItems(), timeSeries.getSubSeries(5, 4));
        assertEquals(TimeSeriesTestUtils.createSeriesWithItems(), timeSeries.getSubSeries(5, -1));
    }

    @Test
    public void testSubSeriesFromStartIndex() {
        addNewItemsForTimestamps(1, 2, 3, 3, 3, 4, 5, 6, 6, 6, 7, 8);
        assertEquals(TimeSeriesTestUtils.createSeriesWithItems(1, 2, 3, 3, 3, 4, 5, 6, 6, 6, 7, 8), timeSeries.getSubSeries(-1));
        assertEquals(TimeSeriesTestUtils.createSeriesWithItems(1, 2, 3, 3, 3, 4, 5, 6, 6, 6, 7, 8), timeSeries.getSubSeries(0));
        assertEquals(TimeSeriesTestUtils.createSeriesWithItems(1, 2, 3, 3, 3, 4, 5, 6, 6, 6, 7, 8), timeSeries.getSubSeries(1));
        assertEquals(TimeSeriesTestUtils.createSeriesWithItems(2, 3, 3, 3, 4, 5, 6, 6, 6, 7, 8), timeSeries.getSubSeries(2));
        assertEquals(TimeSeriesTestUtils.createSeriesWithItems(3, 3, 3, 4, 5, 6, 6, 6, 7, 8), timeSeries.getSubSeries(3));
        assertEquals(TimeSeriesTestUtils.createSeriesWithItems(4, 5, 6, 6, 6, 7, 8), timeSeries.getSubSeries(4));
        assertEquals(TimeSeriesTestUtils.createSeriesWithItems(7, 8), timeSeries.getSubSeries(7));
        assertEquals(TimeSeriesTestUtils.createSeriesWithItems(8), timeSeries.getSubSeries(8));
        assertEquals(TimeSeriesTestUtils.createSeriesWithItems(), timeSeries.getSubSeries(9));
    }

    private void waitForLatch() {
        try {
            countDownLatch.await(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Latch timeout expired");
        }
    }

    private void addCountdownListener() {
        CountDownLatchSeriesListener l = new CountDownLatchSeriesListener(countDownLatch, events);
        timeSeries.addTimeSeriesListener(l);
    }

    private void checkExceptionDetails(TimeSeriesOrderingException e, int index, int timestamp) {
        assertEquals(index, e.getIndex());
        assertEquals(timestamp, e.getTimestamp());
    }

}
