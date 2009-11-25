package com.od.jtimeseries.server.servermetrics;

import com.od.jtimeseries.capture.impl.DefaultCapture;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.source.ValueRecorder;
import com.od.jtimeseries.source.impl.DefaultValueRecorder;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.util.time.Time;
import com.od.jtimeseries.util.time.TimePeriod;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 25-Nov-2009
 * Time: 19:53:14
 * To change this template use File | Settings | File Templates.
 */
public class TotalSeriesCountMetric extends ServerMetric {

    public static final String TOTAL_SERIES_COUNT_ID = "TotalSeriesCount";
    private static final String COUNT_OF_UDP_SERIES_UPDATES_DESC = "Total number of series managed by the server";

    private TimeSeriesContext rootContext;
    private ValueRecorder matchesValueRecorder;

    public TotalSeriesCountMetric(TimeSeriesContext rootContext) {
        this.rootContext = rootContext;
    }

    public TimePeriod getSchedulingPeriod() {
        return Time.minutes(15);
    }

    public String getSeriesId() {
        return TOTAL_SERIES_COUNT_ID;
    }

    public String getMetricDescription() {
        return COUNT_OF_UDP_SERIES_UPDATES_DESC;
    }

    public void setupSeries(TimeSeriesContext metricContext, IdentifiableTimeSeries timeSeries) {
        //n.b. usually we could use metricContext.createCounter() to create the counter, capture and series in one method call,
        //but here the series is already created (may have been reloaded) so create and add the Counter and timed capture the hard way.
        matchesValueRecorder = new DefaultValueRecorder("Source_" + getSeriesId(), "Counter for " + getSeriesId());
        DefaultCapture t = new DefaultCapture("Capture " + getSeriesId(), matchesValueRecorder, timeSeries);
        metricContext.addChild(matchesValueRecorder, t);

        //take a first value immediately
        run();
    }

    public void run() {
        matchesValueRecorder.newValue(
            rootContext.findAllTimeSeries().getNumberOfMatches()
        );
    }
}
