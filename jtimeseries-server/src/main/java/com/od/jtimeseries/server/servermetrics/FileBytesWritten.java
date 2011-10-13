package com.od.jtimeseries.server.servermetrics;

import com.od.jtimeseries.capture.function.CaptureFunctions;
import com.od.jtimeseries.component.managedmetric.AbstractManagedMetric;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.server.serialization.RoundRobinSerializer;
import com.od.jtimeseries.source.ValueRecorder;
import com.od.jtimeseries.util.time.TimePeriod;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 13/10/11
 * Time: 13:04
 */
public class FileBytesWritten extends AbstractManagedMetric {

    private static final String id = "FileBytesWritten";
    private String parentContextPath;
    private TimePeriod captureTime;

    public FileBytesWritten(String parentContextPath) {
        this(parentContextPath, DEFAULT_TIME_PERIOD_FOR_SERVER_METRICS);
    }

    public FileBytesWritten(String parentContextPath, TimePeriod captureTime) {
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
        ValueRecorder v = metricContext.createValueRecorderSeries(id, "Bytes written to timeseries files", CaptureFunctions.SUM(captureTime));
        RoundRobinSerializer.setFileBytesWritten(v);
    }
}
