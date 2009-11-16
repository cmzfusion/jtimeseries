/**
 *  This file is part of ObjectDefinitions JTimeSeries
 */
package com.od.jtimeseries.capture.impl;

import com.od.jtimeseries.capture.TimedCapture;
import com.od.jtimeseries.capture.impl.AbstractCapture;
import com.od.jtimeseries.capture.function.CaptureFunction;
import com.od.jtimeseries.capture.CaptureScheduler;
import com.od.jtimeseries.capture.CaptureState;
import org.jmock.Mockery;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;
import org.junit.After;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 19-Feb-2009
 * Time: 17:20:27
 */
public abstract class AbstractTestCaptureScheduler extends Assert {

    protected CaptureScheduler scheduler;
    protected final int CAPTURE_PERIOD_MILLIS = 10;
    protected Mockery mockery;
    protected final AtomicLong triggerCount = new AtomicLong();
    protected TimedCapture triggerCountingCapture;

    @Before
    public void setUp() {
        scheduler = createCaptureScheduler();
        mockery = new Mockery();
        triggerCount.set(0);

        triggerCountingCapture = new DummyTimedCapture() {
            public void triggerCapture(long time) {
                triggerCount.incrementAndGet();
            }
        };
    }

    @After
    public void tearDown() {
        scheduler.stop();
        scheduler = null;
        mockery = null;
    }

    protected abstract CaptureScheduler createCaptureScheduler();


    @Test
    public void capturesWhichAreAlreadyStartedAreTriggeredWhenSchedulerIsStarted() {
        triggerCountingCapture.start();
        scheduler.addCapture(triggerCountingCapture);
        scheduler.start();
        sleepCapturePeriods(2);
        assertTrue(triggerCount.longValue() > 0);
    }

    @Test
    public void capturesWhichAreNotStartedAreNotTriggeredWhenSchedulerIsStarted() {
        scheduler.addCapture(triggerCountingCapture);
        scheduler.start();
        sleepCapturePeriods(2);
        assertTrue(triggerCount.longValue() == 0);
    }

    @Test
    public void capturesWhichAreAlreadyStartedAreTriggeredWhenAddedToRunningScheduler() {
        triggerCountingCapture.start();
        scheduler.start();
        scheduler.addCapture(triggerCountingCapture);
        sleepCapturePeriods(2);
        assertTrue(triggerCount.longValue() > 0);
    }

    @Test
    public void capturesWhichAreNotStartedAreNotTriggeredWhenAddedToRunningScheduler() {
        scheduler.start();
        scheduler.addCapture(triggerCountingCapture);
        sleepCapturePeriods(2);
        assertTrue(triggerCount.longValue() == 0);
    }

    @Test
    public void whenSchedulerIsRunningACaptureGetsTriggeredOnceItIsStarted() {
        scheduler.start();
        scheduler.addCapture(triggerCountingCapture);
        sleepCapturePeriods(2);
        assertTrue(triggerCount.longValue() == 0);
        triggerCountingCapture.start();
        sleepCapturePeriods(2);
        assertTrue(triggerCount.longValue() > 0);
    }

    @Test
    public void whenSchedulerIsRunningACaptureStopsGettingTriggeringOnceItIsStopped() {
        scheduler.start();
        scheduler.addCapture(triggerCountingCapture);
        triggerCountingCapture.start();
        sleepCapturePeriods(2);
        assertTrue(triggerCount.longValue() > 0);
        triggerCountingCapture.stop();
        triggerCount.set(0);
        sleepCapturePeriods(2);
        assertTrue(triggerCount.longValue() == 0);
    }

    @Test
    public void whenACaptureAssociatedWithARunningSchedulerIsRestartedItStartsGettingTriggeredAgain() {
        whenSchedulerIsRunningACaptureStopsGettingTriggeringOnceItIsStopped();
        triggerCountingCapture.start();
        sleepCapturePeriods(2);
        assertTrue(triggerCount.longValue() > 0);
    }

    protected void sleepCapturePeriods(float numberOfPeriods) {
        try {
            Thread.sleep((long)(CAPTURE_PERIOD_MILLIS * numberOfPeriods));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    protected class DummyTimedCapture extends AbstractCapture implements TimedCapture {

        public DummyTimedCapture() {
            super("DummyCapture", "DummyCapture", null, null);
        }

        public CaptureFunction getCaptureFunction() {
            return null;
        }

        public long getCapturePeriodInMilliseconds() {
            return CAPTURE_PERIOD_MILLIS;
        }

        public void triggerCapture(long timestamp) {
            if ( getState() != CaptureState.STARTED) {
                changeStateAndFireEvent(CaptureState.STARTED);
            }
        }

        public void start() {
            changeStateAndFireEvent(CaptureState.STARTING);
        }

        public void stop() {
            changeStateAndFireEvent(CaptureState.STOPPED);
        }

    }

}
