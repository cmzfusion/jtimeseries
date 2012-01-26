package com.od.jtimeseries.server.servermetrics;

import com.od.jtimeseries.component.managedmetric.AbstractManagedMetric;
import com.od.jtimeseries.component.managedmetric.jmx.JmxMetric;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.identifiable.Identifiable;
import com.od.jtimeseries.source.Counter;
import com.od.jtimeseries.util.time.TimePeriod;

import static com.od.jtimeseries.capture.function.CaptureFunctions.COUNT_OVER;
import static com.od.jtimeseries.capture.function.CaptureFunctions.LATEST_COUNT;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 29/11/11
 * Time: 12:28
 */
public class JmxQueryCountMetric extends AbstractManagedMetric {
    private static final String id = "JmxQueryCount";
    private String parentContextPath;
    private TimePeriod captureTime;

    public JmxQueryCountMetric(String parentContextPath) {
        this(parentContextPath, DEFAULT_TIME_PERIOD_FOR_SERVER_METRICS);
    }

    public JmxQueryCountMetric(String parentContextPath, TimePeriod captureTime) {
        this.parentContextPath = parentContextPath;
        this.captureTime = captureTime;
    }

    protected String getSeriesPath() {
        return parentContextPath + Identifiable.NAMESPACE_SEPARATOR + id;
    }

    public void doInitializeMetric(TimeSeriesContext rootContext, String path) {
        Counter c = rootContext.createCounterSeries(path, "Number of queries operations made to open jmx conections, several values may be read during each query", COUNT_OVER(captureTime), LATEST_COUNT(captureTime));
        JmxMetric.setJmxQueryCounter(c);
    }
}

