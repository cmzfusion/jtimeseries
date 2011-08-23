/**
 * Copyright (C) 2011 (nick @ objectdefinitions.com)
 *
 * This file is part of JTimeseries.
 *
 * JTimeseries is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JTimeseries is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JTimeseries.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.od.jtimeseries.ui.visualizer.chart.creator;

import com.od.jtimeseries.chart.TimeSeriesTableModelAdapter;
import com.od.jtimeseries.chart.TimeSeriesXYDataset;
import com.od.jtimeseries.timeseries.TimeSeriesEvent;
import com.od.jtimeseries.timeseries.TimeSeriesListener;
import com.od.jtimeseries.timeseries.impl.MovingWindowTimeSeries;
import com.od.jtimeseries.ui.config.ChartRangeMode;
import com.od.jtimeseries.ui.config.DomainTimeSelection;
import com.od.jtimeseries.ui.timeseries.ChartingTimeSeries;
import com.od.jtimeseries.util.time.Time;
import com.od.jtimeseries.util.time.TimeSource;
import com.od.swing.util.AwtSafeListener;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;

import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 27-Feb-2010
 * Time: 11:26:46
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractXYChartCreator {

    private XYPlot plot;
    private ChartRangeMode chartRangeMode;
    private HashMap<String, NumberAxis> axisBySeriesId = new HashMap<String, NumberAxis>();
    private HashMap<String, Integer> axisIndexBySeriesId = new HashMap<String, Integer>();
    private DomainTimeSelection domainSelection;
    private boolean showLegend;
    private String title;
    private Color chartBackgroundColor;
    private DateFormat dateFormat = new SimpleDateFormat("MMMdd HH:mm");
    private java.util.List<ChartingTimeSeries> timeSeriesList = Collections.EMPTY_LIST;
    private ChartDataFilter chartDataFilter;

    public AbstractXYChartCreator(ChartCreatorParameters p) {
        this.chartBackgroundColor = p.getChartBackgroundColor();
        this.timeSeriesList = p.getTimeSeriesList();
        this.chartRangeMode = p.getChartRangeMode();
        this.domainSelection = p.getDomainSelection();
        this.showLegend = p.isShowLegend();
        this.title = p.getTitle();
        this.chartDataFilter = p.getChartDataFilter();
    }

    public JFreeChart createNewChart() {
        JFreeChart chart = createChart();
        plot = (XYPlot)chart.getPlot();
        plot.setBackgroundPaint(chartBackgroundColor);
        plot.setAxisOffset(new RectangleInsets(5,5,5,5));
        addSeries();
        return chart;
    }

    private JFreeChart createChart() {
        final JFreeChart chart = buildChart();
        chart.getXYPlot().setDomainAxis(new DateAxis());
        DateAxis dateAxis = (DateAxis)chart.getXYPlot().getDomainAxis();
        dateAxis.setDateFormatOverride(dateFormat);
        return chart;
    }

    protected abstract JFreeChart buildChart();

    private void addSeries() {
        for ( int loop=0; loop < timeSeriesList.size(); loop++) {
            ChartingTimeSeries series = timeSeriesList.get(loop);
            addSeriesToChart(series, loop);
        }
    }

    private void addSeriesToChart(ChartingTimeSeries contextTimeSeries, int seriesId) {
        XYDataset dataSet = createDataSet(contextTimeSeries);
        plot.setDataset(seriesId, dataSet);
        XYItemRenderer renderer = createXYItemRenderer(seriesId);

        plot.setRenderer(seriesId, renderer);
        createRangeAxes(contextTimeSeries, seriesId, plot);
        setSeriesColor(plot, seriesId, contextTimeSeries);
    }

    protected abstract XYItemRenderer createXYItemRenderer(int seriesId);

    private void createRangeAxes(ChartingTimeSeries contextTimeSeries, int seriesId, XYPlot plot) {
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
                plot.setRangeAxis(axisIndex, axis);
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

    private void setSeriesColor(XYPlot plot, int series, ChartingTimeSeries remoteChartingTimeSeries) {
        Color seriesColor = remoteChartingTimeSeries.getColor();
        XYItemRenderer renderer = plot.getRenderer(series);
        renderer.setSeriesPaint(0, seriesColor);
        if (chartRangeMode == ChartRangeMode.RangePerSeries) {
            plot.getRangeAxis(series).setLabelPaint(seriesColor);
            plot.getRangeAxis(series).setTickLabelPaint(seriesColor);
        }
    }


    private XYDataset createDataSet(ChartingTimeSeries contextTimeSeries) {
//        TimeSeriesTableModelAdapter timeSeriesTableModelAdapter = new TimeSeriesTableModelAdapter(
//            contextTimeSeries,
//            domainSelection,
//            true
//        );
        final MovingWindowTimeSeries movingWindowTimeSeries = new MovingWindowTimeSeries(domainSelection, TimeSource.OPEN_END_TIME, Time.seconds(10));
        movingWindowTimeSeries.setUpdateWindowInSwingEventThread(true);

        contextTimeSeries.addTimeSeriesListener(AwtSafeListener.getAwtSafeListener(new TimeSeriesListener() {
            public void itemsAddedOrInserted(TimeSeriesEvent e) {
                movingWindowTimeSeries.addAll(e.getItems());
            }

            public void itemsRemoved(TimeSeriesEvent e) {
                movingWindowTimeSeries.removeAll(e.getItems());
            }

            public void seriesChanged(TimeSeriesEvent e) {
                movingWindowTimeSeries.clear();
                movingWindowTimeSeries.addAll(e.getItems());
            }
        }, TimeSeriesListener.class));

        TimeSeriesTableModelAdapter timeSeriesTableModelAdapter = new TimeSeriesTableModelAdapter(movingWindowTimeSeries, contextTimeSeries.getDescription());

        //create a TimeSeriesXYDataset which applies TreatNanAsZero filter
        return new TimeSeriesXYDataset(contextTimeSeries.getDisplayName(), timeSeriesTableModelAdapter) {
            public Number getY(int series, int item) {
                Number result = super.getY(series, item);
                if ( Double.isNaN(result.doubleValue())) {
                    result = chartDataFilter == ChartDataFilter.NanAsZero ? Double.valueOf(0) : Double.NaN;
                }
                return result;
            }
        };
    }

    protected boolean isShowLegend() {
        return showLegend;
    }

    public String getTitle() {
        return title;
    }

}
