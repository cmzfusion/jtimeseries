package com.od.jtimeseries.server.servermetrics;

import com.od.jtimeseries.capture.function.CaptureFunctions;
import com.od.jtimeseries.component.managedmetric.AbstractManagedMetric;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.server.serialization.RoundRobinSerializer;
import com.od.jtimeseries.source.Counter;
import com.od.jtimeseries.util.time.TimePeriod;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 05/10/11
 * Time: 09:08
 */
public class FileHeaderReadsMetric extends AbstractManagedMetric {
    private static final String id = "FileHeaderReads";
    private String parentContextPath;
    private TimePeriod captureTime;

    public FileHeaderReadsMetric(String parentContextPath) {
        this(parentContextPath, DEFAULT_TIME_PERIOD_FOR_SERVER_METRICS);
    }

    public FileHeaderReadsMetric(String parentContextPath, TimePeriod captureTime) {
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
        Counter c = metricContext.createCounterSeries(id, "Count of file header read operations", CaptureFunctions.COUNT(captureTime));
        RoundRobinSerializer.setFileHeaderReadCounter(c);
    }
}