package com.od.jtimeseries.server.servermetrics;

import com.od.jtimeseries.component.managedmetric.AbstractManagedMetric;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.identifiable.Identifiable;
import com.od.jtimeseries.server.serialization.RoundRobinSerializer;
import com.od.jtimeseries.source.Counter;
import com.od.jtimeseries.util.time.TimePeriod;

import static com.od.jtimeseries.capture.function.CaptureFunctions.COUNT_OVER;
import static com.od.jtimeseries.capture.function.CaptureFunctions.TOTAL_COUNT;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 05/10/11
 * Time: 09:08
 */
public class FileOperationErrorCount extends AbstractManagedMetric {
    private static final String id = "FileOperationErrors";
    private String parentContextPath;
    private TimePeriod captureTime;

    public FileOperationErrorCount(String parentContextPath) {
        this(parentContextPath, DEFAULT_TIME_PERIOD_FOR_SERVER_METRICS);
    }

    public FileOperationErrorCount(String parentContextPath, TimePeriod captureTime) {
        this.parentContextPath = parentContextPath;
        this.captureTime = captureTime;
    }

    protected String getSeriesPath() {
        return parentContextPath + Identifiable.NAMESPACE_SEPARATOR + id;
    }

    public void doInitializeMetric(TimeSeriesContext rootContext, String path) {
        Counter c = rootContext.createCounterSeries(path, "Count of errors encountered during series file reads or writes", COUNT_OVER(captureTime), TOTAL_COUNT(captureTime));
        RoundRobinSerializer.setFileErrorCounter(c);
    }
}
