package com.od.jtimeseries.context;

import com.od.jtimeseries.capture.function.CaptureFunctions;
import com.od.jtimeseries.capture.impl.DefaultCaptureScheduler;
import com.od.jtimeseries.util.AbstractSimpleCaptureFixture;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.source.Counter;
import org.junit.Test;
import org.junit.After;

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
        childContext = rootContext.createChildContext("child");
        grandchildContext = childContext.createChildContext("granchild");

        counter = rootContext.createCounter("counter", "counter", CaptureFunctions.MEAN(capturePeriod));
        childcounter = childContext.createCounter("childcounter", "childcounter", CaptureFunctions.MEAN(capturePeriod));
        grandchildCounter = grandchildContext.createCounter("grandchildCounter", "grandchildCounter", CaptureFunctions.MEAN(capturePeriod));
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
        assertTrue(rootContext.getScheduler().containsCapture(rootContext.findCaptures(counter).getFirstMatch()));
        assertTrue(rootContext.getScheduler().containsCapture(rootContext.findCaptures(childcounter).getFirstMatch()));
        assertTrue(rootContext.getScheduler().containsCapture(rootContext.findCaptures(grandchildCounter).getFirstMatch()));
    }

    @Test
    public void testNewSchedulerForGrandchildPicksUpTheGrandchildCaptures() {
        grandchildContext.setScheduler(new DefaultCaptureScheduler("test", "test"));
        assertNotSame(rootContext.getScheduler(), grandchildContext.getScheduler());
        assertFalse(rootContext.getScheduler().containsCapture(rootContext.findCaptures(grandchildCounter).getFirstMatch()));
        assertTrue(grandchildContext.getScheduler().containsCapture(rootContext.findCaptures(grandchildCounter).getFirstMatch()));
    }

    @Test
    public void testNewSchedulerForChildPicksUpTheChildAndGrandchildCapturesIfGranchildDoesNotHaveItsOwnScheduler() {
        childContext.setScheduler(new DefaultCaptureScheduler("test", "test"));

        assertNotSame(rootContext.getScheduler(), childContext.getScheduler());
        assertNotSame(rootContext.getScheduler(), grandchildContext.getScheduler());

        assertEquals(1, rootContext.getScheduler().getCaptures().size());
        assertEquals(2, childContext.getScheduler().getCaptures().size());
        assertEquals(2, grandchildContext.getScheduler().getCaptures().size());

        assertTrue(rootContext.getScheduler().containsCapture(rootContext.findCaptures(counter).getFirstMatch()));
        assertTrue(childContext.getScheduler().containsCapture(rootContext.findCaptures(childcounter).getFirstMatch()));
        assertTrue(childContext.getScheduler().containsCapture(rootContext.findCaptures(grandchildCounter).getFirstMatch()));
    }

    @Test
    public void testSettingNewSchedulerForChildDoesNotPickUpGrandchildCapturesIfGrandchildHasItsOwnScheduler() {
        grandchildContext.setScheduler(new DefaultCaptureScheduler("grandchildsched", "grandchildsched"));
        childContext.setScheduler(new DefaultCaptureScheduler("childsched", "childsched"));

        assertNotSame(rootContext.getScheduler(), grandchildContext.getScheduler());
        assertNotSame(rootContext.getScheduler(), childContext.getScheduler());
        assertNotSame(grandchildContext.getScheduler(), childContext.getScheduler());

        assertEquals(1, rootContext.getScheduler().getCaptures().size());
        assertEquals(1, childContext.getScheduler().getCaptures().size());
        assertEquals(1, grandchildContext.getScheduler().getCaptures().size());

        assertTrue(rootContext.getScheduler().containsCapture(rootContext.findCaptures(counter).getFirstMatch()));
        assertTrue(childContext.getScheduler().containsCapture(childContext.findCaptures(childcounter).getFirstMatch()));
        assertTrue(grandchildContext.getScheduler().containsCapture(rootContext.findCaptures(grandchildCounter).getFirstMatch()));
    }

   @Test
    public void testStartAndStoppingSchedulingAffectsLocalSchedulerAndThoseForChildNodes() {
        grandchildContext.setScheduler(new DefaultCaptureScheduler("grandchildsched", "grandchildsched"));
        childContext.setScheduler(new DefaultCaptureScheduler("childsched", "childsched"));
        childContext.startScheduling();

        assertFalse(rootContext.getScheduler().isStarted());
        assertTrue(childContext.getScheduler().isStarted());
        assertTrue(grandchildContext.getScheduler().isStarted());

        childContext.stopScheduling();
        assertFalse(childContext.getScheduler().isStarted());
        assertFalse(grandchildContext.getScheduler().isStarted());
    }


}
