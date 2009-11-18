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
package com.od.jtimeseries.ui;

import com.od.jtimeseries.chart.TimeSeriesTableModelAdapter;
import com.od.jtimeseries.chart.TimeSeriesXYDataset;
import com.od.jtimeseries.ui.timeseries.RemoteChartingTimeSeries;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeEventType;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 07-Jan-2009
 * Time: 14:28:23
 */
public class TimeSeriesChart extends JPanel {

    private String title;
    private List<RemoteChartingTimeSeries> timeSeriesList = Collections.EMPTY_LIST;
    private Color[] seriesColors = new Color[] {
            Color.BLUE.darker(),
            Color.GREEN.darker(),
            Color.RED.darker(),
            Color.BLACK,
            Color.GRAY,
            Color.CYAN.darker(),
            Color.DARK_GRAY,
            Color.MAGENTA,
            Color.ORANGE,
            Color.YELLOW.darker(),
            Color.PINK
    };
    private ChartPanel chartPanel;
    private JPanel noChartsPanel = new JPanel();
    private boolean multipleRange = true;
    private DateFormat dateFormat = new SimpleDateFormat("MMMdd HH:mm");

    private PropertyChangeListener refreshChartPropertyListener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            createAndSetChart();
        }
    };
    private JFreeChart chart;

    public TimeSeriesChart(String title) {
        this.title = title;
        setLayout(new BorderLayout());
        createNoChartsPanel();
        add(noChartsPanel, BorderLayout.CENTER);
    }

    private void createNoChartsPanel() {
        noChartsPanel.setLayout(new BorderLayout());
        JLabel noSeriesLabel = new JLabel("No Timeseries Selected");
        noSeriesLabel.setHorizontalAlignment(JLabel.CENTER);
        noSeriesLabel.setVerticalAlignment(JLabel.CENTER);
        noChartsPanel.add(noSeriesLabel);
    }

    public void setSeries(List<RemoteChartingTimeSeries> newSelection) {
        removePropertyListener(this.timeSeriesList);
        addPropertyListener(newSelection);
        this.timeSeriesList = newSelection;
        if ( timeSeriesList.size() == 0) {
            if ( chartPanel != null) {
                remove(chartPanel);
                chart = null;
            }
            add(noChartsPanel, BorderLayout.CENTER);
        } else {
            createAndSetChart();
            remove(noChartsPanel);
            add(chartPanel, BorderLayout.CENTER);
        }
        validate();
        repaint();
    }

    private void addPropertyListener(List<RemoteChartingTimeSeries> newSelection) {
        for ( RemoteChartingTimeSeries s : newSelection) {
            s.addPropertyChangeListener(
                    RemoteChartingTimeSeries.DISPLAY_NAME_PROPERTY,
                    refreshChartPropertyListener
            );
        }
    }

    private void removePropertyListener(List<RemoteChartingTimeSeries> timeSeriesList) {
        for ( RemoteChartingTimeSeries s : timeSeriesList) {
            s.removePropertyChangeListener(refreshChartPropertyListener);
        }
    }

    public boolean isMultipleRange() {
        return multipleRange;
    }

    public void setMultipleRange(boolean multipleRange) {
        if ( this.multipleRange != multipleRange ) {
            this.multipleRange = multipleRange;
            createAndSetChart();
        }
    }

    private void createAndSetChart() {
        chart = createNewChart();
        if ( chartPanel == null ) {
            chartPanel = new ChartPanel(chart);
        } else {
            chartPanel.setChart(chart);
        }
    }

    private JFreeChart createNewChart() {
        JFreeChart chart = createChart();
        XYPlot plot = (XYPlot)chart.getPlot();
        plot.setAxisOffset(new RectangleInsets(5,5,5,5));
        addSeries(chart);
        setColors(plot);
        return chart;
    }

    private void setColors(XYPlot plot) {
        for ( int loop=0; loop < timeSeriesList.size(); loop++) {
            setSeriesColor(plot, loop);
        }
    }

    private void addSeries(JFreeChart chart) {
        for ( int loop=1; loop < timeSeriesList.size(); loop++) {
            addSeriesToChart(chart, timeSeriesList.get(loop), loop);
        }
    }

    private void addSeriesToChart(JFreeChart chart, RemoteChartingTimeSeries contextTimeSeries, int seriesId) {
        XYDataset dataSet = createDataSet(contextTimeSeries);
        XYPlot plot = (XYPlot)chart.getPlot();
        plot.setDataset(seriesId, dataSet);
        XYItemRenderer renderer2 = new StandardXYItemRenderer();
        plot.setRenderer(seriesId, renderer2);
        if ( multipleRange) {
            NumberAxis axis = new NumberAxis(contextTimeSeries.getDisplayName());
            plot.setRangeAxis(seriesId, axis);
            plot.setRangeAxisLocation(seriesId, seriesId % 2 == 0 ? AxisLocation.BOTTOM_OR_LEFT : AxisLocation.BOTTOM_OR_RIGHT);
            plot.mapDatasetToRangeAxis(seriesId, seriesId);
        } else {
            plot.setRangeAxis(0, new NumberAxis("values"));
        }
        setSeriesColor(plot, seriesId);
    }

    private void setSeriesColor(XYPlot plot, int series) {
        Color seriesColor = getColor(series);
        XYItemRenderer renderer = plot.getRenderer(series);
        renderer.setSeriesPaint(0, seriesColor);
        if ( multipleRange) {
            plot.getRangeAxis(series).setLabelPaint(seriesColor);
            plot.getRangeAxis(series).setTickLabelPaint(seriesColor);
        }
    }

    private Color getColor(int seriesId) {
        return seriesColors[seriesId % seriesColors.length];
    }

    private JFreeChart createChart() {

        RemoteChartingTimeSeries m = getPrimaryTimeSeries();

        final JFreeChart chart = ChartFactory.createTimeSeriesChart(
            title,
            "Time of Day",
            m.getDisplayName(),
            createDataSet(m),
            true,
            true,
            false
        );
        addTitleChangeListener(chart);

        DateAxis dateAxis = (DateAxis)chart.getXYPlot().getDomainAxis();
        dateAxis.setDateFormatOverride(dateFormat);
        return chart;
    }

    private void addTitleChangeListener(final JFreeChart chart) {
        chart.addChangeListener(new ChartChangeListener() {

            public void chartChanged(ChartChangeEvent event) {
                if ( event.getType() == ChartChangeEventType.GENERAL) {
                    TimeSeriesChart.this.title = chart.getTitle().getText();
                }
            }
        });
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title =  title;
        if ( chart != null ) {
            chart.setTitle(title);
        }
    }

    private RemoteChartingTimeSeries getPrimaryTimeSeries() {
        return timeSeriesList.get(0);
    }

    private XYDataset createDataSet(RemoteChartingTimeSeries contextTimeSeries) {
        TimeSeriesTableModelAdapter timeSeriesTableModelAdapter = new TimeSeriesTableModelAdapter(contextTimeSeries);
        return new TimeSeriesXYDataset(contextTimeSeries.getDisplayName(), timeSeriesTableModelAdapter);
    }

}
