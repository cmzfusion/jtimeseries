package com.od.jtimeseries.capture.function;

import junit.framework.TestCase;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.context.impl.DefaultTimeSeriesContext;
import com.od.jtimeseries.util.time.TimePeriod;
import com.od.jtimeseries.util.time.Time;
import com.od.jtimeseries.source.*;
import org.junit.Before;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 31-Jan-2010
 * Time: 11:56:19
 * To change this template use File | Settings | File Templates.
 */
public class TestCaptureFunctions extends TestCase {

    protected TimeSeriesContext rootContext;

    @Before
    public void setUp() {
        rootContext = new DefaultTimeSeriesContext("Test Root Context", "Test Root Context");
    }

    public void testMilliseconds() {
        ValueRecorder v = rootContext.createValueRecorder("Value", "Value Description", CaptureFunctions.CHANGE(Time.milliseconds(10)));
        assertEquals("Value (Change 10ms)", rootContext.findTimeSeries(v).getFirstMatch().getId());
    }

    public void testSeconds() {
        ValueRecorder v = rootContext.createValueRecorder("Value", "Value Description", CaptureFunctions.MAX(Time.seconds(30)));
        assertEquals("Value (Max 30s)", rootContext.findTimeSeries(v).getFirstMatch().getId());
    }

    public void testMinutes() {
        ValueRecorder v = rootContext.createValueRecorder("Value", "Value Description", CaptureFunctions.MIN(Time.minutes(20)));
        assertEquals("Value (Min 20min)", rootContext.findTimeSeries(v).getFirstMatch().getId());
    }

    public void testHours() {
        ValueRecorder v = rootContext.createValueRecorder("Value", "Value Description", CaptureFunctions.MEAN(Time.hours(2)));
        assertEquals("Value (Mean 2hr)", rootContext.findTimeSeries(v).getFirstMatch().getId());
    }

    public void testDays() {
        ValueRecorder v = rootContext.createValueRecorder("Value", "Value Description", CaptureFunctions.SUM(Time.days(3)));
        assertEquals("Value (Sum 3day)", rootContext.findTimeSeries(v).getFirstMatch().getId());
    }

    public void testNaming() {
        rootContext.createCounter("Login Attempts", "Count of Login Attempts",
                CaptureFunctions.COUNT_OVER(Time.days(3)),
                CaptureFunctions.MAX(Time.milliseconds(50))
        );

        assertNotNull(rootContext.get("Login Attempts (Count Over 3day)"));
        assertNotNull(rootContext.get("Login Attempts (Max 50ms)"));
    }







//    protected void doExtraSetUp() {
//        counter = rootContext.createCounter("TestCounter", "Test Counter Description", CaptureFunctions.CHANGE(capturePeriod));
//        valueRecorder = rootContext.createValueRecorder("TestValueRecorder", "Test Value Recorder Description", CaptureFunctions.MEAN(capturePeriod));
//        eventTimer = rootContext.createEventTimer("TestEventTimer", "Test Event Timer Description", CaptureFunctions.MAX(capturePeriod));
//        queueTimer = rootContext.createQueueTimer("TestQueueTimer", "Test Queue Timer Description", CaptureFunctions.MIN(capturePeriod));
//    }


}
