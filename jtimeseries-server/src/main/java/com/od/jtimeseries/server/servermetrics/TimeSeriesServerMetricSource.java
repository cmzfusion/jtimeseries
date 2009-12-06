package com.od.jtimeseries.server.servermetrics;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 06-Dec-2009
 * Time: 11:57:12
 * To change this template use File | Settings | File Templates.
 */
public class TimeSeriesServerMetricSource implements ServerMetricSource {

    private List<ServerMetric> metrics;

    public TimeSeriesServerMetricSource(List<ServerMetric> metrics) {
        this.metrics = metrics;
    }

    public List<ServerMetric> getServerMetrics() {
        return metrics;
    }
}
