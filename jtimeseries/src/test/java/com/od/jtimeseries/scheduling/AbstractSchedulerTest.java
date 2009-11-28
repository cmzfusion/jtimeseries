/**
 *  This file is part of ObjectDefinitions JTimeSeries
 */
package com.od.jtimeseries.scheduling;

import com.od.jtimeseries.capture.TimedCapture;
import com.od.jtimeseries.capture.impl.AbstractCapture;
import com.od.jtimeseries.capture.function.CaptureFunction;
import com.od.jtimeseries.scheduling.Scheduler;
import com.od.jtimeseries.capture.CaptureState;
import com.od.jtimeseries.util.time.TimePeriod;
import com.od.jtimeseries.util.time.Time;
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
public abstract class AbstractSchedulerTest extends Assert {

    protected Scheduler scheduler;
    protected final TimePeriod CAPTURE_PERIOD_MILLIS = Time.milliseconds(10);
    protected Mockery mockery;
    protected final AtomicLong triggerCount = new AtomicLong();
    protected TimedCapture triggerCountingCapture;

    @Before
    public void setUp() {
        scheduler = createCaptureScheduler();
        mockery = new Mockery();
        triggerCount.set(0);

        triggerCountingCapture = new DummyTimedCapture() {
            public void trigger(long time) {
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

    protected abstract Scheduler createCaptureScheduler();


    @Test
    public void capturesAreTriggeredWhenSchedulerIsStarted() {
        triggerCountingCapture.start();
        scheduler.addTriggerable(triggerCountingCapture);
        scheduler.start();
        sleepCapturePeriods(2);
        assertTrue(triggerCount.longValue() > 0);
    }

    @Test
    public void capturesAreTriggeredWhenAddedToRunningScheduler() {
        triggerCountingCapture.start();
        scheduler.start();
        scheduler.addTriggerable(triggerCountingCapture);
        sleepCapturePeriods(2);
        assertTrue(triggerCount.longValue() > 0);
    }

    protected void sleepCapturePeriods(float numberOfPeriods) {
        try {
            Thread.sleep((long)(CAPTURE_PERIOD_MILLIS.getLengthInMillis() * numberOfPeriods));
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

        public TimePeriod getTimePeriod() {
            return CAPTURE_PERIOD_MILLIS;
        }

        public void trigger(long timestamp) {
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
