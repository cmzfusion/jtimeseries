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
package com.od.jtimeseries.ui.timeseries;

import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.util.PathParser;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.util.logging.LogMethods;

import java.util.List;
import java.util.Date;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 12-Jan-2009
 * Time: 09:43:39
 *
 * A time series used by the visualizer
 */
public class ChartingTimeSeries extends DelegatingPropertyChangeTimeseries implements IdentifiableTimeSeries {

    private static final LogMethods logMethods = LogUtils.getLogMethods(ChartingTimeSeries.class);

    public static final String SELECTED_PROPERTY = "selected";
    public static final String SERIES_STALE_PROPERTY = "seriesStale";
    public static final String DISPLAY_NAME_PROPERTY = "displayName";
    public static final String COLOUR_PROPERTY = "color";

    private static ColorRotator colorRotator = new ColorRotator();

    private boolean selected;
    private List<String> pathElements;
    private String displayName;
    private Color color = colorRotator.getNextColor();
    private volatile boolean seriesStale = false;
    private Date lastRefreshTime;
    private ChartSeriesListener chartSeriesListener;

    public ChartingTimeSeries(IdentifiableTimeSeries wrappedSeries, ChartSeriesListener chartSeriesListener) {
        super(wrappedSeries);
        this.chartSeriesListener = chartSeriesListener;
    }

    public String getDisplayName() {
        if ( displayName == null ) {
            setDisplayName(getPath());
        }
        return displayName;
    }

    public void setDisplayName(String displayName) {
        String oldValue = this.displayName;
        this.displayName = displayName;
        firePropertyChange(DISPLAY_NAME_PROPERTY, oldValue, this.displayName);
    }

    public boolean isSeriesStale() {
        return seriesStale;
    }

    public void setSeriesStale(boolean seriesStale) {
        boolean oldValue = this.seriesStale;
        this.seriesStale = seriesStale;
        firePropertyChange(SERIES_STALE_PROPERTY, oldValue, this.seriesStale);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        boolean oldValue = this.selected;
        this.selected = selected;
        chartSeriesListener.chartSeriesChanged(
            new ChartSeriesEvent(this,
                selected ?
                ChartSeriesEvent.ChartSeriesEventType.SERIES_CHART_DISPLAYED :
                ChartSeriesEvent.ChartSeriesEventType.SERIES_CHART_HIDDEN
            )
        );
        firePropertyChange(SELECTED_PROPERTY, oldValue, this.selected);
    }

    public Date getLastRefreshTime() {
        return lastRefreshTime;
    }

    public void setLastRefreshTime(Date time) {
        Date oldValue = lastRefreshTime;
        this.lastRefreshTime = time;
        firePropertyChange("lastRefreshTime", oldValue, time);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        Color oldValue = this.color;
        this.color = color;
        firePropertyChange(COLOUR_PROPERTY, oldValue, color);
    }

    public List<String> getPathElements() {
        if ( pathElements == null) {
            pathElements = PathParser.splitPath(getParentPath());
        }
        return pathElements;
    }
}
