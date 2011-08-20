package com.od.jtimeseries.timeseries.impl;

import com.od.jtimeseries.timeseries.DefaultTimeSeriesItem;
import com.od.jtimeseries.timeseries.IndexedTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.util.numeric.LongNumeric;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 31/01/11
 * Time: 08:09
 */
public class TimeSeriesTestUtils {

    public static List<TimeSeriesItem> createItemsWithTimestamps(long... timestampsForItems) {
        List<TimeSeriesItem> items = new ArrayList<TimeSeriesItem>();
        for (long timestampsForItem : timestampsForItems) {
            items.add(createItemWithTimestamp(timestampsForItem));
        }
        return items;
    }

    public static TimeSeriesItem createItemWithTimestamp(long timestamp) {
        return new DefaultTimeSeriesItem(timestamp, LongNumeric.valueOf(0));
    }

    public static TimeSeriesItem createItemWithTimestamp(long timestamp, long value) {
        return new DefaultTimeSeriesItem(timestamp, LongNumeric.valueOf(value));
    }

    public static IndexedTimeSeries createSeriesWithItems(int... timestamps) {
        IndexedTimeSeries test = new DefaultTimeSeries();
        for (int stamp : timestamps) {
            test.addItem(createItemWithTimestamp(stamp));
        }
        return test;
    }
}
