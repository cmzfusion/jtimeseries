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
public abstract class ServerMetric implements Runnable {

    /**
     * @return a scheduling period, if this metric requires scheduling, or null
     */
    public abstract TimePeriod getSchedulingPeriod();

    public abstract String getSeriesId();

    public abstract String getMetricDescription();

    public abstract void setupSeries(TimeSeriesContext metricContext, IdentifiableTimeSeries series) ;
}
