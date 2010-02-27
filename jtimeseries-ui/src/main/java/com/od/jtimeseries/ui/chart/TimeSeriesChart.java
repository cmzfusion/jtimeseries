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
package com.od.jtimeseries.ui.chart;

import com.od.jtimeseries.ui.timeseries.RemoteChartingTimeSeries;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeEventType;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.RectangleInsets;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 07-Jan-2009
 * Time: 14:28:23
 */
public class TimeSeriesChart extends JPanel {

    private static final String[] CHART_REFRESH_LISTEN_PROPERTIES = new String[] {
            RemoteChartingTimeSeries.DISPLAY_NAME_PROPERTY,
            RemoteChartingTimeSeries.COLOUR_PROPERTY
    };

    private String title;
    private List<RemoteChartingTimeSeries> timeSeriesList = Collections.EMPTY_LIST;
    private ChartPanel chartPanel;
    private JPanel noChartsPanel = new JPanel();
    private ChartRangeMode chartRangeMode = ChartRangeMode.RangePerId;
    private DateFormat dateFormat = new SimpleDateFormat("MMMdd HH:mm");
    private Color chartBackgroundColor = Color.WHITE;

    private PropertyChangeListener refreshChartPropertyListener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            createAndSetChart();
        }
    };
    private JFreeChart chart;
    private boolean showLegend = true;

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

        //our own copy guaranteed to be RandomAccess list
        newSelection = new ArrayList<RemoteChartingTimeSeries>(newSelection);
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

    public void setChartBackgroundColor(Color c) {
        chartBackgroundColor = c;
        createAndSetChart();
    }

    public Color getChartBackgroundColor() {
        return chartBackgroundColor;
    }

    private void addPropertyListener(List<RemoteChartingTimeSeries> newSelection) {
        for ( RemoteChartingTimeSeries s : newSelection) {
            for ( String property : CHART_REFRESH_LISTEN_PROPERTIES) {
                s.addPropertyChangeListener(property, refreshChartPropertyListener);
            }
        }
    }

    private void removePropertyListener(List<RemoteChartingTimeSeries> timeSeriesList) {
        for ( RemoteChartingTimeSeries s : timeSeriesList) {
            for ( String property : CHART_REFRESH_LISTEN_PROPERTIES) {
                s.removePropertyChangeListener(property, refreshChartPropertyListener);
            }
        }
    }

    public ChartRangeMode getChartRangeMode() {
        return chartRangeMode;
    }

    public void setChartRangeMode(ChartRangeMode m ) {
        if ( this.chartRangeMode != m ) {
            this.chartRangeMode = m;
            createAndSetChart();
        }
    }

    public void setShowLegend(boolean showLegend) {
        if ( this.showLegend != showLegend) {
            this.showLegend = showLegend;
            createAndSetChart();
        }
    }

    public boolean isShowLegend() {
        return this.showLegend;
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
        plot.setBackgroundPaint(chartBackgroundColor);
        plot.setAxisOffset(new RectangleInsets(5,5,5,5));
        addSeries(chart);
        return chart;
    }

    private void addSeries(JFreeChart chart) {
        ChartSeriesPopulator c = new ChartSeriesPopulator(chart, chartRangeMode);
        for ( int loop=0; loop < timeSeriesList.size(); loop++) {
            RemoteChartingTimeSeries series = timeSeriesList.get(loop);
            System.out.println("Adding series " + series.getId());
            c.addSeriesToChart(series, loop);
        }
    }

    private JFreeChart createChart() {
        final JFreeChart chart = ChartFactory.createTimeSeriesChart(
            title,
            "Time of Day",
            null,
            null,
            showLegend,
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
}
