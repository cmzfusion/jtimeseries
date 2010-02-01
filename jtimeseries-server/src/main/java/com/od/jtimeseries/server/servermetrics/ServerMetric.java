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

    /**
     * Called by the server to ask the metric to initialize itself
     * Typically this will involve creating a new timeseries within the metricContext
     *
     * @param metricContext, the parent context mapping, determined by getParentContextPath()
     */
    public void initializeMetric(TimeSeriesContext metricContext) ;

    /**
     * @return the id of the TimeSeries created, to be used when the server logs the setup of the metric
     */
    public String getSeriesId();

    String getParentContextPath();
}
