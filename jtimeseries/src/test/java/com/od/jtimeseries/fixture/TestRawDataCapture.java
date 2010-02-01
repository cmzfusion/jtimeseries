/**
 *  This file is part of ObjectDefinitions SwingBench
 *  Copyright (C) Nick Ebbutt 02 2009
 */
package com.od.jtimeseries.fixture;

import com.od.jtimeseries.timeseries.TimeSeries;
import com.od.jtimeseries.timeseries.impl.DefaultTimeSeries;
import com.od.jtimeseries.util.AbstractSimpleCaptureFixture;
import com.od.jtimeseries.util.numeric.DoubleNumeric;
import com.od.jtimeseries.util.numeric.LongNumeric;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 17-Feb-2009
 * Time: 13:34:24
 */
public class TestRawDataCapture extends AbstractSimpleCaptureFixture {

    protected void doExtraSetUp() {
        counter = rootContext.newCounter("TestCounter", "Test Counter Description");
        valueRecorder = rootContext.newValueRecorder("TestValueRecorder", "Test Value Recorder");
        eventTimer = rootContext.newEventTimer("TestEventTimer", "Test Event Timer");
        queueTimer = rootContext.newQueueTimer("TestQueueTimer", "Test Queue Timer");
    }

    @Test
    public void testRawValuesAreCapturedCorrectly() {
        assertNotNull(counter);
        assertNotNull(valueRecorder);
        assertNotNull(eventTimer);
        assertNotNull(queueTimer);

        rootContext.startDataCapture();
        counter.incrementCount();
        counter.decrementCount();

        valueRecorder.newValue(1.5d);
        valueRecorder.newValue(100l);
        valueRecorder.newValue(new DoubleNumeric(1.5d));
        valueRecorder.newValue(new LongNumeric(100l));

        eventTimer.startEventTimer();
        sleep(sleepPeriod);
        eventTimer.stopEventTimer();
        eventTimer.startEventTimer();
        sleep(sleepPeriod);
        eventTimer.stopEventTimer();

        Object o = new Object();
        queueTimer.objectAddedToQueue(o);
        sleep(sleepPeriod);
        queueTimer.objectRemovedFromQueue(o);

        queueTimer.objectAddedToQueue(o);
        sleep(sleepPeriod);
        queueTimer.objectRemovedFromQueue(o);

        TimeSeries timeSeries = rootContext.findTimeSeries(counter).getFirstMatch();
        DefaultTimeSeries d = new DefaultTimeSeries(timeSeries); //create a ListTimeSeries for random access to indexes
        assertEquals(2, d.size());
        assertEquals(1, d.get(0).longValue());
        assertEquals(0, d.get(1).longValue());

        timeSeries = rootContext.findTimeSeries(valueRecorder).getFirstMatch();
        d = new DefaultTimeSeries(timeSeries);
        assertEquals(4, d.size());
        assertEquals(1.5d, d.get(0).doubleValue());
        assertEquals(100l, d.get(1).longValue());
        assertEquals(1.5d, d.get(2).doubleValue());
        assertEquals(100l, d.get(3).longValue());

        timeSeries = rootContext.findTimeSeries(eventTimer).getFirstMatch();
        checkRecordedSleepPeriods(timeSeries);

        timeSeries = rootContext.findTimeSeries(queueTimer).getFirstMatch();
        checkRecordedSleepPeriods(timeSeries);
    }

}
