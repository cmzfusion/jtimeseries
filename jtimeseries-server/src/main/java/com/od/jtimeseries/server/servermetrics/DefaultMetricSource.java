package com.od.jtimeseries.server.servermetrics;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 06-Dec-2009
 * Time: 11:57:12
 *
 * Simply stores a list of metrics, usually created and populated from xml via spring context.
 * Other implementations might connect to a database to read in the details of metrics to be
 * created, for example
 */
public class DefaultMetricSource implements ServerMetricSource {

    private List<ServerMetric> metrics;

    public DefaultMetricSource(List<ServerMetric> metrics) {
        this.metrics = metrics;
    }

    public List<ServerMetric> getServerMetrics() {
        return metrics;
    }
}
