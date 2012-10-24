package com.od.jtimeseries.util;

import com.od.jtimeseries.capture.Capture;
import com.od.jtimeseries.capture.CaptureState;
import com.od.jtimeseries.capture.TimedCapture;
import com.od.jtimeseries.context.ContextQueries;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.context.impl.SeriesContext;
import com.od.jtimeseries.source.*;
import com.od.jtimeseries.timeseries.TimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.util.numeric.Numeric;
import com.od.jtimeseries.util.time.Time;
import com.od.jtimeseries.util.time.TimePeriod;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 21-Feb-2009
 * Time: 23:46:18
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractSimpleCaptureFixture extends Assert {

    protected TimeSeriesContext rootContext;
    protected TimePeriod sleepPeriod = Time.milliseconds(100);
    protected TimePeriod capturePeriod = Time.milliseconds(500);

    //sources, if adding, change also getListOfSources()
    protected volatile Counter counter;
    protected volatile ValueRecorder valueRecorder;
    protected volatile EventTimer eventTimer;


    @Before
    public void setUp() {
        rootContext = new SeriesContext("Test Root Context", "Test Root Context");
        doExtraSetUp();
    }

    protected abstract void doExtraSetUp();

    @After
    public void tearDown() {
        if ( rootContext != null ) {
            rootContext.stopDataCapture().stopScheduling();
        }
        rootContext = null;
        counter = null;
        valueRecorder = null;
        eventTimer = null;
    }

    protected CaptureCompleteCountdown createCapturePeriodListener() {
        List<Capture> timedCaptures = getAllTimedCaptures();

        CaptureCompleteCountdown w = new CaptureCompleteCountdown(timedCaptures.size());
        for ( Capture c : timedCaptures) {
            c.addCaptureListener(w);
        }
        return w;
    }

    protected CaptureStartedCountdown createCaptureStartedListener() {
        List<Capture> timedCaptures = getAllTimedCaptures();

        CaptureStartedCountdown w = new CaptureStartedCountdown(timedCaptures.size());
        for ( Capture c : timedCaptures) {
            c.addCaptureListener(w);
        }
        return w;
    }

    private List<Capture> getAllTimedCaptures() {
        List<ValueSource> l = getListOfSources();

        List<Capture> timedCaptures = new LinkedList<Capture>();
        for ( ValueSource v : l) {
            List<Capture> captures = rootContext.findCaptures(v).getAllMatches();
            for ( Capture c : captures) {
                if ( c instanceof TimedCapture) {
                    timedCaptures.add(c);
                }
            }
        }
        return timedCaptures;
    }

    private List<ValueSource> getListOfSources() {
        List<ValueSource> l = new ArrayList<ValueSource>();
        addIfNotNull(l, counter);
        addIfNotNull(l, valueRecorder);
        addIfNotNull(l, eventTimer);
        return l;
    }

    private void addIfNotNull(List<ValueSource> l, ValueSource v) {
        if ( v != null) {
            l.add(v);
        }
    }

    protected void sleep(TimePeriod timePeriod) {
        try {
            Thread.sleep(timePeriod.getLengthInMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //here we are expecting the event timer to have recorded times within a given range of the actual sleep time
    //the range is defined by JTimeSeriesTestConstants.TIMER_INACCURACY_MS
    //it's possible, although unlikely, that this test will fail because the range is too narrow
    protected void checkRecordedSleepPeriods(TimeSeries timeSeries) {
        assertEquals(2, timeSeries.size());
        Iterator<TimeSeriesItem> i = timeSeries.iterator();
        TimeSeriesItem itemOne = i.next();
        TimeSeriesItem itemTwo = i.next();
        assertTrue(
            sleepPeriod.getLengthInMillis() - JTimeSeriesTestConstants.TIMER_INACCURACY_MS <= itemOne.longValue() &&
            sleepPeriod.getLengthInMillis() + JTimeSeriesTestConstants.TIMER_INACCURACY_MS >=  itemOne.longValue()
        );
        assertTrue(
            sleepPeriod.getLengthInMillis() - JTimeSeriesTestConstants.TIMER_INACCURACY_MS <= itemTwo.longValue() &&
            sleepPeriod.getLengthInMillis() + JTimeSeriesTestConstants.TIMER_INACCURACY_MS >=  itemTwo.longValue()
        );
    }

    protected void sleepUntilCapturePeriodOver(TimePeriod capturePeriod, long sleepStart) {
        long timeElapsedSinceStart = System.currentTimeMillis() - sleepStart;

        //allow 10ms extra to make sure we are clearly into the next period
        long timeToSleep = (capturePeriod.getLengthInMillis() - (timeElapsedSinceStart % capturePeriod.getLengthInMillis()));
        timeToSleep += 30;
        try {
            System.out.println("Sleeping for " + timeToSleep);
            Thread.sleep(timeToSleep + 10); //allow
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

     /**
     * A mechanism which allows us to wait until values have been captured for all of the
     * timed captures for the sources we are interested in
     */
    protected static class CaptureCompleteCountdown extends AbstractCaptureCountdown {

         public CaptureCompleteCountdown(int captureCount) {
             super(captureCount);
         }

        public void captureComplete(Capture source, Numeric value, TimeSeries series) {
            countDownLatch.countDown();
        }

     }

    /**
     * A mechanism which allows us to wait until values have been captured for all of the
     * timed captures for the sources we are interested in
     */
    protected static class CaptureStartedCountdown extends AbstractCaptureCountdown {

        public CaptureStartedCountdown(int captureCount) {
            super(captureCount);
        }

        public void captureStateChanged(Capture source, CaptureState oldState, CaptureState newState) {
            if ( newState == CaptureState.STARTED) {
                countDownLatch.countDown();
            }
        }

     }
}
