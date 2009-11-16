package com.od.jtimeseries.timeseries.impl;

import com.od.jtimeseries.timeseries.TimeSeriesItem;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 01-Mar-2009
 * Time: 19:58:53
 * To change this template use File | Settings | File Templates.
 */
public class TestDequeList extends AbstractListTest {

    List<TimeSeriesItem> getListInstance() {
        return new RandomAccessDeque<TimeSeriesItem>();
    }
}
