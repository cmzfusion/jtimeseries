package com.od.jtimeseries.ui.visualizer.chart.creator;

import com.od.jtimeseries.ui.config.ChartRangeMode;
import com.od.jtimeseries.ui.config.DomainTimeSelection;
import com.od.jtimeseries.ui.timeseries.ChartingTimeSeries;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.XYItemRenderer;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 09/08/11
 * Time: 09:00
 */
public class XYLineChartCreator extends XYChartCreator {

    public XYLineChartCreator(ChartCreatorParameters p) {
        super(p);
    }

    protected XYItemRenderer createXYItemRenderer(int seriesId) {
        return new EfficientXYLineAndShapeRenderer();
//        XYLineAndShapeRenderer renderer2 = new XYLineAndShapeRenderer(true, false);
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
}
