package com.od.jtimeseries.server.servermetrics;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 06-Dec-2009
 * Time: 11:36:01
 * To change this template use File | Settings | File Templates.
 */
public interface ServerMetricSource {

    List<ServerMetric> getServerMetrics();

}
