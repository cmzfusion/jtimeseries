package com.od.jtimeseries.capture.impl;

import com.od.jtimeseries.scheduling.GroupByPeriodScheduler;
import com.od.jtimeseries.scheduling.Scheduler;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 01/11/11
 *
 * Time: 23:29
 */
public class TestTimedCaptureWithGroupByPeriodScheduler extends AbstractTestTimedCaptureScheduling {

    protected Scheduler createScheduler() {
        return new GroupByPeriodScheduler();
    }

}

