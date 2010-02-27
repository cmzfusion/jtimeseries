package com.od.jtimeseries.ui.chart;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import com.od.jtimeseries.ui.timeseries.RemoteChartingTimeSeries;
import com.od.jtimeseries.chart.TimeSeriesTableModelAdapter;
import com.od.jtimeseries.chart.TimeSeriesXYDataset;

import java.awt.*;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 27-Feb-2010
 * Time: 11:26:46
 * To change this template use File | Settings | File Templates.
 */
public class ChartSeriesPopulator {

    private JFreeChart chart;
    XYPlot plot;
    private ChartRangeMode chartRangeMode;
    private HashMap<String, NumberAxis> axisBySeriesId = new HashMap<String, NumberAxis>();
    private HashMap<String, Integer> axisIndexBySeriesId = new HashMap<String, Integer>();


    public ChartSeriesPopulator(JFreeChart chart, ChartRangeMode chartRangeMode) {
        this.chart = chart;
        this.chartRangeMode = chartRangeMode;
        this.plot = (XYPlot)chart.getPlot();
    }

    public void addSeriesToChart(RemoteChartingTimeSeries contextTimeSeries, int seriesId) {
        XYDataset dataSet = createDataSet(contextTimeSeries);
        plot.setDataset(seriesId, dataSet);
        XYItemRenderer renderer2 = new StandardXYItemRenderer();
        plot.setRenderer(seriesId, renderer2);
        createRangeAxes(contextTimeSeries, seriesId, plot);
        setSeriesColor(plot, seriesId, contextTimeSeries);
    }

    private void createRangeAxes(RemoteChartingTimeSeries contextTimeSeries, int seriesId, XYPlot plot) {
        switch(chartRangeMode) {
            case RangePerSeries:
                NumberAxis axis = new NumberAxis(contextTimeSeries.getDisplayName());
                plot.setRangeAxis(seriesId, axis);
                plot.setRangeAxisLocation(seriesId, seriesId % 2 == 0 ? AxisLocation.BOTTOM_OR_LEFT : AxisLocation.BOTTOM_OR_RIGHT);
                plot.mapDatasetToRangeAxis(seriesId, seriesId);
                break;
            case RangePerId :
                String id = contextTimeSeries.getId();
                axis = getOrCreateAxis(id);
                int axisIndex = getAxisIndex(id);
                plot.setRangeAxis(seriesId, axis);
                plot.mapDatasetToRangeAxis(seriesId, axisIndex);
                break;
            default :
                plot.setRangeAxis(0, new NumberAxis("values"));
        }
    }

    private NumberAxis getOrCreateAxis(String id) {
        NumberAxis axis;
        axis = axisBySeriesId.get(id);
        if ( axis == null ) {
            axis = new NumberAxis(id);
            int newAxisIndex = axisBySeriesId.size();
            axisIndexBySeriesId.put(id, newAxisIndex);
            axisBySeriesId.put(id, axis);
            plot.setRangeAxisLocation(newAxisIndex, newAxisIndex % 2 == 0 ? AxisLocation.BOTTOM_OR_LEFT : AxisLocation.BOTTOM_OR_RIGHT);
        }
        return axis;
    }

    private int getAxisIndex(String id) {
        return axisIndexBySeriesId.get(id);
    }

    private void setSeriesColor(XYPlot plot, int series, RemoteChartingTimeSeries remoteChartingTimeSeries) {
        Color seriesColor = remoteChartingTimeSeries.getColor();
        XYItemRenderer renderer = plot.getRenderer(series);
        renderer.setSeriesPaint(0, seriesColor);
        if (chartRangeMode == ChartRangeMode.RangePerSeries) {
            plot.getRangeAxis(series).setLabelPaint(seriesColor);
            plot.getRangeAxis(series).setTickLabelPaint(seriesColor);
        }
    }


    private XYDataset createDataSet(RemoteChartingTimeSeries contextTimeSeries) {
        TimeSeriesTableModelAdapter timeSeriesTableModelAdapter = new TimeSeriesTableModelAdapter(contextTimeSeries);
        return new TimeSeriesXYDataset(contextTimeSeries.getDisplayName(), timeSeriesTableModelAdapter);
    }


}
