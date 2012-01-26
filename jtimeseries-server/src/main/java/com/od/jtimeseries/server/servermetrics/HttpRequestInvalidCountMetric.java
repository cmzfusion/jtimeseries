package com.od.jtimeseries.server.servermetrics;

import com.od.jtimeseries.component.managedmetric.AbstractManagedMetric;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.identifiable.Identifiable;
import com.od.jtimeseries.server.ServerHttpRequestMonitor;
import com.od.jtimeseries.source.Counter;
import com.od.jtimeseries.util.time.TimePeriod;

import static com.od.jtimeseries.capture.function.CaptureFunctions.COUNT_OVER;
import static com.od.jtimeseries.capture.function.CaptureFunctions.LATEST_COUNT;

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

    protected String getSeriesPath() {
        return parentContextPath + Identifiable.NAMESPACE_SEPARATOR + id;
    }

    public void doInitializeMetric(TimeSeriesContext rootContext, String path) {
        Counter c = rootContext.createCounterSeries(path, "Count of invalid HTTP requests", COUNT_OVER(captureTime), LATEST_COUNT(captureTime));
        ServerHttpRequestMonitor.setHttpRequestInvalidCounter(c);
    }
}
