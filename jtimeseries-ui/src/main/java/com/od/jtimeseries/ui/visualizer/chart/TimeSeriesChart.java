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
package com.od.jtimeseries.ui.visualizer.chart;

import com.od.jtimeseries.ui.config.ChartRangeMode;
import com.od.jtimeseries.ui.config.DomainTimeSelection;
import com.od.jtimeseries.ui.timeseries.ChartingTimeSeries;
import com.od.jtimeseries.ui.visualizer.chart.creator.*;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeEventType;
import org.jfree.chart.event.ChartChangeListener;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 07-Jan-2009
 * Time: 14:28:23
 */
public class TimeSeriesChart extends JPanel {

    private static final String[] CHART_REFRESH_LISTEN_PROPERTIES = new String[] {
            ChartingTimeSeries.DISPLAY_NAME_PROPERTY,
            ChartingTimeSeries.COLOUR_PROPERTY
    };

    private String title;
    private List<ChartingTimeSeries> timeSeriesList = Collections.EMPTY_LIST;
    private ChartPanel chartPanel;
    private JPanel noChartsPanel = new JPanel();
    private ChartRangeMode chartRangeMode = ChartRangeMode.RangePerId;
    private Color chartBackgroundColor = Color.WHITE;
    private ChartType chartType = ChartType.DEFAULT_CHART_TYPE;
    private ChartDataFilter chartDataFilter = ChartDataFilter.NoFilter;

    private PropertyChangeListener refreshChartPropertyListener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            createAndSetChart();
        }
    };
    private JFreeChart chart;
    private boolean showLegend = true;
    private DomainTimeSelection domainStartTimeSelection = new DomainTimeSelection();
    private final ChartCreatorFactory chartCreatorFactory = new ChartCreatorFactory();

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

    public void setSeries(List<ChartingTimeSeries> newSelection) {
        removePropertyListener(this.timeSeriesList);

        //our own copy guaranteed to be RandomAccess list
        newSelection = new ArrayList<ChartingTimeSeries>(newSelection);
        addPropertyListener(newSelection);
        this.timeSeriesList = newSelection;
        if ( timeSeriesList.size() == 0) {
            if ( chartPanel != null) {
                remove(chartPanel);
                chartPanel = null;
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

    private void addPropertyListener(List<ChartingTimeSeries> newSelection) {
        for ( ChartingTimeSeries s : newSelection) {
            for ( String property : CHART_REFRESH_LISTEN_PROPERTIES) {
                s.addPropertyChangeListener(property, refreshChartPropertyListener);
            }
        }
    }

    private void removePropertyListener(List<ChartingTimeSeries> timeSeriesList) {
        for ( ChartingTimeSeries s : timeSeriesList) {
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

    public DomainTimeSelection getDomainStartTimeSelection() {
        return domainStartTimeSelection;
    }

    public ChartType getChartType() {
        return chartType;
    }

    public void setChartType(ChartType chartType) {
        if ( this.chartType != chartType) {
            this.chartType = chartType;
            createAndSetChart();
        }
    }

    public ChartDataFilter getChartDataFilter() {
        return chartDataFilter;
    }


    public void setChartDataFilter(ChartDataFilter f) {
        if ( this.chartDataFilter != f) {
            this.chartDataFilter = f;
            createAndSetChart();
        }
    }


    public void setDomainStartTimeSelection(DomainTimeSelection newValue) {
        if ( ! this.domainStartTimeSelection.equals(newValue) ) {
            this.domainStartTimeSelection = newValue;
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
        ChartCreatorParameters p = new ChartCreatorParameters(chartRangeMode, domainStartTimeSelection, chartBackgroundColor, timeSeriesList, showLegend, title, chartDataFilter);
        AbstractXYChartCreator chartCreator = chartCreatorFactory.getChartCreator(chartType, p);
        chart = chartCreator.createNewChart();
        addTitleChangeListener(chart);
        if ( chartPanel == null ) {
            chartPanel = new ChartPanel(chart);
        } else {
            chartPanel.setChart(chart);
        }
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
