package com.od.jtimeseries.capture.impl;

import com.od.jtimeseries.scheduling.NonGroupingScheduler;
import com.od.jtimeseries.scheduling.Scheduler;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 01/11/11
 * Time: 23:28
 */
public class TestTimedCaptureWithNonGroupingScheduler extends AbstractTestTimedCaptureScheduling {

    protected  Scheduler createScheduler() {
        return new NonGroupingScheduler();
    }

}
