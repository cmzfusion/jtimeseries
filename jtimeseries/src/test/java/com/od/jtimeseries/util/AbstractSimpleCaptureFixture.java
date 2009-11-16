package com.od.jtimeseries.util;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.After;
import com.od.jtimeseries.context.ContextFactory;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.context.impl.DefaultContextFactory;
import com.od.jtimeseries.util.time.TimePeriod;
import com.od.jtimeseries.util.time.Time;
import com.od.jtimeseries.timeseries.ListTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.source.Counter;
import com.od.jtimeseries.source.ValueRecorder;
import com.od.jtimeseries.source.EventTimer;
import com.od.jtimeseries.source.QueueTimer;

import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 21-Feb-2009
 * Time: 23:46:18
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractSimpleCaptureFixture extends Assert {

    protected TimeSeriesContext rootContext;
    protected Counter counter;
    protected ValueRecorder valueRecorder;
    protected EventTimer eventTimer;
    protected QueueTimer queueTimer;
    protected TimePeriod sleepPeriod = Time.millisecond((int)(25 * JTimeSeriesTestConstants.TOLERANCE_MULTIPLIER));
    protected TimePeriod capturePeriod = Time.millisecond((int)(250 * JTimeSeriesTestConstants.TOLERANCE_MULTIPLIER));

    @Before
    public void setUp() {
        ContextFactory contextFactory = new DefaultContextFactory();
        rootContext = contextFactory.createRootContext("Test Root Context");
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
        queueTimer = null;
    }

    protected void sleep(TimePeriod timePeriod) {
        try {
            Thread.sleep(timePeriod.getLengthInMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void checkRecordedSleepPeriods(TimeSeries timeSeries) {
        assertSame(2, timeSeries.size());
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
}
