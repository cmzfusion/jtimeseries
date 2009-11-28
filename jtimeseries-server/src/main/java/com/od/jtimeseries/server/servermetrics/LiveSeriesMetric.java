package com.od.jtimeseries.server.servermetrics;

import com.od.jtimeseries.capture.impl.DefaultCapture;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.server.message.AppendToSeriesMessageListener;
import com.od.jtimeseries.source.ValueRecorder;
import com.od.jtimeseries.source.impl.DefaultValueRecorder;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.util.time.TimePeriod;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 25-Nov-2009
 * Time: 19:33:20
 * To change this template use File | Settings | File Templates.
 */
public class LiveSeriesMetric extends ServerMetric {

    public static final String LIVE_SERIES_TOTAL_METRIC_ID = "TotalLiveSeries";
    private static final String LIVE_SERIES_TOTAL_METRIC_DESC =
            "A count of the series which have recevied updates during the last " + AppendToSeriesMessageListener.STALE_SERIES_DELAY;

    public TimePeriod getSchedulingPeriod() {
        return null;
    }

    public String getSeriesId() {
        return LIVE_SERIES_TOTAL_METRIC_ID;
    }

    public String getMetricDescription() {
        return LIVE_SERIES_TOTAL_METRIC_DESC;
    }

    public void setupSeries(TimeSeriesContext metricContext, IdentifiableTimeSeries timeSeries) {
        //n.b. usually we could use metricContext.createCounter() to create the counter, capture and series in one method call,
        //but here the series is already created (may have been reloaded) so create and add the Counter and timed capture the hard way.
        ValueRecorder v = new DefaultValueRecorder("Source_" + getSeriesId(), "Value Recorder for " + getSeriesId());
        DefaultCapture t = new DefaultCapture("Capture " + getSeriesId(), v, timeSeries);
        metricContext.addChild(v, t);
        AppendToSeriesMessageListener.setLiveSeriesTotalValueRecorder(v);
    }

    public void run() {
    }
}
