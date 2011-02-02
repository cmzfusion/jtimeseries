/**
 * Copyright (C) 2009 Nick Ebbutt (nick@objectdefinitions.com)
 *
 * This file is part of JTimeseries.
 *
 * JTimeseries is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JTimeseries is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JTimeseries.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.od.jtimeseries.timeseries.impl;

import java.util.*;

import com.od.jtimeseries.timeseries.TimeSeriesItem;
import org.junit.Before;
import org.junit.Assert;
import org.junit.Test;
import org.junit.After;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 30-Dec-2008
 * Time: 11:06:59
 *
 * General tests of the List interface for list based timeseries
 */
public abstract class AbstractListTest extends Assert {

    private List<TimeSeriesItem> list;

    @Before
    public void setUp() throws Exception {
        list = getListInstance();
    }

    @After
    public void tearDown() {
        list = null;
    }

    abstract List<TimeSeriesItem> getListInstance() throws Exception;

    public void testRandomAccess() {
        populateList(17);
        assertSame(17, list.size());
        assertSame((Integer)0, list.get(0));
        assertSame((Integer)16, list.get(16));

        list.remove(16);

        assertSame(16, list.size());
        assertSame((Integer)0, list.get(0));
        assertSame((Integer)15, list.get(15));

        list.remove(0);
        list.remove(0);
        list.remove(0);

        assertSame(13, list.size());
        assertSame((Integer)3, list.get(0));
        assertSame((Integer)15, list.get(12));

        for ( int loop=16; loop <= 32; loop++) {
            list.add(TimeSeriesTestUtils.createItemWithTimestamp(loop));
        }
        //the default list time series uses a dequeue internally
        //at this point the dequeue internal contents array starts to wrap around so that 32
        //should be at index 0
        //we need to test that the get() implementation still works when this wrap around takes place

        assertSame(30, list.size());
        assertSame((Integer)3, list.get(0));
        assertSame((Integer)32, list.get(29));

        list.add(TimeSeriesTestUtils.createItemWithTimestamp(33));
        assertSame((Integer)3, list.get(0));
        assertSame(31, list.size());
        assertSame((Integer)33, list.get(30));
    }

    private void populateList(int size) {
        list.clear();
        for ( int loop=0; loop < size; loop ++) {
            list.add(TimeSeriesTestUtils.createItemWithTimestamp(loop));
        }
    }

    @Test
    public void testIteration() {
        int iterations = (int)(Math.random() * 100) + 5;

        //the Deque should implement iteration identically to an ArrayList
        for ( int loop=1; loop < iterations; loop++) {
            list.add(TimeSeriesTestUtils.createItemWithTimestamp(loop));
        }

        Iterator<TimeSeriesItem> i = list.iterator();
        int start = 1;
        while( i.hasNext()) {
            assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(start++), i.next());
        }

        ListIterator li = list.listIterator();
        start = 1;
        while( li.hasNext()) {
            assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(start++), li.next());
        }

        li = list.listIterator(list.size());
        start = list.size();
        while( li.hasPrevious()) {
            assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(start--), li.previous());
        }

        li = list.subList(0, list.size()).listIterator();
        start = 1;
        while( li.hasNext()) {
            assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(start++), li.next());
        }

        li = list.subList(0, list.size()).listIterator(list.size());
        start = list.size();
        while( li.hasPrevious()) {
            assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(start--), li.previous());
        }

        li = list.listIterator();
        assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(1),li.next());
        assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(2),li.next());
        assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(2),li.previous()); //this seems totally illogical to me, but that's how List iterators are defined to work
        assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(1),li.previous());

        start = 1;
        for ( TimeSeriesItem item : list) {
            assertTrue( item.equals(TimeSeriesTestUtils.createItemWithTimestamp(start++)));
        }
    }

    @Test
    public void testMutatingListIteration() {
        //ok, so here I'm just playing around adding and removing, trying to get it to break
        //There seem to be a lot of corner cases, it could do with a more rigorous approach -
        //be my guest, but don't take away any corner cases already covered here
        list.add(TimeSeriesTestUtils.createItemWithTimestamp(2));
        list.add(TimeSeriesTestUtils.createItemWithTimestamp(3));

        ListIterator<TimeSeriesItem> li = list.listIterator();
        li.add(TimeSeriesTestUtils.createItemWithTimestamp(1));
        testContainsConsecutiveNumbersStartingFromOne(list, 3);

        TimeSeriesItem i = li.next();
        assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(2), i);

        li.remove();
        assertEquals(2, list.size());
        assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(1), list.get(0));
        assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(3), list.get(1));

        i = li.next();
        assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(3), i);
        li.remove();

        assertEquals(1, list.size());
        assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(1), list.get(0));

        li.add(TimeSeriesTestUtils.createItemWithTimestamp(2));
        testContainsConsecutiveNumbersStartingFromOne(list, 2);

        li.add(TimeSeriesTestUtils.createItemWithTimestamp(3));
        i = li.previous();
        assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(3), i);

        li.remove();
        testContainsConsecutiveNumbersStartingFromOne(list, 2);

        li.previous();
        li.set(TimeSeriesTestUtils.createItemWithTimestamp(7));
        assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(1), list.get(0));
        assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(7), list.get(1));
    }

    @Test
    public void testAdd() {
        addNewItemsForTimestamps(1,3);
        list.add(1, TimeSeriesTestUtils.createItemWithTimestamp(2));
        testContainsConsecutiveNumbersStartingFromOne(list, 3);
    }

    @Test
    public void testAddAll() throws InterruptedException {
        addNewItemsForTimestamps(1,4);

        List<TimeSeriesItem> toInsert = Arrays.asList(TimeSeriesTestUtils.createItemWithTimestamp(2), TimeSeriesTestUtils.createItemWithTimestamp(3));
        list.addAll(1, toInsert);
        testContainsConsecutiveNumbersStartingFromOne(list, 4);

        list.addAll(Arrays.asList(TimeSeriesTestUtils.createItemWithTimestamp(5), TimeSeriesTestUtils.createItemWithTimestamp(6)));
        testContainsConsecutiveNumbersStartingFromOne(list, 6);
    }

    @Test
    public void testRemove() {
        addNewItemsForTimestamps(1,2,3,4,5);
        list.remove(2);
        list.remove(TimeSeriesTestUtils.createItemWithTimestamp(5));
        checkListContentsByTimestamp(1,2,4);
    }

    @Test
    public void testRemoveAll() {
        addNewItemsForTimestamps(1,2,3,4,5,6);
        list.removeAll(TimeSeriesTestUtils.createItemsWithTimestamps(3, 4));
        checkListContentsByTimestamp(1,2,5,6);
    }

    @Test
    public void testSet() {
        addNewItemsForTimestamps(1,3,4);
        list.set(1, TimeSeriesTestUtils.createItemWithTimestamp(2));
        checkListContentsByTimestamp(1,2,4);
    }

    @Test
    public void testIndexOf() {
        addNewItemsForTimestamps(1,2,2,3);
        assertEquals(1, list.indexOf(TimeSeriesTestUtils.createItemWithTimestamp(2)));
    }

    @Test
    public void testLastIndexOf() {
        addNewItemsForTimestamps(1,2,2,2,3);
        assertEquals(3, list.lastIndexOf(TimeSeriesTestUtils.createItemWithTimestamp(2)));
    }

    @Test
    public void testEqualsAndHashcode() {
        long random = (long)(Math.random() * (Long.MAX_VALUE / 4));
        ArrayList<TimeSeriesItem> l = new ArrayList<TimeSeriesItem>();
        l.add(TimeSeriesTestUtils.createItemWithTimestamp(random));
        l.add(TimeSeriesTestUtils.createItemWithTimestamp(random * 2));
        l.add(TimeSeriesTestUtils.createItemWithTimestamp(random * 3));
        list.add(TimeSeriesTestUtils.createItemWithTimestamp(random));
        list.add(TimeSeriesTestUtils.createItemWithTimestamp(random * 2));
        list.add(TimeSeriesTestUtils.createItemWithTimestamp(random * 3));
        assertEquals(list, l);
        assertEquals(list.hashCode(), l.hashCode());

        list.add(TimeSeriesTestUtils.createItemWithTimestamp(random * 4));
        assertFalse(list.equals(l));
    }

    @Test
    public void testClear() {
        addNewItemsForTimestamps(1,2);
        assertEquals(2, list.size());
        list.clear();
        assertEquals(0, list.size());
    }

    @Test
    public void testContains()  {
        long val = (long)(Math.random() * Long.MAX_VALUE / 2);
        list.add(TimeSeriesTestUtils.createItemWithTimestamp(val));
        list.add(TimeSeriesTestUtils.createItemWithTimestamp(val * 2));
        assertTrue(list.contains(TimeSeriesTestUtils.createItemWithTimestamp(val)));
        assertFalse(list.contains(TimeSeriesTestUtils.createItemWithTimestamp(val - 1)));
    }

    @Test
    public void testContainsAll()  {
        long val = (long)(Math.random() * Long.MAX_VALUE / 2);
        list.add(TimeSeriesTestUtils.createItemWithTimestamp(val));
        list.add(TimeSeriesTestUtils.createItemWithTimestamp(val * 2));
        assertTrue(list.containsAll(Arrays.asList(TimeSeriesTestUtils.createItemWithTimestamp(val), TimeSeriesTestUtils.createItemWithTimestamp(val * 2))));
        assertFalse(list.containsAll(Arrays.asList(TimeSeriesTestUtils.createItemWithTimestamp(val), TimeSeriesTestUtils.createItemWithTimestamp(val - 1))));
    }

    @Test
    public void testRetainAll() {
        long val = (long)(Math.random() * Long.MAX_VALUE / 3);
        list.add(TimeSeriesTestUtils.createItemWithTimestamp(val));
        list.add(TimeSeriesTestUtils.createItemWithTimestamp(val * 2));
        list.add(TimeSeriesTestUtils.createItemWithTimestamp(val * 3));
        list.retainAll(Arrays.asList(TimeSeriesTestUtils.createItemWithTimestamp(val * 2)));
        assertEquals(1, list.size());
        assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(val * 2), list.get(0));
    }

    protected void testContainsConsecutiveNumbersStartingFromOne(List<TimeSeriesItem> l, int size) {
        assertEquals(size, l.size());
        for ( int count=0; count < size; count++) {
            assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(count + 1), l.get(count));
        }
    }

    protected void addNewItemsForTimestamps(long... timestampsForItems) {
        for (long timestampsForItem : timestampsForItems) {
            list.add(TimeSeriesTestUtils.createItemWithTimestamp(timestampsForItem));
        }
    }


    protected void checkListContentsByTimestamp(long... timestampsForItems) {
        for (int loop=0; loop<timestampsForItems.length; loop++) {
            assertEquals(TimeSeriesTestUtils.createItemWithTimestamp(timestampsForItems[loop]), list.get(loop));
        }
    }

}
