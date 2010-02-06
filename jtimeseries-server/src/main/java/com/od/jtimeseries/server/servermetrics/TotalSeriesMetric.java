package com.od.jtimeseries.server.servermetrics;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.source.ValueSupplier;
import com.od.jtimeseries.util.time.TimePeriod;
import com.od.jtimeseries.util.numeric.Numeric;
import com.od.jtimeseries.util.numeric.LongNumeric;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 25-Nov-2009
 * Time: 19:53:14
 * To change this template use File | Settings | File Templates.
 */
public class TotalSeriesMetric extends AbstractServerMetric {

    private static final String id = "TotalSeries";
    private String parentContextPath;
    private TimeSeriesContext rootContext;
    private TimePeriod countPeriod;

    public TotalSeriesMetric(String parentContextPath, TimeSeriesContext rootContext) {
        this(parentContextPath, rootContext, DEFAULT_TIME_PERIOD_FOR_SERVER_METRICS);
    }

    public TotalSeriesMetric(String parentContextPath, TimeSeriesContext rootContext, TimePeriod countPeriod) {
        this.parentContextPath = parentContextPath;
        this.rootContext = rootContext;
        this.countPeriod = countPeriod;
    }


    public String getSeriesId() {
        return id;
    }

    public String getParentContextPath() {
        return parentContextPath;
    }

    public void initializeMetric(TimeSeriesContext metricContext) {
        metricContext.newTimedValueSource(
            id,
            "Total number of series managed by the server",
            new TotalSeriesCountValueSupplier(),
            countPeriod
        );
    }

    private class TotalSeriesCountValueSupplier implements ValueSupplier {

        public Numeric getValue() {
            return LongNumeric.valueOf(rootContext.findAllTimeSeries().getNumberOfMatches());
        }
    }
}
