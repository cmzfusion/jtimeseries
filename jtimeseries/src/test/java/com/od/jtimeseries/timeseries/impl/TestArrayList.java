package com.od.jtimeseries.timeseries.impl;

import com.od.jtimeseries.timeseries.TimeSeriesItem;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 01-Mar-2009
 * Time: 19:59:32
 * To change this template use File | Settings | File Templates.
 *
 * Clearly we don't really need to test ArrayList itself
 * This is here to validate that TestList works OK for a correctly implemented list
 */
public class TestArrayList extends AbstractListTest {

    List<TimeSeriesItem> getListInstance() {
        return new ArrayList<TimeSeriesItem>();
    }
}
