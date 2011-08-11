package com.od.jtimeseries.ui.visualizer.chart.creator;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYItemRenderer;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 11/08/11
 * Time: 13:25
 */
public class XYScatterChartCreator extends XYChartCreator {

    public XYScatterChartCreator(ChartCreatorParameters p) {
        super(p);
    }

    protected JFreeChart buildChart() {
        return ChartFactory.createScatterPlot(
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
        return new EfficientXYLineAndShapeRenderer(false, true);
    }

}
