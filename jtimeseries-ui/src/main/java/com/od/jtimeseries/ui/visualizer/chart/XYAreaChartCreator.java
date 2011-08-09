package com.od.jtimeseries.ui.visualizer.chart;

import com.od.jtimeseries.ui.config.ChartRangeMode;
import com.od.jtimeseries.ui.config.DomainTimeSelection;
import com.od.jtimeseries.ui.timeseries.ChartingTimeSeries;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYAreaRenderer2;
import org.jfree.chart.renderer.xy.XYItemRenderer;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 09/08/11
 * Time: 08:59
 */
public class XYAreaChartCreator extends XYChartCreator {

    public XYAreaChartCreator(ChartRangeMode chartRangeMode, DomainTimeSelection domainSelection, Color chartBackgroundColor, java.util.List<ChartingTimeSeries> timeSeriesList, boolean showLegend, String title) {
        super(chartRangeMode, domainSelection, chartBackgroundColor, timeSeriesList, showLegend, title);
    }

    protected JFreeChart buildChart() {
        return ChartFactory.createXYAreaChart(
                getTitle(),
                "Time of Day",
                "Value",
                null,
                PlotOrientation.VERTICAL,
                isShowLegend(),
                true,
                false
        );
    }

    protected XYItemRenderer createXYItemRenderer(int seriesId) {
        return new XYAreaRenderer2();
    }

}
