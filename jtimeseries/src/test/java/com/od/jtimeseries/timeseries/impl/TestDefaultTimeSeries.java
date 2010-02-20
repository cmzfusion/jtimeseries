package com.od.jtimeseries.timeseries.impl;

import com.od.jtimeseries.timeseries.ListTimeSeries;
import org.junit.Test;
import org.junit.After;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 01-Mar-2009
 * Time: 21:12:41
 * To change this template use File | Settings | File Templates.
 */
public class TestDefaultTimeSeries extends AbstractListTimeSeriesTest<DefaultTimeSeries> {


    public DefaultTimeSeries getTimeSeriesInstance() {
        return new DefaultTimeSeries();
    }

    @After
    public void tearDown() {
        super.tearDown();
    }

    @Test
    public void testBinarySearchForTimestamp() {
        addNewItemsForTimestamps(10,20,30,40,50);
        assertEquals(1, getTimeSeries().binarySearchForItemWithTimestamp(20));
        assertEquals(4, getTimeSeries().binarySearchForItemWithTimestamp(50));
        assertEquals(-2, getTimeSeries().binarySearchForItemWithTimestamp(15));
        assertEquals(-6, getTimeSeries().binarySearchForItemWithTimestamp(60));
    }


}
