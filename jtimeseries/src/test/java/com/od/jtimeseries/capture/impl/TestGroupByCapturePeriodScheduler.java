package com.od.jtimeseries.capture.impl;

import com.od.jtimeseries.capture.CaptureScheduler;
import com.od.jtimeseries.capture.impl.GroupByCapturePeriodScheduler;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 21-Feb-2009
 * Time: 19:28:30
 * To change this template use File | Settings | File Templates.
 */
public class TestGroupByCapturePeriodScheduler extends AbstractTestCaptureScheduler {

    private volatile boolean testPassed = true;
    private long firstGroupCaptureTime;

    protected CaptureScheduler createCaptureScheduler() {
        return new GroupByCapturePeriodScheduler("TestId", "TestDescription");
    }

    @Test
    //n.b. there is also an assumption here that the captures in the group by period are triggered in the order they
    //are added to the scheduler
    public void testGroupByCapturePeriodSchedulerTriggersCaptureGroupsAtTheSameTimepoint() {
        scheduler.addCapture(new DummyTimedCapture() {{
                start();
            }

            public void triggerCapture(long timestamp) {
                firstGroupCaptureTime = timestamp;
            }

            public long getCapturePeriodInMilliseconds() {
                return CAPTURE_PERIOD_MILLIS;
            }
        });

        scheduler.addCapture(new DummyTimedCapture() {{
                start();
            }

            public void triggerCapture(long timestamp) {
                if ( timestamp != firstGroupCaptureTime) {
                    testPassed = false;
                }
            }

            public long getCapturePeriodInMilliseconds() {
                return CAPTURE_PERIOD_MILLIS;
            }
        });

        scheduler.start();
        sleepCapturePeriods(2);
        assertTrue(testPassed);
    }
}
