package com.od.jtimeseries.server.servermetrics;

import com.od.jtimeseries.capture.function.CaptureFunctions;
import com.od.jtimeseries.component.managedmetric.AbstractManagedMetric;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.server.ServerHttpRequestMonitor;
import com.od.jtimeseries.source.Counter;
import com.od.jtimeseries.util.time.TimePeriod;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 23/06/11
 * Time: 18:13
 */
public class HttpRequestInvalidCountMetric extends AbstractManagedMetric {

    private static final String id = "HttpRequestInvalidCount";
    private String parentContextPath;
    private TimePeriod captureTime;

    public HttpRequestInvalidCountMetric(String parentContextPath) {
        this(parentContextPath, DEFAULT_TIME_PERIOD_FOR_SERVER_METRICS);
    }

    public HttpRequestInvalidCountMetric(String parentContextPath, TimePeriod captureTime) {
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
        Counter c = metricContext.createCounterSeries(id, "Count of invalid HTTP requests", CaptureFunctions.COUNT(captureTime));
        ServerHttpRequestMonitor.setHttpRequestInvalidCounter(c);
    }
}