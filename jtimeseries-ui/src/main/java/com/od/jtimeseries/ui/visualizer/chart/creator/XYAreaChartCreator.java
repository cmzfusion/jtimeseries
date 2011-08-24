package com.od.jtimeseries.ui.visualizer.chart.creator;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYAreaRenderer2;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 09/08/11
 * Time: 08:59
 */
public class XYAreaChartCreator extends AbstractXYChartCreator {

    public XYAreaChartCreator(ChartCreatorParameters p) {
        super(p);
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

    protected XYItemRenderer createXYItemRenderer(XYDataset dataSet) {
        return new XYAreaRenderer2();
    }

}
