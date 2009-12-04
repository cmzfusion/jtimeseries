package com.od.jtimeseries.server.servermetrics;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.source.ValueSupplier;
import com.od.jtimeseries.util.time.Time;
import com.od.jtimeseries.util.numeric.Numeric;
import com.od.jtimeseries.util.numeric.LongNumeric;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 25-Nov-2009
 * Time: 19:53:14
 * To change this template use File | Settings | File Templates.
 */
public class TotalSeriesCountMetric extends ServerMetric {

    private static final String id = "TotalSeriesCount";
    private TimeSeriesContext rootContext;

    public TotalSeriesCountMetric(TimeSeriesContext rootContext) {
        this.rootContext = rootContext;
    }

    public String getSeriesId() {
        return id;
    }

    public void setupSeries(TimeSeriesContext metricContext) {
        metricContext.createTimedValueSource(
                id,
            "Total number of series managed by the server",
            new TotalSeriesCountValueSupplier(),
            Time.minutes(15)
        );
    }

    private class TotalSeriesCountValueSupplier implements ValueSupplier {

        public Numeric getValue() {
            return new LongNumeric(rootContext.findAllTimeSeries().getNumberOfMatches());
        }
    }
}
