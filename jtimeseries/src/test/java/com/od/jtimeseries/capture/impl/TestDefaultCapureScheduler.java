/**
 *  This file is part of ObjectDefinitions SwingBench
 *  Copyright (C) Nick Ebbutt 02 2009
 */
package com.od.jtimeseries.capture.impl;

import com.od.jtimeseries.capture.CaptureScheduler;
import com.od.jtimeseries.capture.impl.DefaultCaptureScheduler;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 19-Feb-2009
 * Time: 17:34:57
 */
public class TestDefaultCapureScheduler extends AbstractTestCaptureScheduler {

    protected CaptureScheduler createCaptureScheduler() {
        return new DefaultCaptureScheduler("TestId", "TestDescription");
    }
}
