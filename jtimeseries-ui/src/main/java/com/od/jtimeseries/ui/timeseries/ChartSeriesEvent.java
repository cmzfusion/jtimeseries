package com.od.jtimeseries.ui.timeseries;

import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 25-Nov-2010
 * Time: 18:12:22
 * To change this template use File | Settings | File Templates.
 */
public class ChartSeriesEvent {

    private UIPropertiesTimeSeries sourceSeries;
    private ChartSeriesEventType chartEvent;

    public ChartSeriesEvent(UIPropertiesTimeSeries sourceSeries, ChartSeriesEventType chartEvent) {
        this.sourceSeries = sourceSeries;
        this.chartEvent = chartEvent;
    }

    public UIPropertiesTimeSeries getSourceSeries() {
        return sourceSeries;
    }

    public ChartSeriesEventType getChartSeriesEventType() {
        return chartEvent;
    }

    public void setChartEvent(ChartSeriesEventType chartEvent) {
        this.chartEvent = chartEvent;
    }

    public static enum ChartSeriesEventType {
        SERIES_CHART_DISPLAYED,
        SERIES_CHART_HIDDEN,
        SERIES_CHART_DISPOSED
    }
}
