package com.od.jtimeseries.server.servermetrics;

import com.od.jtimeseries.capture.function.CaptureFunctions;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.server.serialization.RoundRobinTimeSeries;
import com.od.jtimeseries.source.Counter;
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
    private static final String id = "SeriesGarbageCollected";

    public TimePeriod getSchedulingPeriod() {
        return null;
    }

    public String getSeriesId() {
        return id;
    }

    public void setupSeries(TimeSeriesContext metricContext) {
        Counter counter = metricContext.createCounter(
                id,
                "A count of the series deallocated for memory efficiency every " + countPeriod +
                ", we would expect a heavily loaded server to regularly deallocate series data once it is no longer possible to " +
                "maintain all series data in RAM",
                CaptureFunctions.COUNT(countPeriod));
        RoundRobinTimeSeries.setGarbageCollectionCounter(counter);
    }
}
