package com.od.jtimeseries.server.servermetrics;

import com.od.jtimeseries.component.managedmetric.AbstractManagedMetric;
import com.od.jtimeseries.component.managedmetric.jmx.DefaultJmxConnectionPool;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.identifiable.Identifiable;
import com.od.jtimeseries.source.Counter;
import com.od.jtimeseries.util.time.TimePeriod;

import static com.od.jtimeseries.capture.function.CaptureFunctions.LATEST;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 25/11/11
 * Time: 19:23
 */
public class JmxConnectionCreationCountMetric extends AbstractManagedMetric {
    private static final String id = "JmxConnectionCreation";
    private String parentContextPath;
    private TimePeriod captureTime;

    public JmxConnectionCreationCountMetric(String parentContextPath) {
        this(parentContextPath, DEFAULT_TIME_PERIOD_FOR_SERVER_METRICS);
    }

    public JmxConnectionCreationCountMetric(String parentContextPath, TimePeriod captureTime) {
        this.parentContextPath = parentContextPath;
        this.captureTime = captureTime;
    }

    protected String getSeriesPath() {
        return parentContextPath + Identifiable.NAMESPACE_SEPARATOR + id;
    }

    public void doInitializeMetric(TimeSeriesContext rootContext, String path) {
        Counter c = rootContext.createCounterSeries(
            path,
            "Count of connections created by jmx connection pool",
            LATEST(captureTime)
        );
        DefaultJmxConnectionPool.setJmxConnectionCreationCounter(c);
    }
}
