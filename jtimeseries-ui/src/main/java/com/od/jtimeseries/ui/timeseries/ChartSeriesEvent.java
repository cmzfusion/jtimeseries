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
package com.od.jtimeseries.ui.timeseries;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 25-Nov-2010
 * Time: 18:12:22
 * To change this template use File | Settings | File Templates.
 */
public class ChartSeriesEvent {

    private UIPropertiesTimeSeries sourceSeries;
    private ChartSeriesEventType chartEvent;

    public ChartSeriesEvent(UIPropertiesTimeSeries sourceSeries, ChartSeriesEventType chartEvent) {
        this.sourceSeries = sourceSeries;
        this.chartEvent = chartEvent;
    }

    public UIPropertiesTimeSeries getSourceSeries() {
        return sourceSeries;
    }

    public ChartSeriesEventType getChartSeriesEventType() {
        return chartEvent;
    }

    public void setChartEvent(ChartSeriesEventType chartEvent) {
        this.chartEvent = chartEvent;
    }

    public static enum ChartSeriesEventType {
        SERIES_CHART_DISPLAYED,
        SERIES_CHART_HIDDEN,
        SERIES_CHART_DISPOSED
    }
}
