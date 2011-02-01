package com.od.jtimeseries.fixture;

import com.od.jtimeseries.timeseries.TimeSeriesItem;
import org.junit.Test;
import com.od.jtimeseries.util.numeric.DoubleNumeric;
import com.od.jtimeseries.util.numeric.LongNumeric;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeries;
import com.od.jtimeseries.util.AbstractSimpleCaptureFixture;
import com.od.jtimeseries.util.time.Time;
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
        counter = rootContext.createCounterSeries("TestCounter", "Test Counter Description",
                CaptureFunctions.COUNT(capturePeriod),
                CaptureFunctions.CHANGE(capturePeriod),
                CaptureFunctions.MEAN_CHANGE(Time.milliseconds((int)capturePeriod.getLengthInMillis() / 5), capturePeriod),
                CaptureFunctions.MEAN_COUNT(Time.milliseconds((int)capturePeriod.getLengthInMillis() / 5), capturePeriod),
                CaptureFunctions.RAW_VALUES()
        );

        valueRecorder = rootContext.createValueRecorderSeries("TestValueRecorder", "Test Value Recorder", CaptureFunctions.MEAN(capturePeriod));
        eventTimer = rootContext.createEventTimerSeries("TestEventTimer", "Test Event Timer", CaptureFunctions.MAX(capturePeriod));
        queueTimer = rootContext.createQueueTimerSeries("TestQueueTimer", "Test Queue Timer", CaptureFunctions.MIN(capturePeriod));
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

        //the 'raw' count values - these are not 'timed capture' but worth testing them here
        TimeSeries rawValueSeries = rootContext.getTimeSeries("TestCounter");
        assertEquals(8, rawValueSeries.size());
        assertEquals(1, rawValueSeries.getEarliestItem().longValue());
        assertEquals(4, rawValueSeries.getLatestItem().longValue());

        List<IdentifiableTimeSeries> allSeries = rootContext.findAllTimeSeries().getAllMatches();
        assertEquals(8, allSeries.size());
        for (IdentifiableTimeSeries s : allSeries) {
            if ( s != rawValueSeries) {
                assertEquals(2, s.size());
            }
        }

        //this series is the change in the count over the period, which is 2 because the count was decremented
        TimeSeries s = rootContext.findTimeSeries("TestCounter \\(Change " + capturePeriod).getFirstMatch();
        Iterator<TimeSeriesItem> i = s.iterator();
        TimeSeriesItem i1 = i.next();
        TimeSeriesItem i2 = i.next();
        assertEquals(2, i1.longValue());
        assertEquals(2, i2.longValue());

        //this series is the absolute count of values collected over the period, which is 4
        s = rootContext.findTimeSeries("TestCounter \\(Count " + capturePeriod).getFirstMatch();
        i = s.iterator();
        i1 = i.next();
        i2 = i.next();
        assertEquals(4, i1.longValue());
        assertEquals(4, i2.longValue());

        //this series is the mean change over capturePeriod / 5
        s = rootContext.findTimeSeries("TestCounter \\(Change Per").getFirstMatch();
        i = s.iterator();
        i1 = i.next();
        i2 = i.next();
        assertEquals(0.4, i1.doubleValue(), 0.0001);
        assertEquals(0.4, i2.doubleValue(), 0.0001);

        //this series is the mean count over capturePeriod / 5
        s = rootContext.findTimeSeries("TestCounter \\(Count Per").getFirstMatch();
        i = s.iterator();
        i1 = i.next();
        i2 = i.next();
        assertEquals(0.8, i1.doubleValue(), 0.0001);
        assertEquals(0.8, i2.doubleValue(), 0.0001);

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
        valueRecorder.newValue(DoubleNumeric.valueOf(1.5d));
        valueRecorder.newValue(LongNumeric.valueOf(100l));

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
