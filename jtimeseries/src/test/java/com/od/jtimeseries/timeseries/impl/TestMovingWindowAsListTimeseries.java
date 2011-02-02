package com.od.jtimeseries.timeseries.impl;

import com.od.jtimeseries.timeseries.ListTimeSeries;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 02/02/11
 * Time: 09:19
 */
public class TestMovingWindowAsListTimeseries extends AbstractListTimeSeriesTest {

    @Override
    public ListTimeSeries getTimeSeriesInstance() throws Exception {
        return new MovingWindowTimeSeries();
    }
}
