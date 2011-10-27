package com.od.jtimeseries.server.servermetrics;

import com.od.jtimeseries.capture.function.CaptureFunctions;
import com.od.jtimeseries.component.managedmetric.AbstractManagedMetric;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.server.serialization.RoundRobinSerializer;
import com.od.jtimeseries.source.Counter;
import com.od.jtimeseries.util.time.TimePeriod;

import static com.od.jtimeseries.capture.function.CaptureFunctions.COUNT_OVER;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 13/10/11
 * Time: 13:04
 */
public class FileBytesRead extends AbstractManagedMetric {

    private static final String id = "FileBytesRead";
    private String parentContextPath;
    private TimePeriod captureTime;

    public FileBytesRead(String parentContextPath) {
        this(parentContextPath, DEFAULT_TIME_PERIOD_FOR_SERVER_METRICS);
    }

    public FileBytesRead(String parentContextPath, TimePeriod captureTime) {
        this.parentContextPath = parentContextPath;
        this.captureTime = captureTime;
    }

    public String getSeriesId() {
        return id;
    }

    public String getParentContextPath() {
        return parentContextPath;
    }

    public void doInitializeMetric(TimeSeriesContext metricContext) {
        Counter c = metricContext.createCounterSeries(id, "Bytes read from timeseries files", COUNT_OVER(captureTime), CaptureFunctions.TOTAL_COUNT(captureTime));
        RoundRobinSerializer.setFileBytesRead(c);
    }
}