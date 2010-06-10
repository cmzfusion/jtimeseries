package com.od.jtimeseries.context;

import org.junit.Test;
import com.od.jtimeseries.context.impl.DefaultContextFactory;
import com.od.jtimeseries.util.AbstractSimpleCaptureFixture;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesFactory;
import com.od.jtimeseries.timeseries.impl.DefaultTimeSeriesFactory;
import com.od.jtimeseries.scheduling.DefaultScheduler;
import com.od.jtimeseries.capture.impl.DefaultCaptureFactory;
import com.od.jtimeseries.capture.Capture;
import com.od.jtimeseries.capture.CaptureFactory;
import com.od.jtimeseries.capture.CaptureState;
import com.od.jtimeseries.source.ValueSource;
import com.od.jtimeseries.source.ValueSourceFactory;
import com.od.jtimeseries.source.Counter;
import com.od.jtimeseries.source.impl.DefaultValueSourceFactory;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 23-Feb-2009
 * Time: 12:07:18
 * To change this template use File | Settings | File Templates.
 */
public class TestContext extends AbstractSimpleCaptureFixture {

    private TimeSeriesContext childContext;

    protected void doExtraSetUp() {
        rootContext.setProperty(ContextProperties.START_CAPTURES_IMMEDIATELY_PROPERTY, "false");
        counter = rootContext.createCounterSeries("TestCounter", "Test Counter Description");
        valueRecorder = rootContext.createValueRecorderSeries("TestValueRecorder", "Test Value Recorder");
        eventTimer = rootContext.createEventTimerSeries("TestEventTimer", "Test Event Timer");
        queueTimer = rootContext.createQueueTimerSeries("TestQueueTimer", "Test Queue Timer");

        childContext = rootContext.createContext("Child");
    }

    @Test
    public void testGetRootContext() {
        assertSame(rootContext, rootContext.getRoot());
        assertSame(rootContext, childContext.getRoot());
    }

    @Test
    public void testGetSources() {
        assertSame(counter, rootContext.getSource(counter.getId()));

        assertEquals(4, rootContext.getSources().size());
        assertTrue(rootContext.getSources().contains(counter));
        assertTrue(rootContext.getSources().contains(valueRecorder));
        assertTrue(rootContext.getSources().contains(eventTimer));
        assertTrue(rootContext.getSources().contains(queueTimer));
    }

    @Test
    public void testGetCaptures() {
        Capture counterCapture = rootContext.findCaptures(counter).getFirstMatch();
        assertSame(counterCapture, rootContext.getCapture(counterCapture.getId()));

        assertEquals(4, rootContext.getCaptures().size());
        assertTrue(rootContext.getCaptures().contains(rootContext.findCaptures(counter).getFirstMatch()));
        assertTrue(rootContext.getCaptures().contains(rootContext.findCaptures(valueRecorder).getFirstMatch()));
        assertTrue(rootContext.getCaptures().contains(rootContext.findCaptures(eventTimer).getFirstMatch()));
        assertTrue(rootContext.getCaptures().contains(rootContext.findCaptures(queueTimer).getFirstMatch()));
    }

    @Test
    public void testGetTimeSeries() {
        IdentifiableTimeSeries timeSeries = rootContext.findTimeSeries(counter).getFirstMatch();
        assertSame(timeSeries, rootContext.getTimeSeries(timeSeries.getId()));

        assertEquals(4, rootContext.getTimeSeries().size());
        assertTrue(rootContext.getTimeSeries().contains(rootContext.findTimeSeries(counter).getFirstMatch()));
        assertTrue(rootContext.getTimeSeries().contains(rootContext.findTimeSeries(valueRecorder).getFirstMatch()));
        assertTrue(rootContext.getTimeSeries().contains(rootContext.findTimeSeries(eventTimer).getFirstMatch()));
        assertTrue(rootContext.getTimeSeries().contains(rootContext.findTimeSeries(queueTimer).getFirstMatch()));
    }

    @Test
    public void testGetChildContexts() {
        assertSame(childContext, rootContext.getContext(childContext.getId()));

        assertEquals(1, rootContext.getChildContexts().size());
        assertSame(childContext, rootContext.getChildContexts().get(0));
    }

    @Test
    public void testGetById() {
        assertSame(childContext, rootContext.get(childContext.getId()));
        assertNull(rootContext.get("wibble"));

        assertSame(counter, rootContext.get(counter.getId(), ValueSource.class));
        assertNull(rootContext.get("Wibble", ValueSource.class));

        //the identifiable with counter id is not a time series, so this should throw an exception
        try {
            rootContext.get(counter.getId(), IdentifiableTimeSeries.class);
            fail("Should throw a WrongClassTypeException");
        } catch ( WrongClassTypeException w ) {
        } catch ( Throwable t) {
            fail("Expected WrongClassTypeException");
        }
    }

    @Test
    public void testContainsChild() {
        assertTrue(rootContext.containsChildWithId(childContext.getId()));
        assertFalse(rootContext.containsChildWithId("Wibble"));
    }

    @Test
    public void testRootCanChildContextShareSameFactoriesInitially() {
        ValueSourceFactory sourceFactory = rootContext.getValueSourceFactory();
        assertNotNull(sourceFactory);
        assertSame(sourceFactory, childContext.getValueSourceFactory());

        CaptureFactory captureFactory = rootContext.getCaptureFactory();
        assertNotNull(captureFactory);
        assertSame(captureFactory, childContext.getCaptureFactory());

        TimeSeriesFactory timeSeriesFactory = rootContext.getTimeSeriesFactory();
        assertNotNull(timeSeriesFactory);
        assertSame(timeSeriesFactory, childContext.getTimeSeriesFactory());

        ContextFactory contextFactory = rootContext.getContextFactory();
        assertNotNull(contextFactory);
        assertSame(contextFactory, childContext.getContextFactory());
    }

    @Test
    public void testNewChildInheritsParentsFactories() {
        ValueSourceFactory sourceFactory = rootContext.getValueSourceFactory();
        CaptureFactory captureFactory = rootContext.getCaptureFactory();
        TimeSeriesFactory timeSeriesFactory = rootContext.getTimeSeriesFactory();
        ContextFactory contextFactory = rootContext.getContextFactory();

        rootContext.createContext("newchild");
        TimeSeriesContext child = rootContext.getContext("newchild");
        assertSame(sourceFactory, child.getValueSourceFactory());
        assertSame(captureFactory, child.getCaptureFactory());
        assertSame(timeSeriesFactory, child.getTimeSeriesFactory());
        assertSame(contextFactory, child.getContextFactory());
    }

    @Test
    public void testChangingRootFactoriesAffectsChildContextWhenChildHasNoLocalFactory() {
        ValueSourceFactory newSourceFactory = new DefaultValueSourceFactory("newSourceFactory", "test");
        rootContext.setValueSourceFactory(newSourceFactory);
        assertSame(newSourceFactory, rootContext.getValueSourceFactory());
        assertSame(newSourceFactory, childContext.getValueSourceFactory());

        CaptureFactory newCaptureFactory = new DefaultCaptureFactory("newCaptureFactory", "test");
        rootContext.setCaptureFactory(newCaptureFactory);
        assertSame(newCaptureFactory, rootContext.getCaptureFactory());
        assertSame(newCaptureFactory, childContext.getCaptureFactory());

        TimeSeriesFactory newTimeSeriesFactory = new DefaultTimeSeriesFactory("newTimeSeriesFactory", "test");
        rootContext.setTimeSeriesFactory(newTimeSeriesFactory);
        assertSame(newTimeSeriesFactory, rootContext.getTimeSeriesFactory());
        assertSame(newTimeSeriesFactory, childContext.getTimeSeriesFactory());

        ContextFactory newContextFactory = new DefaultContextFactory("newContextFactory", "test");
        rootContext.setContextFactory(newContextFactory);
        assertSame(newContextFactory, rootContext.getContextFactory());
        assertSame(newContextFactory, childContext.getContextFactory());
    }

    @Test
    public void testChangingChildContextFactoriesDoesNotAffectRootContext() {
        ValueSourceFactory newSourceFactory = new DefaultValueSourceFactory("newSourceFactory", "test");
        childContext.setValueSourceFactory(newSourceFactory);
        assertNotSame(rootContext.getValueSourceFactory(), childContext.getValueSourceFactory());

        CaptureFactory newCaptureFactory = new DefaultCaptureFactory("newCaptureFactory", "test");
        childContext.setCaptureFactory(newCaptureFactory);
        assertNotSame(rootContext.getCaptureFactory(), childContext.getCaptureFactory());

        TimeSeriesFactory newTimeSeriesFactory = new DefaultTimeSeriesFactory("newTimeSeriesFactory", "test");
        childContext.setTimeSeriesFactory(newTimeSeriesFactory);
        assertNotSame(rootContext.getTimeSeriesFactory(), childContext.getTimeSeriesFactory());

        ContextFactory newContextFactory = new DefaultContextFactory("newContextFactory", "test");
        childContext.setContextFactory(newContextFactory);
        assertNotSame(rootContext.getContextFactory(), childContext.getContextFactory());
    }

    @Test
    public void testAddingFactoryInstanceToContextReplacesExistingFactory() {
        ValueSourceFactory valueSourceFactory = rootContext.getValueSourceFactory();
        rootContext.addChild(new DefaultValueSourceFactory("valueSourceFactory", "test"));
        assertNotSame(valueSourceFactory, rootContext.getValueSourceFactory());
        assertEquals(1, rootContext.getChildren(ValueSourceFactory.class).size());

        CaptureFactory captureFactory = rootContext.getCaptureFactory();
        rootContext.addChild(new DefaultCaptureFactory("captureFactory", "test"));
        assertNotSame(captureFactory, rootContext.getCaptureFactory());
        assertEquals(1, rootContext.getChildren(CaptureFactory.class).size());

        TimeSeriesFactory seriesFactory = rootContext.getTimeSeriesFactory();
        rootContext.addChild(new DefaultTimeSeriesFactory("seriesFactory", "test"));
        assertNotSame(seriesFactory, rootContext.getTimeSeriesFactory());
        assertEquals(1, rootContext.getChildren(TimeSeriesFactory.class).size());

        ContextFactory contextFactory = rootContext.getContextFactory();
        rootContext.addChild(new DefaultContextFactory("contextFactory", "test"));
        assertNotSame(contextFactory, rootContext.getContextFactory());
        assertEquals(1, rootContext.getChildren(ContextFactory.class).size());
    }

    @Test
    public void testCannotChangesSchedulerIfSchedulerStarted() {
        rootContext.startScheduling();
        try {
            rootContext.setScheduler(new DefaultScheduler("test", "test"));
            fail("Should not be able to set scheduler");
        } catch (RuntimeException r) {
        }
    }

    @Test
    public void testCreateContextRecursive() {
        rootContext.createContext("child2.grandchild1");
        assertNotNull(rootContext.getContext("child2"));
        assertNotNull(rootContext.getContext("child2").getContext("grandchild1"));
        assertEquals("child2", rootContext.getContext("child2").getDescription());
        assertEquals("grandchild1", rootContext.getContext("child2").getContext("grandchild1").getDescription());

        rootContext.createContext("child2.grandchild2.greatgrandchild1", "wibble");
        assertEquals("child2", rootContext.getContext("child2").getDescription());
        assertEquals("grandchild2", rootContext.getContext("child2").getContext("grandchild2").getDescription());
        assertEquals("wibble", rootContext.getContext("child2").getContext("grandchild2").getContext("greatgrandchild1").getDescription());

        //creating a context with a "" path just returns the current context
        assertEquals(rootContext, rootContext.createContext("", ""));
    }

    @Test
    public void testStartCaptureStartsAllCapturesRecursivelyFromLocalContextDown() {
        childContext.createCounterSeries("counter", "counter");

        TimeSeriesContext grandchildcontext = childContext.createContext("grandchild");
        grandchildcontext.createCounterSeries("counter", "counter");

        childContext.startDataCapture();
        assertEquals(2, childContext.findAllCaptures().getNumberOfMatches());
        for ( Capture capture : childContext.findAllCaptures().getAllMatches()) {
            assertTrue(capture.getState() == CaptureState.STARTED);
        }

        //root context capture should not be started
        assertTrue(rootContext.getCaptures().size() > 0);
        for ( Capture capture : rootContext.getCaptures()) {
            assertFalse(capture.getState() == CaptureState.STARTED);
        }
    }

    @Test
    public void testStartCaptureImmediatelyProperty() {
        rootContext.setProperty(ContextProperties.START_CAPTURES_IMMEDIATELY_PROPERTY, "false");
        Counter c = childContext.createCounterSeries("counter", "counter");
        assertTrue(childContext.findCaptures(c).getFirstMatch().getState() == CaptureState.STOPPED);

        rootContext.setProperty(ContextProperties.START_CAPTURES_IMMEDIATELY_PROPERTY, "true");
        c = childContext.createCounterSeries("counter2", "counter2");
        assertTrue(childContext.findCaptures(c).getFirstMatch().getState() == CaptureState.STARTED);
    }

    @Test
    public void testCreateTimeSeries() {
        IdentifiableTimeSeries s = rootContext.createTimeSeries("timeSeries", "testDescription");
        assertEquals("timeSeries", s.getId());
        assertEquals("testDescription", s.getDescription());

        try {
            s = rootContext.createTimeSeries("", "");
            fail("Should not be able to create a timeseries with no id");
        } catch (Exception e) {}
    }

    @Test
    public void testCreateTimeSeriesRecursive() {
        IdentifiableTimeSeries s = rootContext.createTimeSeries("child1.timeSeries", "testDescription");
        assertFalse(rootContext.containsChildWithId("timeSeries"));
        assertTrue(rootContext.getContext("child1").containsChildWithId("timeSeries"));
        assertEquals("timeSeries", s.getId());
        assertEquals("testDescription", s.getDescription());
    }

}
