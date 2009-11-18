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

import com.od.jtimeseries.ui.displaypattern.DisplayNamePattern;
import com.od.jtimeseries.ui.timeseries.RemoteChartingTimeSeriesConfig;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 03-Jun-2009
 * Time: 08:10:22
 *
 * A bean to represent all visualizer configuration information
 */
public class VisualizerConfiguration {

    private String chartsTitle;
    private List<DisplayNamePattern> displayNamePatterns;
    private boolean tableSelectorVisible;
    private List<RemoteChartingTimeSeriesConfig> chartConfigs;
    private boolean multipleRangeChart;
    private int dividorLocation;

    public VisualizerConfiguration() {
    }

    public VisualizerConfiguration(String chartsTitle, List<DisplayNamePattern> displayNamePatterns, boolean tableSelectorVisible, List<RemoteChartingTimeSeriesConfig> chartConfigs, boolean isMultipleRangeChart, int dividorLocation ) {
        this.chartsTitle = chartsTitle;
        this.displayNamePatterns = displayNamePatterns;
        this.tableSelectorVisible = tableSelectorVisible;
        this.chartConfigs = chartConfigs;
        this.multipleRangeChart = isMultipleRangeChart;
        this.dividorLocation = dividorLocation;
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

    public boolean isTableSelectorVisible() {
        return tableSelectorVisible;
    }

    public void setTableSelectorVisible(boolean tableSelectorVisible) {
        this.tableSelectorVisible = tableSelectorVisible;
    }

    public List<RemoteChartingTimeSeriesConfig> getChartConfigs() {
        return chartConfigs;
    }

    public void setChartConfigs(List<RemoteChartingTimeSeriesConfig> chartConfigs) {
        this.chartConfigs = chartConfigs;
    }

    public boolean isMultipleRangeChart() {
        return multipleRangeChart;
    }

    public void setMultipleRangeChart(boolean multipleRangeChart) {
        this.multipleRangeChart = multipleRangeChart;
    }

    public int getDividorLocation() {
        return dividorLocation;
    }

    public void setDividorLocation(int dividorLocation) {
        this.dividorLocation = dividorLocation;
    }
}
