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

import com.od.jtimeseries.chart.MovingWindowXYDataset;
import com.od.jtimeseries.ui.config.ChartRangeMode;
import com.od.jtimeseries.ui.config.DomainTimeSelection;
import com.od.jtimeseries.ui.timeseries.ChartingTimeSeries;
import com.od.jtimeseries.util.time.Time;
import com.od.jtimeseries.util.time.TimeSource;
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
        Map<String, MovingWindowXYDataset<ChartingTimeSeries>> dataSets = new HashMap<String, MovingWindowXYDataset<ChartingTimeSeries>>();
        for (ChartingTimeSeries series : timeSeriesList) {
            addToDataSet(dataSets, series);
        }

        int index = 0;
        for ( MovingWindowXYDataset<ChartingTimeSeries> d : dataSets.values()) {
            plot.setDataset(index, d);

            XYItemRenderer renderer = createXYItemRenderer(d);
            plot.setRenderer(index, renderer);

            for ( int series=0; series < d.getSeriesCount(); series++) {
                ChartingTimeSeries c = d.getTimeSeries(series);
                renderer.setSeriesPaint(series, c.getColor());
            }

            if (chartRangeMode == ChartRangeMode.RangePerSeries) {
                plot.getRangeAxis(index).setLabelPaint(d.getTimeSeries(0).getColor());
                plot.getRangeAxis(index).setTickLabelPaint(d.getTimeSeries(0).getColor());
            }

            d.startMovingWindow(Time.seconds(10));
            index++;
        }
    }

    private void addToDataSet(Map<String, MovingWindowXYDataset<ChartingTimeSeries>> dataSets, ChartingTimeSeries series) {
        String key;
        switch(chartRangeMode) {
            case RangePerSeries :
                key = series.getPath();
                break;
            case RangePerId:
                key = series.getId();
                break;
            case SingleRange:
            default:
                key = "SingleRange";
        }
        addSeriesToDataSet(key, dataSets);
    }

    private MovingWindowXYDataset addSeriesToDataSet(String key, Map<String, MovingWindowXYDataset<ChartingTimeSeries>> dataSets) {
        MovingWindowXYDataset<ChartingTimeSeries> s = dataSets.get(key);
        if ( s == null) {
            s = new MovingWindowXYDataset<ChartingTimeSeries>(domainSelection, TimeSource.OPEN_END_TIME);
            dataSets.put(key, s);
        }
        return s;
    }

    protected abstract XYItemRenderer createXYItemRenderer(XYDataset dataSet);

    private void createRangeAxes(ChartingTimeSeries contextTimeSeries, int datasetId, XYPlot plot) {
        switch(chartRangeMode) {
            case RangePerSeries:
                NumberAxis axis = new NumberAxis(contextTimeSeries.getDisplayName());
                plot.setRangeAxis(datasetId, axis);
                plot.setRangeAxisLocation(datasetId, datasetId % 2 == 0 ? AxisLocation.BOTTOM_OR_LEFT : AxisLocation.BOTTOM_OR_RIGHT);
                plot.mapDatasetToRangeAxis(datasetId, datasetId);
                break;
            case RangePerId :
                String id = contextTimeSeries.getId();
                axis = getOrCreateAxis(id);
                int axisIndex = getAxisIndex(id);
                plot.setRangeAxis(axisIndex, axis);
                plot.mapDatasetToRangeAxis(datasetId, axisIndex);
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

    protected boolean isShowLegend() {
        return showLegend;
    }

    public String getTitle() {
        return title;
    }

}
