package com.od.jtimeseries.server.servermetrics;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.server.message.AppendToSeriesMessageListener;
import com.od.jtimeseries.source.ValueRecorder;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 25-Nov-2009
 * Time: 19:33:20
 * To change this template use File | Settings | File Templates.
 */
public class LiveSeriesMetric extends AbstractServerMetric {

    private static final String id = "TotalLiveSeries";
    private String parentContextPath;

    public LiveSeriesMetric(String parentContextPath) {
        this.parentContextPath = parentContextPath;
    }

    public String getSeriesId() {
        return id;
    }

    public String getParentContextPath() {
        return parentContextPath;
    }

    public void setupSeries(TimeSeriesContext metricContext) {
        ValueRecorder v = metricContext.createValueRecorder(id, "A count of the series which have recevied updates during the last " + AppendToSeriesMessageListener.STALE_SERIES_DELAY);
        AppendToSeriesMessageListener.setLiveSeriesTotalValueRecorder(v);
    }
}
