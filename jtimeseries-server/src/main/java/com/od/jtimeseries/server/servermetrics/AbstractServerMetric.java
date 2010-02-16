package com.od.jtimeseries.server.servermetrics;

import com.od.jtimeseries.util.time.TimePeriod;
import com.od.jtimeseries.util.time.Time;
import com.od.jtimeseries.context.TimeSeriesContext;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 06-Dec-2009
 * Time: 12:04:07
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractServerMetric implements ServerMetric {

    protected static final TimePeriod DEFAULT_TIME_PERIOD_FOR_SERVER_METRICS = Time.minutes(5);

    public final void initializeMetrics(TimeSeriesContext rootContext)  {
        TimeSeriesContext c = rootContext.createContextForPath(getParentContextPath());
        doInitializeMetric(c);
    }

    protected abstract void doInitializeMetric(TimeSeriesContext targetContext);

    /**
     * @return the path to the context in which the metric will be created
     */
    protected abstract String getParentContextPath();

    /**
     * @return the id of the metric which will be created
     */
    protected abstract String getSeriesId();

    public String toString() {
        return getParentContextPath() + "." + getSeriesId();
    }
}
