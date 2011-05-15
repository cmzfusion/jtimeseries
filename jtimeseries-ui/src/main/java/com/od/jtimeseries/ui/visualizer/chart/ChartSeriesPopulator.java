/**
 * Copyright (C) 2009 (nick @ objectdefinitions.com)
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
package com.od.jtimeseries.ui.visualizer.chart;

import com.od.jtimeseries.chart.TimeSeriesTableModelAdapter;
import com.od.jtimeseries.chart.TimeSeriesXYDataset;
import com.od.jtimeseries.ui.config.ChartRangeMode;
import com.od.jtimeseries.ui.config.DomainTimeSelection;
import com.od.jtimeseries.ui.timeseries.ChartingTimeSeries;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;

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

    private XYPlot plot;
    private ChartRangeMode chartRangeMode;
    private HashMap<String, NumberAxis> axisBySeriesId = new HashMap<String, NumberAxis>();
    private HashMap<String, Integer> axisIndexBySeriesId = new HashMap<String, Integer>();
    private DomainTimeSelection domainSelection;

    public ChartSeriesPopulator(XYPlot plot, ChartRangeMode chartRangeMode, DomainTimeSelection domainSelection) {
        this.plot = plot;
        this.chartRangeMode = chartRangeMode;
        this.domainSelection = domainSelection;
    }

    public void addSeriesToChart(ChartingTimeSeries contextTimeSeries, int seriesId) {
        XYDataset dataSet = createDataSet(contextTimeSeries);
        plot.setDataset(seriesId, dataSet);
        EfficientXYLineAndShapeRenderer renderer2 = new EfficientXYLineAndShapeRenderer();
//        XYLineAndShapeRenderer renderer2 = new XYLineAndShapeRenderer(true, false);

        plot.setRenderer(seriesId, renderer2);
        createRangeAxes(contextTimeSeries, seriesId, plot);
        setSeriesColor(plot, seriesId, contextTimeSeries);
    }

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
        TimeSeriesTableModelAdapter timeSeriesTableModelAdapter = new TimeSeriesTableModelAdapter(
            contextTimeSeries,
            domainSelection,
            true
        );
        return new TimeSeriesXYDataset(contextTimeSeries.getDisplayName(), timeSeriesTableModelAdapter);
    }


}
