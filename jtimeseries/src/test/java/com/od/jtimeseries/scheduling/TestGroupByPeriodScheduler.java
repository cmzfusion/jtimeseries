package com.od.jtimeseries.scheduling;

import com.od.jtimeseries.scheduling.Scheduler;
import com.od.jtimeseries.scheduling.GroupByPeriodScheduler;
import com.od.jtimeseries.util.time.TimePeriod;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 21-Feb-2009
 * Time: 19:28:30
 * To change this template use File | Settings | File Templates.
 */
public class TestGroupByPeriodScheduler extends AbstractSchedulerTest {

    private volatile boolean testPassed = true;
    private long firstGroupCaptureTime;

    protected Scheduler createCaptureScheduler() {
        return new GroupByPeriodScheduler();
    }

    @Test
    //n.b. there is also an assumption here that the captures in the group by period are triggered in the order they
    //are added to the scheduler
    public void testGroupByCapturePeriodSchedulerTriggersCaptureGroupsAtTheSameTimepoint() {
        scheduler.addTriggerable(new DummyTimedCapture() {{
                start();
            }

            public void trigger(long timestamp) {
                firstGroupCaptureTime = timestamp;
            }

            public TimePeriod getTriggerPeriod() {
                return CAPTURE_PERIOD_MILLIS;
            }
        });

        scheduler.addTriggerable(new DummyTimedCapture() {{
                start();
            }

            public void trigger(long timestamp) {
                if ( timestamp != firstGroupCaptureTime) {
                    testPassed = false;
                }
            }

            public TimePeriod getTriggerPeriod() {
                return CAPTURE_PERIOD_MILLIS;
            }
        });

        scheduler.start();
        sleepCapturePeriods(2);
        assertTrue(testPassed);
    }
}
