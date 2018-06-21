package com.od.jtimeseries.server.timeseries;

import com.od.jtimeseries.timeseries.IndexedTimeSeries;
import com.od.jtimeseries.timeseries.Item;
import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.timeseries.impl.DefaultTimeSeries;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 31/01/11
 * Time: 08:09
 */
class TimeSeriesTestUtils {
    
    static TimeSeriesItem createItemWithTimestamp(long timestamp) {
        return new Item(timestamp, 0L);
    }

}
