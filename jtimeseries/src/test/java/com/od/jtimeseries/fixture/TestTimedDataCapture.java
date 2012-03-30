package com.od.jtimeseries.fixture;

import com.od.jtimeseries.JTimeSeries;
import com.od.jtimeseries.capture.Capture;
import com.od.jtimeseries.capture.CaptureState;
import com.od.jtimeseries.capture.function.CaptureFunctions;
import com.od.jtimeseries.context.ContextProperties;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.source.Counter;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.util.AbstractSimpleCaptureFixture;
import com.od.jtimeseries.util.numeric.DoubleNumeric;
import com.od.jtimeseries.util.numeric.LongNumeric;
import com.od.jtimeseries.util.time.Time;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

import static com.od.jtimeseries.capture.function.CaptureFunctions.*;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 21-Feb-2009
 * Time: 23:47:10
 * To change this template use File | Settings | File Templates.
 */
public class TestTimedDataCapture extends AbstractSimpleCaptureFixture {

    public final int CAPTURE_DIVISOR_FOR_MEAN_CHANGE = 5;

    protected void doExtraSetUp() {
        rootContext.stopScheduling(); //now started by default
        counter = rootContext.createCounterSeries("TestCounter", "Test Counter Description",
                CaptureFunctions.COUNT_OVER(capturePeriod),
                CaptureFunctions.CHANGE(capturePeriod),
                CaptureFunctions.MEAN_CHANGE(Time.milliseconds((int)capturePeriod.getLengthInMillis() / CAPTURE_DIVISOR_FOR_MEAN_CHANGE), capturePeriod),
                CaptureFunctions.MEAN_COUNT_OVER(Time.milliseconds((int) capturePeriod.getLengthInMillis() / CAPTURE_DIVISOR_FOR_MEAN_CHANGE), capturePeriod),
                CaptureFunctions.RAW_VALUES()
        );

        valueRecorder = rootContext.createValueRecorderSeries("TestValueRecorder", "Test Value Recorder", MEAN(capturePeriod));
        eventTimer = rootContext.createEventTimerSeries("TestEventTimer", "Test Event Timer", MAX(capturePeriod));
    }

    @Test
    public void testTimedCapturesWorkCorrectly() {
        assertNotNull(counter);
        assertNotNull(valueRecorder);
        assertNotNull(eventTimer);

        CaptureStartedCountdown captureStartedListener = createCaptureStartedListener();
        rootContext.startScheduling();
        captureStartedListener.waitForAll();

        CaptureCompleteCountdown countDownListener = createCapturePeriodListener();
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
        assertEquals(7, allSeries.size());
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

        //this series is the change in the counter value (the count during the period)
        s = rootContext.findTimeSeries("TestCounter \\(Count " + capturePeriod).getFirstMatch();
        i = s.iterator();
        i1 = i.next();
        i2 = i.next();
        assertEquals(2, i1.longValue());
        assertEquals(2, i2.longValue());

        //this series is the mean change over capturePeriod / 5
        s = rootContext.findTimeSeries("TestCounter \\(Change Per").getFirstMatch();
        i = s.iterator();
        i1 = i.next();
        i2 = i.next();
        assertEquals(2d / CAPTURE_DIVISOR_FOR_MEAN_CHANGE, i1.doubleValue(), 0.05);
        assertEquals(2d / CAPTURE_DIVISOR_FOR_MEAN_CHANGE, i2.doubleValue(), 0.05);

        //this series is the mean change in the counter value (the count during the period)
        s = rootContext.findTimeSeries("TestCounter \\(Count Per").getFirstMatch();
        i = s.iterator();
        i1 = i.next();
        i2 = i.next();
        assertEquals(2d / CAPTURE_DIVISOR_FOR_MEAN_CHANGE, i1.doubleValue(), 0.05);
        assertEquals(2d / CAPTURE_DIVISOR_FOR_MEAN_CHANGE, i2.doubleValue(), 0.05);

        s = rootContext.findTimeSeries(valueRecorder).getFirstMatch();
        i = s.iterator();
        i1 = i.next();
        i2 = i.next();
        assertEquals(50.75, i1.doubleValue());
        assertEquals(50.75, i2.doubleValue());

        s = rootContext.findTimeSeries(eventTimer).getFirstMatch();
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
    }

    @Test
    public void testTimedCaptureLifecycleWhenStartImmediately() {
        TimeSeriesContext c = JTimeSeries.createRootContext();
        c.stopScheduling();//now is started by default
        Counter counter = c.createCounterSeries("test.counter", "Test Counter Description", CaptureFunctions.COUNT_OVER(Time.milliseconds(500)));
        Capture capture = c.findCaptures(counter).getFirstMatch();
        doTestFromAStartingState(c, capture);
    }

    @Test
    public void testTimedCaptureLifecycleWhenNotStartImmediately() {
        TimeSeriesContext c = JTimeSeries.createRootContext();
        c.stopScheduling();//now is started by default
        c.setProperty(ContextProperties.START_CAPTURES_IMMEDIATELY_PROPERTY, "false");
        Counter counter = c.createCounterSeries("test.counter", "Test Counter Description", CaptureFunctions.COUNT_OVER(Time.milliseconds(500)));
        Capture capture = c.findCaptures(counter).getFirstMatch();
        assertEquals(CaptureState.STOPPED, capture.getState());

        c.startDataCapture();
        doTestFromAStartingState(c, capture);
    }

    private void doTestFromAStartingState(TimeSeriesContext c, Capture capture) {
        assertEquals(CaptureState.STARTING, capture.getState());

        CaptureStartedCountdown captureStartedListener = new CaptureStartedCountdown(1);
        capture.addCaptureListener(captureStartedListener);

        c.startScheduling();
        captureStartedListener.waitForAll();
        assertEquals(CaptureState.STARTED, capture.getState());

        capture.stop();
        assertEquals(CaptureState.STOPPED, capture.getState());
    }

}
