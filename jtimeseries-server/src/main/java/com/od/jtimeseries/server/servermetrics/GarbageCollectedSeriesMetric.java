package com.od.jtimeseries.server.servermetrics;

import com.od.jtimeseries.capture.function.CaptureFunctions;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.server.serialization.RoundRobinTimeSeries;
import com.od.jtimeseries.source.Counter;
import com.od.jtimeseries.util.time.TimePeriod;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 23-Nov-2009
 * Time: 21:43:01
 * To change this template use File | Settings | File Templates.
 */
public class GarbageCollectedSeriesMetric extends AbstractServerMetric {

    private TimePeriod countPeriod;
    private static final String id = "SeriesGarbageCollected";
    private String parentContextPath;

    public GarbageCollectedSeriesMetric(String parentContextPath) {
        this(parentContextPath, DEFAULT_TIME_PERIOD_FOR_SERVER_METRICS);
    }

    public GarbageCollectedSeriesMetric(String parentContextPath, TimePeriod countPeriod) {
        this.parentContextPath = parentContextPath;
        this.countPeriod = countPeriod;
    }

    public String getParentContextPath() {
        return parentContextPath;
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
                CaptureFunctions.DELTA(countPeriod));
        RoundRobinTimeSeries.setGarbageCollectionCounter(counter);
    }

}
