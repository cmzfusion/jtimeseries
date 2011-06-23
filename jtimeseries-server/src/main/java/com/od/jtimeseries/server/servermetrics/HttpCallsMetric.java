package com.od.jtimeseries.server.servermetrics;

import com.od.jtimeseries.component.managedmetric.AbstractManagedMetric;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.server.ServerStatsHttpHandlerFactory;
import com.od.jtimeseries.server.message.AppendToSeriesMessageListener;
import com.od.jtimeseries.source.ValueRecorder;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 23/06/11
 * Time: 18:13
 */
public class HttpCallsMetric extends AbstractManagedMetric {

    private static final String id = "HttpRequestTime";
    private String parentContextPath;

    public HttpCallsMetric(String parentContextPath) {
        this.parentContextPath = parentContextPath;
    }

    public String getSeriesId() {
        return id;
    }

    public String getParentContextPath() {
        return parentContextPath;
    }

    public void doInitializeMetric(TimeSeriesContext metricContext) {
        ValueRecorder v = metricContext.createValueRecorderSeries(id, "Length of time taken to process HTTP requests");
        ServerStatsHttpHandlerFactory.setHttpCallsValueRecorder(v);
    }
}
