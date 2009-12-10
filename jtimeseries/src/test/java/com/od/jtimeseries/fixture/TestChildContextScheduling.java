package com.od.jtimeseries.fixture;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.util.AbstractSimpleCaptureFixture;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.scheduling.DefaultScheduler;
import com.od.jtimeseries.scheduling.Triggerable;
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

        counter = rootContext.createCounter("TestCounter", "Test Counter Description", CaptureFunctions.CHANGE(capturePeriod));
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
        childContext2.setScheduler(new DefaultScheduler("SchedulerId", "SchedulerDescription"));

        //the new scheduler should now own the eventTimer capture
        assertTrue(childContext2.getScheduler().containsTriggerable(
            (Triggerable)childContext2.findCaptures(eventTimer).getFirstMatch())
        );

        testCapture();
    }


    private void testCapture() {
        WaitForEndOfCapturePeriodListener countDownListener = getCapturePeriodListener();
        rootContext.startScheduling().startDataCapture();

        //wait for each capture to trigger, so that we are in the first capture period, and record some values
        countDownListener.waitForAll();
        counter.incrementCount();
        valueRecorder.newValue(10);
        eventTimer.startEventTimer();
        sleep(sleepPeriod);
        eventTimer.stopEventTimer();

        //wait for the next capture period to start (after which the values we recorded in the first period
        //should have been committed and then stop the scheduling
        countDownListener.waitForAll();
        rootContext.stopDataCapture().stopScheduling();

        //we should then have recorded one value in each series
        for ( IdentifiableTimeSeries s : rootContext.findAllTimeSeries().getAllMatches()) {
            assertEquals(1, s.size());
        }
    }

}
