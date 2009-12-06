package com.od.jtimeseries.server.servermetrics;

import com.od.jtimeseries.util.time.TimePeriod;
import com.od.jtimeseries.util.time.Time;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 06-Dec-2009
 * Time: 12:04:07
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractServerMetric implements ServerMetric {

    protected static final TimePeriod DEFAULT_TIME_PERIOD_FOR_SERVER_METRICS = Time.minutes(5);

    public String toString() {
        return getParentContextPath() + "." + getSeriesId();
    }
}
