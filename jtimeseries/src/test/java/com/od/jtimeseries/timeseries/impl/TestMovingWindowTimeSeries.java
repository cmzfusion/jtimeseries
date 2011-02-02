package com.od.jtimeseries.timeseries.impl;

import com.od.jtimeseries.timeseries.ListTimeSeries;
import com.od.jtimeseries.timeseries.ListTimeSeriesEvent;
import com.od.jtimeseries.timeseries.TimeSeriesItem;
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

        movingWindowSeries.addAll(TimeSeriesTestUtils.createItemsForTimestamps(
            2, 4, 5, 5, 6, 8
        ));

        setStartAndEndTime(4, 6);
    }

    public void testGetSnapshot() {
        assertEquals("testGetSnapshot", TimeSeriesTestUtils.createItemsForTimestamps(4, 5, 5, 6), movingWindowSeries.getSnapshot());

        setStartAndEndTime(5, 5);
        assertEquals("testGetSnapshot", TimeSeriesTestUtils.createItemsForTimestamps(5, 5), movingWindowSeries.getSnapshot());
    }

    public void testGetIndexOfFirstItemAtOrBefore() {
        assertEquals("testGetIndexOfFirstItemAtOrBefore", 3, movingWindowSeries.getIndexOfFirstItemAtOrBefore(6));
        assertEquals("testGetIndexOfFirstItemAtOrBefore", 2, movingWindowSeries.getIndexOfFirstItemAtOrBefore(5));
        assertEquals("testGetIndexOfFirstItemAtOrBefore", 0, movingWindowSeries.getIndexOfFirstItemAtOrBefore(4));
        assertEquals("testGetIndexOfFirstItemAtOrBefore", -1, movingWindowSeries.getIndexOfFirstItemAtOrBefore(2));
    }

    public void testGetIndexOfFirstItemAtOrAfter() {
        assertEquals("testGetIndexOfFirstItemAtOrBefore", -1, movingWindowSeries.getIndexOfFirstItemAtOrAfter(2));
        assertEquals("testGetIndexOfFirstItemAtOrBefore", 0, movingWindowSeries.getIndexOfFirstItemAtOrAfter(4));
        assertEquals("testGetIndexOfFirstItemAtOrBefore", 1, movingWindowSeries.getIndexOfFirstItemAtOrAfter(5));
        assertEquals("testGetIndexOfFirstItemAtOrBefore", 3, movingWindowSeries.getIndexOfFirstItemAtOrAfter(6));
    }

    public void testSize() {
        assertEquals("testSize", 4, movingWindowSeries.size());
        setStartAndEndTime(5, 5);
        assertEquals("testSize", 2, movingWindowSeries.size());
        setStartAndEndTime(0, Long.MAX_VALUE);
        assertEquals("testSize", 6, movingWindowSeries.size());
    }

    public void testIsEmpty() {
        assertFalse("testIsEmpty", movingWindowSeries.isEmpty());
        movingWindowSeries.clear();
        assertTrue("testIsEmpty", movingWindowSeries.isEmpty());
    }

    public void testContains() {
        assertTrue("testContains", movingWindowSeries.contains(TimeSeriesTestUtils.createItemWithTimestamp(4)));
        assertTrue("testContains", movingWindowSeries.contains(TimeSeriesTestUtils.createItemWithTimestamp(5)));
        assertFalse("testContains", movingWindowSeries.contains(TimeSeriesTestUtils.createItemWithTimestamp(1)));
        assertFalse("testContains", movingWindowSeries.contains(TimeSeriesTestUtils.createItemWithTimestamp(8)));
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
        assertEquals("testIterator",TimeSeriesTestUtils.createItemsForTimestamps(4, 5, 6), movingWindowSeries.getSnapshot());
    }

    public void testToArray() {
        Object[] expected = TimeSeriesTestUtils.createItemsForTimestamps(4, 5, 5, 6).toArray();
        assertTrue("testToArray", Arrays.equals(expected, movingWindowSeries.toArray()));

        TimeSeriesItem[] expected2 = TimeSeriesTestUtils.createItemsForTimestamps(4, 5, 5, 6).toArray(new TimeSeriesItem[0]);
        assertTrue("testToArray", Arrays.equals(expected2, movingWindowSeries.toArray(new TimeSeriesItem[0])));

        assertTrue(movingWindowSeries.toArray(new TimeSeriesItem[0]).getClass() == TimeSeriesItem[].class);
    }

    //If the window is set to open end, the addition affects
    //the view and an event is fired
    public void testAddWhenOpenEnded() {
        movingWindowSeries.setEndTime(MovingWindowTimeSeries.OPEN_END_TIME);
        CountDownLatchSeriesListener countDownListener = addCountDownListener();
        TimeSeriesItem item9 = TimeSeriesTestUtils.createItemWithTimestamp(9);
        movingWindowSeries.add(item9);
        waitForCountdown(countDownListener);

        ListTimeSeriesEvent expectedEvent = ListTimeSeriesEvent.createItemsAddedOrInsertedEvent(
            movingWindowSeries,
            5,
            5,
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
        movingWindowSeries.add(TimeSeriesTestUtils.createItemWithTimestamp(10));

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
        movingWindowSeries.add(item9);
        assertEquals("testAddWhenNoItemsInWindow", modCount, movingWindowSeries.getModCount());

        CountDownLatchSeriesListener countDownListener = addCountDownListener();
        TimeSeriesItem item10 = TimeSeriesTestUtils.createItemWithTimestamp(10);
        movingWindowSeries.add(item10);

        waitForCountdown(countDownListener);

        ListTimeSeriesEvent expectedEvent = ListTimeSeriesEvent.createItemsAddedOrInsertedEvent(
            movingWindowSeries,
            0,
            0,
            Collections.singletonList(item10),
            0
        );
        assertEquals("testAddWhenNoItemsInWindow", expectedEvent, countDownListener.getEvents().get(0));
        assertEquals("testAddWhenNoItemsInWindow", 1, movingWindowSeries.size());
        assertEquals("testAddWhenNoItemsInWindow", item10, movingWindowSeries.get(0));
    }

    public void testRemove() {
        CountDownLatchSeriesListener countDownListener = addCountDownListener();
        TimeSeriesItem item4 = TimeSeriesTestUtils.createItemWithTimestamp(4);
        movingWindowSeries.remove(item4);
        waitForCountdown(countDownListener);
        ListTimeSeriesEvent expectedEvent = ListTimeSeriesEvent.createItemsRemovedEvent(
                movingWindowSeries,
                0,
                0,
                Collections.singletonList(item4),
                0
        );
        assertEquals("testRemove", expectedEvent, countDownListener.getEvents().get(0));
        movingWindowSeries.removeTimeSeriesListener(countDownListener);

        countDownListener = addCountDownListener();
        TimeSeriesItem item6 = TimeSeriesTestUtils.createItemWithTimestamp(6);
        movingWindowSeries.remove(item6);
        waitForCountdown(countDownListener);
        expectedEvent = ListTimeSeriesEvent.createItemsRemovedEvent(
            movingWindowSeries,
            2,
            2,
            Collections.singletonList(item6),
            0
        );
        assertEquals("testRemove", expectedEvent, countDownListener.getEvents().get(0));
        movingWindowSeries.removeTimeSeriesListener(countDownListener);

        countDownListener = addCountDownListener();
        TimeSeriesItem item5 = TimeSeriesTestUtils.createItemWithTimestamp(5);
        movingWindowSeries.remove(item5);
        waitForCountdown(countDownListener);
        expectedEvent = ListTimeSeriesEvent.createItemsRemovedEvent(
            movingWindowSeries,
            0,
            0,
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
        movingWindowSeries.remove(item2);
        failOnCountdownEvent(countDownListener);

        //following remove, 2 is no longer present, even when we enlarge the window to include it
        movingWindowSeries.setStartTime(2);
        assertEquals("testRemoveOutsideWindow", 4, movingWindowSeries.size());
    }

    public void testRemoveAtIndex()  {
        CountDownLatchSeriesListener countDownListener = addCountDownListener();
        movingWindowSeries.remove(0);  //the 2, not currently in window
        failOnCountdownEvent(countDownListener);
        movingWindowSeries.removeTimeSeriesListener(countDownListener);

        countDownListener = addCountDownListener();
        movingWindowSeries.remove(0);  //this is now the 4, at first index in window

        waitForCountdown(countDownListener);
        ListTimeSeriesEvent expectedEvent = ListTimeSeriesEvent.createItemsRemovedEvent(
                movingWindowSeries,
                0,
                0,
                Collections.singletonList(TimeSeriesTestUtils.createItemWithTimestamp(4)),
                0
        );
        assertEquals("testRemove", expectedEvent, countDownListener.getEvents().get(0));
        movingWindowSeries.removeTimeSeriesListener(countDownListener);
    }

    public void testContainsAll() {
        List<TimeSeriesItem> items = TimeSeriesTestUtils.createSeriesWithItems(4, 5, 6);
        assertTrue("testContainsAll", movingWindowSeries.containsAll(items));

        items.add(TimeSeriesTestUtils.createItemWithTimestamp(8));
        assertFalse("testContainsAll", movingWindowSeries.containsAll(items));
    }

    public void testAddAll() {
        movingWindowSeries.setEndTime(12);
        assertEquals("testAddAll", 5, movingWindowSeries.size());

        CountDownLatchSeriesListener countDownListener = addCountDownListener();
        List<TimeSeriesItem> items = TimeSeriesTestUtils.createItemsForTimestamps(10, 12, 14);
        movingWindowSeries.addAll(items);
        waitForCountdown(countDownListener);
        ListTimeSeriesEvent expectedEvent = ListTimeSeriesEvent.createItemsAddedOrInsertedEvent(
                movingWindowSeries,
                5,
                6,
                TimeSeriesTestUtils.createItemsForTimestamps(10, 12),
                0
        );
        assertEquals("testAddAll", expectedEvent, countDownListener.getEvents().get(0));
        movingWindowSeries.removeTimeSeriesListener(countDownListener);

        assertEquals("testAddAll", 7, movingWindowSeries.size());

        movingWindowSeries.setEndTime(14);
        assertEquals("testAddAll", 8, movingWindowSeries.size());

        //modCount should not change, and no event if addition outside window
        long modCount = movingWindowSeries.getModCount();
        countDownListener = addCountDownListener();
        movingWindowSeries.addAll(TimeSeriesTestUtils.createItemsForTimestamps( 16, 18));
        failOnCountdownEvent(countDownListener);
        assertEquals("testAddAll", modCount, movingWindowSeries.getModCount());
    }

    public void testAddAllWhenWindowAfterCurrentContents() {
        setStartAndEndTime(12, 14);
        CountDownLatchSeriesListener countDownListener = addCountDownListener();
        List<TimeSeriesItem> items = TimeSeriesTestUtils.createItemsForTimestamps( 10, 12, 14, 16);
        movingWindowSeries.addAll(items);
        waitForCountdown(countDownListener);
        ListTimeSeriesEvent expectedEvent = ListTimeSeriesEvent.createItemsAddedOrInsertedEvent(
            movingWindowSeries,
            0,
            1,
            TimeSeriesTestUtils.createItemsForTimestamps( 12, 14 ),
            0
        );
        assertEquals("testAddAll", expectedEvent, countDownListener.getEvents().get(0));
        movingWindowSeries.removeTimeSeriesListener(countDownListener);

        assertEquals("testAddAll", 2, movingWindowSeries.size());
    }

    public void testIndexAddAll() {
        movingWindowSeries.setEndTime(12);

        CountDownLatchSeriesListener countDownListener = addCountDownListener();
        List<TimeSeriesItem> items = TimeSeriesTestUtils.createItemsForTimestamps( 10, 12, 14);
        movingWindowSeries.addAll(6, items);
        waitForCountdown(countDownListener);
        ListTimeSeriesEvent expectedEvent = ListTimeSeriesEvent.createSeriesChangedEvent(
                movingWindowSeries,
                TimeSeriesTestUtils.createItemsForTimestamps(4, 5, 5, 6, 8, 10, 12),
                0
        );
        assertEquals("testAddAllAtIndex", expectedEvent, countDownListener.getEvents().get(0));
        movingWindowSeries.removeTimeSeriesListener(countDownListener);

        assertEquals("testAddAllAtIndex", 7, movingWindowSeries.size());

        movingWindowSeries.setEndTime(14);
        assertEquals("testAddAllAtIndex", 8, movingWindowSeries.size());

        //modCount should not change, and no event if addition outside window
        long modCount = movingWindowSeries.getModCount();
        countDownListener = addCountDownListener();
        movingWindowSeries.addAll(9,  TimeSeriesTestUtils.createItemsForTimestamps( 15, 16 ));
        failOnCountdownEvent(countDownListener);
        assertEquals("testAddAllAtIndex", modCount, movingWindowSeries.getModCount());
    }

    public void testRemoveAll() {
        movingWindowSeries.setEndTime(8);

        CountDownLatchSeriesListener countDownListener = addCountDownListener();
        List<TimeSeriesItem> items = TimeSeriesTestUtils.createItemsForTimestamps( 5, 6 );
        movingWindowSeries.removeAll(items);
        waitForCountdown(countDownListener);
        ListTimeSeriesEvent expectedEvent = ListTimeSeriesEvent.createSeriesChangedEvent(
            movingWindowSeries,
            TimeSeriesTestUtils.createItemsForTimestamps( 4, 8),
            0
        );
        assertEquals("testRemoveAll", expectedEvent, countDownListener.getEvents().get(0));
    }

    public void testRetainAll() {
         movingWindowSeries.setEndTime(8);

        CountDownLatchSeriesListener countDownListener = addCountDownListener();
        List<TimeSeriesItem> items = TimeSeriesTestUtils.createItemsForTimestamps( 5, 6 );
        movingWindowSeries.retainAll(items);
        waitForCountdown(countDownListener);
        ListTimeSeriesEvent expectedEvent = ListTimeSeriesEvent.createSeriesChangedEvent(
            movingWindowSeries,
            TimeSeriesTestUtils.createItemsForTimestamps( 5, 5, 6),
            0
        );
        assertEquals("testRetainAll", expectedEvent, countDownListener.getEvents().get(0));
    }

    public void testClear() {
        CountDownLatchSeriesListener countDownListener = addCountDownListener();
        movingWindowSeries.clear();
        waitForCountdown(countDownListener);
        ListTimeSeriesEvent expectedEvent = ListTimeSeriesEvent.createSeriesChangedEvent(
            movingWindowSeries,
            TimeSeriesTestUtils.createItemsForTimestamps(),
            0
        );
        assertEquals("testClear", expectedEvent, countDownListener.getEvents().get(0));
    }

    public void testAddAtIndex() {
        CountDownLatchSeriesListener countDownListener = addCountDownListener();
        TimeSeriesItem item6 = TimeSeriesTestUtils.createItemWithTimestamp(6);
        movingWindowSeries.add(4, item6);
        waitForCountdown(countDownListener);
        ListTimeSeriesEvent expectedEvent = ListTimeSeriesEvent.createItemsAddedOrInsertedEvent(
            movingWindowSeries,
            3,
            3,
            Collections.singletonList(item6),
            0
        );
        assertEquals("testAddAtIndex", expectedEvent, countDownListener.getEvents().get(0));
    }

    public void testAddAtIndexAfterWindow() {
        CountDownLatchSeriesListener countDownListener = addCountDownListener();
        TimeSeriesItem item12 = TimeSeriesTestUtils.createItemWithTimestamp(12);
        movingWindowSeries.add(6, item12);
        failOnCountdownEvent(countDownListener);
        assertEquals("testAddAtIndexAfterWindow", 4, movingWindowSeries.size());

        ListTimeSeries expectedItems = TimeSeriesTestUtils.createSeriesWithItems(4, 5, 5, 6);
        assertEquals("testAddAtIndexAfterWindow", expectedItems, movingWindowSeries.getSnapshot());
    }

    public void testAddAtIndexBeforeWindow() {
        CountDownLatchSeriesListener countDownListener = addCountDownListener();
        TimeSeriesItem item1 = TimeSeriesTestUtils.createItemWithTimestamp(1);
        movingWindowSeries.add(0, item1);
        failOnCountdownEvent(countDownListener);
        assertEquals("testAddAtIndexAfterWindow", 4, movingWindowSeries.size());
        assertEquals("testAddAtIndexAfterWindow", TimeSeriesTestUtils.createSeriesWithItems(4, 5, 5, 6), movingWindowSeries.getSnapshot());
    }

    public void testAddAtIndexJustOutsideCurrentMaxIndexButWithinTimeWindow() {
        CountDownLatchSeriesListener countDownListener = addCountDownListener();
        TimeSeriesItem item6 = TimeSeriesTestUtils.createItemWithTimestamp(6);
        movingWindowSeries.add(5, item6);
        waitForCountdown(countDownListener);
        ListTimeSeriesEvent expectedEvent = ListTimeSeriesEvent.createItemsAddedOrInsertedEvent(
                movingWindowSeries,
                4,
                4,
                Collections.singletonList(item6),
                0
        );
        assertEquals("testAddAtIndexJustOutsideCurrentWindow", expectedEvent, countDownListener.getEvents().get(0));
    }

    public void testIndexOf() {
        assertEquals(0, movingWindowSeries.indexOf(TimeSeriesTestUtils.createItemWithTimestamp(4)));
        assertEquals(1, movingWindowSeries.indexOf(TimeSeriesTestUtils.createItemWithTimestamp(5)));
        assertEquals(-1, movingWindowSeries.indexOf(TimeSeriesTestUtils.createItemWithTimestamp(8)));
    }

    public void testLastIndexOf() {
        assertEquals(0, movingWindowSeries.lastIndexOf(TimeSeriesTestUtils.createItemWithTimestamp(4)));
        assertEquals(2, movingWindowSeries.lastIndexOf(TimeSeriesTestUtils.createItemWithTimestamp(5)));
        assertEquals(-1, movingWindowSeries.indexOf(TimeSeriesTestUtils.createItemWithTimestamp(8)));
    }

    public void testListIterator() {
        ListIterator li = movingWindowSeries.listIterator();
        assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(4),li.next());
        assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(5),li.next());
        assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(5),li.next());
        assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(6),li.next());
        assertFalse(li.hasNext());

        li = movingWindowSeries.listIterator();
        assertEquals(0, li.nextIndex());
        assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(4),li.next());
        assertEquals(1, li.nextIndex());
        assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(5),li.next());
        assertEquals(1, li.previousIndex());
        assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(5),li.previous()); //this seems totally illogical to me, but that's how List iterators are defined to work
        assertEquals(0, li.previousIndex());
        assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(4),li.previous());
        assertEquals(-1, li.previousIndex());
        assertFalse(li.hasPrevious());
    }

    public void testListIteratorRemove() {
        //check that calling remove actually fires an event
        CountDownLatchSeriesListener countDownListener = addCountDownListener();
        ListIterator li = movingWindowSeries.listIterator();
        assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(4),li.next());
        li.remove();
        waitForCountdown(countDownListener);
        TimeSeriesItem item4 = TimeSeriesTestUtils.createItemWithTimestamp(4);
        ListTimeSeriesEvent expectedEvent = ListTimeSeriesEvent.createItemsRemovedEvent(
                movingWindowSeries,
                0,
                0,
                Collections.singletonList(item4),
                0
        );
        assertEquals("testListIterator", expectedEvent, countDownListener.getEvents().get(0));

        movingWindowSeries.removeTimeSeriesListener(countDownListener);

        li.next();
        li.next();
        li.next();
        countDownListener = addCountDownListener();
        li.remove();
        waitForCountdown(countDownListener);
        TimeSeriesItem item6 = TimeSeriesTestUtils.createItemWithTimestamp(6);
        expectedEvent = ListTimeSeriesEvent.createItemsRemovedEvent(
                movingWindowSeries,
                2,
                2,
                Collections.singletonList(item6),
                0
        );
        assertEquals("testListIterator", expectedEvent, countDownListener.getEvents().get(0));

        assertEquals(2, movingWindowSeries.size());
    }

    public void testListIteratorAdd() {
        setStartAndEndTime(4, 7);
        //check that calling add  actually fires an event
        CountDownLatchSeriesListener countDownListener = addCountDownListener();
        ListIterator<TimeSeriesItem> li = movingWindowSeries.listIterator();
        TimeSeriesItem item3 = TimeSeriesTestUtils.createItemWithTimestamp(3);
        li.add(item3);
        failOnCountdownEvent(countDownListener); //3 is outside window

        movingWindowSeries.removeTimeSeriesListener(countDownListener);

        assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(4),li.next());
        assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(5),li.next());
        assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(5),li.next());
        assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(6),li.next());

        countDownListener = addCountDownListener();
        TimeSeriesItem item7 = TimeSeriesTestUtils.createItemWithTimestamp(7);
        li.add(item7);
        waitForCountdown(countDownListener);
        ListTimeSeriesEvent expectedEvent = ListTimeSeriesEvent.createItemsAddedOrInsertedEvent(
                movingWindowSeries,
                4,
                4,
                Collections.singletonList(item7),
                0
        );
        assertEquals("testListIterator", expectedEvent, countDownListener.getEvents().get(0));
        movingWindowSeries.removeTimeSeriesListener(countDownListener);

        countDownListener = addCountDownListener();
        TimeSeriesItem item8 = TimeSeriesTestUtils.createItemWithTimestamp(8);
        li.add(item8);
        failOnCountdownEvent(countDownListener); //8 is outside window

        assertEquals(5, movingWindowSeries.size());
        setStartAndEndTime(0, 1000);
        assertEquals(9, movingWindowSeries.size());
    }

    public void testChangingStartAndEndInvalidateIterator() {
        ListIterator<TimeSeriesItem> li = movingWindowSeries.listIterator();
        li.next();
        movingWindowSeries.setStartTime(1000);
        try {
            li.next();
            fail("Should cause concurrent mod");
        } catch(ConcurrentModificationException c) {}

        setStartAndEndTime(4, 6);
        li = movingWindowSeries.listIterator();
        li.next();
        movingWindowSeries.setEndTime(1000);
        try {
            li.next();
            fail("Should cause concurrent mod");
        } catch(ConcurrentModificationException c) {}

        setStartAndEndTime(4, 6);
        li = movingWindowSeries.subList(0, movingWindowSeries.size()).listIterator();
        li.next();
        movingWindowSeries.setStartTime(1000);
        try {
            li.next();
            fail("Should cause concurrent mod");
        } catch(ConcurrentModificationException c) {}

        setStartAndEndTime(4, 6);
        li = movingWindowSeries.subList(0, movingWindowSeries.size()).listIterator();
        li.next();
        movingWindowSeries.setEndTime(1000);
        try {
            li.next();
            fail("Should cause concurrent mod");
        } catch(ConcurrentModificationException c) {}

    }

    public void testSubList() {
        List l = movingWindowSeries.subList(0, 1);
        assertEquals(TimeSeriesTestUtils.createItemsForTimestamps(4), l);

        l = movingWindowSeries.subList(1, 2);
        assertEquals(TimeSeriesTestUtils.createItemsForTimestamps(5), l);

        CountDownLatchSeriesListener countDownListener = addCountDownListener();
        l.remove(0);
        waitForCountdown(countDownListener);
        TimeSeriesItem item5 = TimeSeriesTestUtils.createItemWithTimestamp(5);
        ListTimeSeriesEvent expectedEvent = ListTimeSeriesEvent.createItemsRemovedEvent(
                movingWindowSeries,
                1,
                1,
                Collections.singletonList(item5),
                0
        );
        assertEquals("testListIterator", expectedEvent, countDownListener.getEvents().get(0));
        assertEquals(3, movingWindowSeries.size());

        try {
            l = movingWindowSeries.subList(0, 4);
            fail("There should now be only 3 items it movable window");
        } catch ( IndexOutOfBoundsException i){}

        l = movingWindowSeries.subList(1, 3);
        assertEquals(TimeSeriesTestUtils.createItemsForTimestamps(5, 6), l);
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
