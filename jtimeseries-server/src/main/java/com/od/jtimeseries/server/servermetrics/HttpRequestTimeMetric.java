package com.od.jtimeseries.server.servermetrics;

import com.od.jtimeseries.capture.function.CaptureFunctions;
import com.od.jtimeseries.component.managedmetric.AbstractManagedMetric;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.identifiable.Identifiable;
import com.od.jtimeseries.server.ServerHttpRequestMonitor;
import com.od.jtimeseries.source.ValueRecorder;
import com.od.jtimeseries.util.time.TimePeriod;

import static com.od.jtimeseries.capture.function.CaptureFunctions.MAX;
import static com.od.jtimeseries.capture.function.CaptureFunctions.MEDIAN;
import static com.od.jtimeseries.capture.function.CaptureFunctions.TOTAL_COUNT;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 23/06/11
 * Time: 18:13
 */
public class HttpRequestTimeMetric extends AbstractManagedMetric {

    private static final String id = "HttpRequestTime";
    private String parentContextPath;
    private TimePeriod captureTime;

    public HttpRequestTimeMetric(String parentContextPath) {
        this(parentContextPath, DEFAULT_TIME_PERIOD_FOR_SERVER_METRICS);
    }

    public HttpRequestTimeMetric(String parentContextPath, TimePeriod captureTime) {
        this.parentContextPath = parentContextPath;
        this.captureTime = captureTime;
    }

    protected String getSeriesPath() {
        return parentContextPath + Identifiable.NAMESPACE_SEPARATOR + id;
    }

    public void doInitializeMetric(TimeSeriesContext rootContext, String path) {
        ValueRecorder v = rootContext.createValueRecorderSeries(path, "Length of time taken to process HTTP requests in milliseconds", MEDIAN(captureTime), MAX(captureTime));
        ServerHttpRequestMonitor.setHttpRequestTimeValueRecorder(v);
    }
}
