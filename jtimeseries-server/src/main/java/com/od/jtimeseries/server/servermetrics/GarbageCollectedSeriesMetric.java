package com.od.jtimeseries.server.servermetrics;

import com.od.jtimeseries.capture.function.CaptureFunctions;
import com.od.jtimeseries.capture.impl.DefaultTimedCapture;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.server.serialization.RoundRobinTimeSeries;
import com.od.jtimeseries.source.Counter;
import com.od.jtimeseries.source.impl.DefaultCounter;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.util.time.Time;
import com.od.jtimeseries.util.time.TimePeriod;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 23-Nov-2009
 * Time: 21:43:01
 * To change this template use File | Settings | File Templates.
 */
public class GarbageCollectedSeriesMetric extends ServerMetric {

    private static final TimePeriod countPeriod = Time.minutes(5);

    public static final String GARBAGE_COLLECTED_SERIES_COUNT_METRIC_ID = "SeriesGarbageCollected";
    private static final String GARBAGE_COLLECTED_SERIES_COUNT_METRIC_DESC =
            "A count of the series deallocated for memory efficiency every " + countPeriod +
            ", we would expect a heavily loaded server to regularly deallocate series data once it is no longer possible to " +
            "maintain all series data in RAM";

    public TimePeriod getSchedulingPeriod() {
        return null;
    }

    public String getSeriesId() {
        return GARBAGE_COLLECTED_SERIES_COUNT_METRIC_ID;
    }

    public String getMetricDescription() {
        return GARBAGE_COLLECTED_SERIES_COUNT_METRIC_DESC;
    }

    public void setupSeries(TimeSeriesContext metricContext, IdentifiableTimeSeries timeSeries) {
        //n.b. usually we could use metricContext.createCounter() to create the counter, capture and series in one method call,
        //but here the series is already created (may have been reloaded) so create and add the Counter and timed capture the hard way.
        Counter counter = new DefaultCounter("Source_" + getSeriesId(), "Counter for " + getSeriesId());
        DefaultTimedCapture t = new DefaultTimedCapture("Capture " + getSeriesId(), counter, timeSeries, CaptureFunctions.COUNT(countPeriod));
        metricContext.addChild(counter, t);

        RoundRobinTimeSeries.setGarbageCollectionCounter(counter);
    }

    public void run() {
    }
}
