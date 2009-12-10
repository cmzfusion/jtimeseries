package com.od.jtimeseries.fixture;

import org.junit.Test;
import com.od.jtimeseries.util.numeric.DoubleNumeric;
import com.od.jtimeseries.util.numeric.LongNumeric;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.util.AbstractSimpleCaptureFixture;
import com.od.jtimeseries.capture.function.CaptureFunctions;

import java.util.List;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 21-Feb-2009
 * Time: 23:47:10
 * To change this template use File | Settings | File Templates.
 */
public class TestTimedDataCapture extends AbstractSimpleCaptureFixture {

    protected void doExtraSetUp() {
        counter = rootContext.createCounter("TestCounter", "Test Counter Description", CaptureFunctions.CHANGE(capturePeriod));
        valueRecorder = rootContext.createValueRecorder("TestValueRecorder", "Test Value Recorder", CaptureFunctions.MEAN(capturePeriod));
        eventTimer = rootContext.createEventTimer("TestEventTimer", "Test Event Timer", CaptureFunctions.MAX(capturePeriod));
        queueTimer = rootContext.createQueueTimer("TestQueueTimer", "Test Queue Timer", CaptureFunctions.MIN(capturePeriod));
    }

    @Test
    public void testTimedCapturesWorkCorrectly() {
        assertNotNull(counter);
        assertNotNull(valueRecorder);
        assertNotNull(eventTimer);
        assertNotNull(queueTimer);

        WaitForEndOfCapturePeriodListener countDownListener = getCapturePeriodListener();
        rootContext.startScheduling().startDataCapture();
        countDownListener.waitForAll();
        generateSourceValuesForPeriod();

        countDownListener.waitForAll();
        generateSourceValuesForPeriod();

        countDownListener.waitForAll();
        rootContext.stopDataCapture().stopScheduling();

        List<IdentifiableTimeSeries> allSeries = rootContext.findAllTimeSeries().getAllMatches();
        assertEquals(4, allSeries.size());

        for (IdentifiableTimeSeries s : allSeries) {
            assertEquals(2, s.size());
        }

        TimeSeries s = rootContext.findTimeSeries(counter).getFirstMatch();
        Iterator<TimeSeriesItem> i = s.iterator();
        TimeSeriesItem i1 = i.next();
        TimeSeriesItem i2 = i.next();
        assertEquals(2, i1.longValue());
        assertEquals(2, i2.longValue());

        s = rootContext.findTimeSeries(valueRecorder).getFirstMatch();
        i = s.iterator();
        i1 = i.next();
        i2 = i.next();
        assertEquals(50.75, i1.doubleValue());
        assertEquals(50.75, i2.doubleValue());

        s = rootContext.findTimeSeries(eventTimer).getFirstMatch();
        checkRecordedSleepPeriods(s);

        s = rootContext.findTimeSeries(queueTimer).getFirstMatch();
        checkRecordedSleepPeriods(s);
    }

    private void generateSourceValuesForPeriod() {
        counter.incrementCount();
        counter.incrementCount();
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
    }


}
