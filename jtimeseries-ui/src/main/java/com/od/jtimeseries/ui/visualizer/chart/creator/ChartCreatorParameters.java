package com.od.jtimeseries.ui.visualizer.chart.creator;

import com.od.jtimeseries.ui.config.ChartRangeMode;
import com.od.jtimeseries.ui.config.DomainTimeSelection;
import com.od.jtimeseries.ui.timeseries.ChartingTimeSeries;

import java.awt.*;
import java.util.List;

public class ChartCreatorParameters {

    private final ChartRangeMode chartRangeMode;
    private final DomainTimeSelection domainSelection;
    private final Color chartBackgroundColor;
    private final List<ChartingTimeSeries> timeSeriesList;
    private final boolean showLegend;
    private final String title;
    private ChartDataFilter chartDataFilter;

    public ChartCreatorParameters(ChartRangeMode chartRangeMode, DomainTimeSelection domainSelection, Color chartBackgroundColor, List<ChartingTimeSeries> timeSeriesList, boolean showLegend, String title, ChartDataFilter chartDataFilter) {
        this.chartRangeMode = chartRangeMode;
        this.domainSelection = domainSelection;
        this.chartBackgroundColor = chartBackgroundColor;
        this.timeSeriesList = timeSeriesList;
        this.showLegend = showLegend;
        this.title = title;
        this.chartDataFilter = chartDataFilter;
    }

    public ChartRangeMode getChartRangeMode() {
        return chartRangeMode;
    }

    public DomainTimeSelection getDomainSelection() {
        return domainSelection;
    }

    public Color getChartBackgroundColor() {
        return chartBackgroundColor;
    }

    public List<ChartingTimeSeries> getTimeSeriesList() {
        return timeSeriesList;
    }

    public boolean isShowLegend() {
        return showLegend;
    }

    public String getTitle() {
        return title;
    }

    public ChartDataFilter getChartDataFilter() {
        return chartDataFilter;
    }
}
