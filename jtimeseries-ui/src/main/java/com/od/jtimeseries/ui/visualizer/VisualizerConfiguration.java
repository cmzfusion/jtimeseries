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
package com.od.jtimeseries.ui.visualizer;

import com.od.jtimeseries.ui.displaypattern.DisplayNamePattern;
import com.od.jtimeseries.ui.selector.table.ColumnSettings;
import com.od.jtimeseries.ui.timeseries.UiTimeSeriesConfig;
import com.od.jtimeseries.ui.util.JTimeSeriesUIConfig;
import com.od.jtimeseries.ui.visualizer.chart.ChartRangeMode;
import com.od.jtimeseries.ui.visualizer.chart.DomainTimeSelection;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 03-Jun-2009
 * Time: 08:10:22
 *
 * A bean to represent all visualizer configuration information
 */
public class VisualizerConfiguration implements JTimeSeriesUIConfig {

    private String chartsTitle;
    private List<DisplayNamePattern> displayNamePatterns;
    private boolean tableSelectorVisible;
    private List<UiTimeSeriesConfig> chartConfigs;
    private int dividorLocation;
    private boolean showLegendOnChart = true;
    private String chartRangeMode = ChartRangeMode.SingleRange.name(); //1.5.x bean persistence does not support enums, unfortunately!
    private DomainTimeSelection domainStartTimeSelection = new DomainTimeSelection();
    private Color chartBackgroundColor = Color.BLACK;
    private List<ColumnSettings> tableColumns = new ArrayList<ColumnSettings>();
    private Rectangle frameBounds;
    private boolean isIcon;

    public VisualizerConfiguration() {
    }

    public VisualizerConfiguration(String chartsTitle, List<DisplayNamePattern> displayNamePatterns, boolean tableSelectorVisible, List<UiTimeSeriesConfig> chartConfigs,
                                   ChartRangeMode chartRangeMode, DomainTimeSelection domainStartTimeSelection, int dividorLocation, boolean showLegendOnChart, Color chartBackgroundColor, List<ColumnSettings> columnSettings ) {
        this.chartsTitle = chartsTitle;
        this.displayNamePatterns = displayNamePatterns;
        this.tableSelectorVisible = tableSelectorVisible;
        this.chartConfigs = chartConfigs;
        this.domainStartTimeSelection = domainStartTimeSelection;
        this.chartRangeMode = chartRangeMode.name();
        this.dividorLocation = dividorLocation;
        this.showLegendOnChart = showLegendOnChart;
        this.chartBackgroundColor = chartBackgroundColor;
        this.tableColumns = columnSettings;
    }

    public String getChartsTitle() {
        return chartsTitle;
    }

    public void setChartsTitle(String chartsTitle) {
        this.chartsTitle = chartsTitle;
    }

    public List<DisplayNamePattern> getDisplayNamePatterns() {
        return displayNamePatterns;
    }

    public void setDisplayNamePatterns(List<DisplayNamePattern> displayNamePatterns) {
        this.displayNamePatterns = displayNamePatterns;
    }

    public DomainTimeSelection getDomainStartTimeSelection() {
        return domainStartTimeSelection;
    }

    public void setDomainStartTimeSelection(DomainTimeSelection domainStartTimeSelection) {
        this.domainStartTimeSelection = domainStartTimeSelection;
    }

    public boolean isTableSelectorVisible() {
        return tableSelectorVisible;
    }

    public void setTableSelectorVisible(boolean tableSelectorVisible) {
        this.tableSelectorVisible = tableSelectorVisible;
    }

    public List<UiTimeSeriesConfig> getChartConfigs() {
        return chartConfigs;
    }

    public void setChartConfigs(List<UiTimeSeriesConfig> chartConfigs) {
        this.chartConfigs = chartConfigs;
    }

    public String getChartRangeMode() {
        return chartRangeMode;
    }

    public void setChartRangeMode(String chartRangeMode) {
        this.chartRangeMode = chartRangeMode;
    }

    public int getDividorLocation() {
        return dividorLocation;
    }

    public void setDividorLocation(int dividorLocation) {
        this.dividorLocation = dividorLocation;
    }

    public boolean isShowLegendOnChart() {
        return showLegendOnChart;
    }

    public void setShowLegendOnChart(boolean showLegendOnChart) {
        this.showLegendOnChart = showLegendOnChart;
    }

    public Color getChartBackgroundColor() {
        return chartBackgroundColor;
    }

    public void setChartBackgroundColor(Color chartBackgroundColor) {
        this.chartBackgroundColor = chartBackgroundColor;
    }

    public List<ColumnSettings> getTableColumns() {
        return tableColumns;
    }

    public void setTableColumns(List<ColumnSettings> columnSettings) {
        this.tableColumns = columnSettings;
    }

    public Rectangle getFrameBounds() {
        return frameBounds;
    }

    public void setFrameBounds(Rectangle frameBounds) {
        this.frameBounds = frameBounds;
    }

    public boolean isIcon() {
        return isIcon;
    }

    public void setIsIcon(boolean frameExtendedState) {
        this.isIcon = frameExtendedState;
    }

    public static VisualizerConfiguration createVisualizerConfiguration(TimeSeriesVisualizer visualizer) {
        return new VisualizerConfiguration(
            visualizer.getChartsTitle(),
            visualizer.getDisplayNamePatterns(),
            visualizer.isTableSelectorVisible(),
            visualizer.getChartConfigs(),
            visualizer.getChartRangeMode(),
            visualizer.getDomainStartTimeSelection(),
            visualizer.getDividerLocation(),
            visualizer.isShowLegendOnChart(),
            visualizer.getChartBackgroundColor(),
            visualizer.getColumns()
        );
    }

    public static void setVisualizerConfiguration(TimeSeriesVisualizer visualizer, VisualizerConfiguration c) {
        visualizer.setChartsTitle(c.getChartsTitle());
        visualizer.setDisplayNamePatterns(c.getDisplayNamePatterns());
        visualizer.setTableSelectorVisible(c.isTableSelectorVisible());
        visualizer.addChartConfigs(c.getChartConfigs());
        visualizer.setChartRangeMode(ChartRangeMode.valueOf(c.getChartRangeMode()));
        visualizer.setDomainStartTimeSelection(c.getDomainStartTimeSelection());
        visualizer.setDividerLocation(c.getDividorLocation());
        visualizer.setShowLegendOnChart(c.isShowLegendOnChart());
        visualizer.setChartBackgroundColor(c.getChartBackgroundColor());

        //if there are no columns, assume we will use the default column set
        if ( c.getTableColumns().size() > 0) {
            visualizer.setColumns(c.getTableColumns());
        }
    }

    //the readResolve method allows us to handle migrations where we add fields which need to
    //be initialised - xstream sets the fields null even if a default is
    //assigned when the field is defined
    private Object readResolve() {
        if ( domainStartTimeSelection == null) {
            domainStartTimeSelection = new DomainTimeSelection();
        }
        return this;
    }

}
