package com.od.jtimeseries.context;

import junit.framework.TestCase;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.context.impl.DefaultTimeSeriesContext;
import com.od.jtimeseries.util.time.Time;
import com.od.jtimeseries.source.*;
import com.od.jtimeseries.capture.function.CaptureFunctions;
import org.junit.Before;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 31-Jan-2010
 * Time: 11:56:19
 * To change this template use File | Settings | File Templates.
 */
public class TestTimeSeriesNaming extends TestCase {

    protected TimeSeriesContext rootContext;

    @Before
    public void setUp() {
        rootContext = new DefaultTimeSeriesContext("Test Root Context", "Test Root Context");
    }

    public void testMilliseconds() {
        ValueRecorder v = rootContext.newValueRecorder("Value", "Value Description", CaptureFunctions.CHANGE(Time.milliseconds(10)));
        assertEquals("Value (Change 10ms)", rootContext.findTimeSeries(v).getFirstMatch().getId());
    }

    public void testSeconds() {
        ValueRecorder v = rootContext.newValueRecorder("Value", "Value Description", CaptureFunctions.MAX(Time.seconds(30)));
        assertEquals("Value (Max 30s)", rootContext.findTimeSeries(v).getFirstMatch().getId());
    }

    public void testMinutes() {
        ValueRecorder v = rootContext.newValueRecorder("Value", "Value Description", CaptureFunctions.MIN(Time.minutes(20)));
        assertEquals("Value (Min 20min)", rootContext.findTimeSeries(v).getFirstMatch().getId());
    }

    public void testHours() {
        ValueRecorder v = rootContext.newValueRecorder("Value", "Value Description", CaptureFunctions.MEAN(Time.hours(2)));
        assertEquals("Value (Mean 2hr)", rootContext.findTimeSeries(v).getFirstMatch().getId());
    }

    public void testDays() {
        ValueRecorder v = rootContext.newValueRecorder("Value", "Value Description", CaptureFunctions.SUM(Time.days(3)));
        assertEquals("Value (Sum 3day)", rootContext.findTimeSeries(v).getFirstMatch().getId());
    }

    public void testNaming() {
        rootContext.newCounter("Login Attempts", "Count of Login Attempts",
                CaptureFunctions.CHANGE(Time.days(3)),
                CaptureFunctions.COUNT(Time.days(3)),
                CaptureFunctions.MAX(Time.milliseconds(50)),
                CaptureFunctions.MEAN_CHANGE(Time.minutes(1), Time.minutes(30)),
                CaptureFunctions.MEAN_COUNT(Time.seconds(30), Time.hours(1)),
                CaptureFunctions.SUM(Time.hours(3)),
                CaptureFunctions.MIN(Time.minutes(120)),
                CaptureFunctions.LAST(Time.seconds(10))
        );

        assertNotNull(rootContext.get("Login Attempts (Change 3day)"));
        assertNotNull(rootContext.get("Login Attempts (Count 3day)"));
        assertNotNull(rootContext.get("Login Attempts (Max 50ms)"));
        assertNotNull(rootContext.get("Login Attempts (Mean Change Per 1min Over 30min)"));
        assertNotNull(rootContext.get("Login Attempts (Mean Count Per 30s Over 1hr)"));
        assertNotNull(rootContext.get("Login Attempts (Sum 3hr)"));
        assertNotNull(rootContext.get("Login Attempts (Min 120min)"));
        assertNotNull(rootContext.get("Login Attempts (Last 10s)"));
    }

}
