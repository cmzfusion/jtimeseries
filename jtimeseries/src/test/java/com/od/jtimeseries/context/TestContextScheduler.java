package com.od.jtimeseries.context;

import com.od.jtimeseries.scheduling.NonGroupingScheduler;
import com.od.jtimeseries.source.Counter;
import com.od.jtimeseries.util.AbstractSimpleCaptureFixture;
import org.junit.After;
import org.junit.Test;

import static com.od.jtimeseries.capture.function.CaptureFunctions.MEAN;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 22-Feb-2009
 * Time: 15:15:17
 * To change this template use File | Settings | File Templates.
 */
public class TestContextScheduler extends AbstractSimpleCaptureFixture {

    private TimeSeriesContext childContext;
    private Counter childcounter;
    private TimeSeriesContext grandchildContext;
    private Counter grandchildCounter;

    protected void doExtraSetUp() {
        childContext = rootContext.createContext("child");
        grandchildContext = childContext.createContext("granchild");

        counter = rootContext.createCounterSeries("counter", "counter", MEAN(capturePeriod));
        childcounter = childContext.createCounterSeries("childcounter", "childcounter", MEAN(capturePeriod));
        grandchildCounter = grandchildContext.createCounterSeries("grandchildCounter", "grandchildCounter", MEAN(capturePeriod));
    }

    @After
    public void tearDown() {
        super.tearDown();
        childContext = null;
        childcounter = null;
        grandchildContext = null;
        grandchildCounter = null;
    }

    @Test
    public void testNewCapturesAreAssignedToRootScheduler() {
        assertTrue(rootContext.getScheduler().containsTriggerable(rootContext.findCaptures(counter).getFirstMatch()));
        assertTrue(rootContext.getScheduler().containsTriggerable(rootContext.findCaptures(childcounter).getFirstMatch()));
        assertTrue(rootContext.getScheduler().containsTriggerable(rootContext.findCaptures(grandchildCounter).getFirstMatch()));
    }

    @Test
    public void testNewSchedulerForGrandchildPicksUpTheGrandchildCaptures() {
        rootContext.stopScheduling();
        grandchildContext.setScheduler(new NonGroupingScheduler());
        assertNotSame(rootContext.getScheduler(), grandchildContext.getScheduler());
        assertFalse(rootContext.getScheduler().containsTriggerable(rootContext.findCaptures(grandchildCounter).getFirstMatch()));
        assertTrue(grandchildContext.getScheduler().containsTriggerable(rootContext.findCaptures(grandchildCounter).getFirstMatch()));
    }

    @Test
    public void testNewSchedulerForChildPicksUpTheChildAndGrandchildCapturesIfGranchildDoesNotHaveItsOwnScheduler() {
        rootContext.stopScheduling();
        childContext.setScheduler(new NonGroupingScheduler());

        assertNotSame(rootContext.getScheduler(), childContext.getScheduler());
        assertNotSame(rootContext.getScheduler(), grandchildContext.getScheduler());

        assertEquals(1, rootContext.getScheduler().getTriggerables().size());
        assertEquals(2, childContext.getScheduler().getTriggerables().size());
        assertEquals(2, grandchildContext.getScheduler().getTriggerables().size());

        assertTrue(rootContext.getScheduler().containsTriggerable(rootContext.findCaptures(counter).getFirstMatch()));
        assertTrue(childContext.getScheduler().containsTriggerable(rootContext.findCaptures(childcounter).getFirstMatch()));
        assertTrue(childContext.getScheduler().containsTriggerable(rootContext.findCaptures(grandchildCounter).getFirstMatch()));
    }

    @Test
    public void testSettingNewSchedulerForChildDoesNotPickUpGrandchildCapturesIfGrandchildHasItsOwnScheduler() {
        rootContext.stopScheduling();
        grandchildContext.setScheduler(new NonGroupingScheduler());
        childContext.setScheduler(new NonGroupingScheduler());

        assertNotSame(rootContext.getScheduler(), grandchildContext.getScheduler());
        assertNotSame(rootContext.getScheduler(), childContext.getScheduler());
        assertNotSame(grandchildContext.getScheduler(), childContext.getScheduler());

        assertEquals(1, rootContext.getScheduler().getTriggerables().size());
        assertEquals(1, childContext.getScheduler().getTriggerables().size());
        assertEquals(1, grandchildContext.getScheduler().getTriggerables().size());

        assertTrue(rootContext.getScheduler().containsTriggerable(rootContext.findCaptures(counter).getFirstMatch()));
        assertTrue(childContext.getScheduler().containsTriggerable(childContext.findCaptures(childcounter).getFirstMatch()));
        assertTrue(grandchildContext.getScheduler().containsTriggerable(rootContext.findCaptures(grandchildCounter).getFirstMatch()));
    }

   @Test
    public void testStartAndStoppingSchedulingAffectsLocalSchedulerAndThoseForChildNodes() {
        rootContext.stopScheduling();
        grandchildContext.setScheduler(new NonGroupingScheduler());
        childContext.setScheduler(new NonGroupingScheduler());
        childContext.startScheduling();

        assertFalse(rootContext.getScheduler().isStarted());
        assertTrue(childContext.getScheduler().isStarted());
        assertTrue(grandchildContext.getScheduler().isStarted());

        childContext.stopScheduling();
        assertFalse(childContext.getScheduler().isStarted());
        assertFalse(grandchildContext.getScheduler().isStarted());
    }


}
