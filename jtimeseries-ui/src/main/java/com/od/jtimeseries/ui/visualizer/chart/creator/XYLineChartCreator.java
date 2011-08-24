package com.od.jtimeseries.ui.visualizer.chart.creator;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 09/08/11
 * Time: 09:00
 */
public class XYLineChartCreator extends AbstractXYChartCreator {

    public XYLineChartCreator(ChartCreatorParameters p) {
        super(p);
    }

    protected JFreeChart buildChart() {
        return ChartFactory.createTimeSeriesChart(
                getTitle(),
                "Time of Day",
                null,
                null,
                isShowLegend(),
                true,
                false
        );
    }

    protected XYItemRenderer createXYItemRenderer(XYDataset dataSet) {
        return new EfficientXYLineAndShapeRenderer(true, false);
    }

}
