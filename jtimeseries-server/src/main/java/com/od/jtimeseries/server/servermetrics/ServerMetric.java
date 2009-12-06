package com.od.jtimeseries.server.servermetrics;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.util.time.TimePeriod;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 23-Nov-2009
 * Time: 21:39:49
 * To change this template use File | Settings | File Templates.
 */
public interface ServerMetric {

    public void setupSeries(TimeSeriesContext metricContext) ;

    public String getSeriesId();

    String getParentContextPath();
}
