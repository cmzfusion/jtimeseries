package com.od.jtimeseries.fixture;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.util.JTimeSeriesTestConstants;
import com.od.jtimeseries.util.AbstractSimpleCaptureFixture;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.capture.impl.DefaultCaptureScheduler;
import com.od.jtimeseries.capture.function.CaptureFunctions;

import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 22-Feb-2009
 * Time: 11:18:53
 * To change this template use File | Settings | File Templates.
 */
public class TestChildContextScheduling extends AbstractSimpleCaptureFixture {
    
    private TimeSeriesContext childContext;
    private TimeSeriesContext childContext2;

    protected void doExtraSetUp() {
        childContext = rootContext.createChildContext("child1");
        childContext2 = childContext.createChildContext("child2", "child 2 description");

        counter = rootContext.createCounter("TestCounter", "Test Counter Description", CaptureFunctions.COUNT(capturePeriod));
        valueRecorder = childContext.createValueRecorder("TestValueRecorder", "Test Value Recorder", CaptureFunctions.MEAN(capturePeriod));
        eventTimer = childContext2.createEventTimer("TestEventTimer", "Test Event Timer", CaptureFunctions.MEAN(capturePeriod));
    }

    @Test
    public void whenChildContextsDoNotSpecifyASchedulerTheyInheritTheParentScheduler() {
        assertSame(rootContext.getScheduler(), childContext2.getScheduler());
        testCapture();
    }

    @Test
    public void whenAChildContextHasItsOwnSchedulerStartingParentContextSchedulingAlsoStartsTheChildScheduler() {

        //set a new child scheduler on context2
        childContext2.setScheduler(new DefaultCaptureScheduler("SchedulerId", "SchedulerDescription"));

        //the new scheduler should now own the eventTimer capture
        assertTrue(childContext2.getScheduler().containsCapture(
                childContext2.findCaptures(eventTimer).getFirstMatch())
        );

        testCapture();
    }


    private void testCapture() {
        rootContext.startScheduling().startDataCapture();
        long startTime = System.currentTimeMillis();
        sleep(JTimeSeriesTestConstants.periodToWaitForFirstScheduledTimePeriodToStart);

        counter.incrementCount();
        valueRecorder.newValue(10);
        eventTimer.startEventTimer();
        sleep(sleepPeriod);
        eventTimer.stopEventTimer();
        sleepUntilCapturePeriodOver(capturePeriod, startTime);

        rootContext.stopDataCapture().stopScheduling();

        for ( IdentifiableTimeSeries s : rootContext.findAllTimeSeries().getAllMatches()) {
            assertEquals(1, s.size());
        }
    }


}
