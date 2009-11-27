/**
 *  This file is part of ObjectDefinitions SwingBench
 *  Copyright (C) Nick Ebbutt 02 2009
 */
package com.od.jtimeseries.scheduling;

import com.od.jtimeseries.scheduling.Scheduler;
import com.od.jtimeseries.scheduling.DefaultScheduler;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 19-Feb-2009
 * Time: 17:34:57
 */
public class TestDefaultScheduler extends AbstractSchedulerTest {

    protected Scheduler createCaptureScheduler() {
        return new DefaultScheduler("TestId", "TestDescription");
    }
}
