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

import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.identifiable.IdentifiableTreeEvent;
import com.od.jtimeseries.util.identifiable.IdentifiableTreeListener;

import java.net.URL;
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
public class ChartingTimeSeries extends AdjustableStartTimeProxySeries implements UIPropertiesTimeSeries {

    private static ColorRotator colorRotator = new ColorRotator();

    private boolean selected;
    private String displayName;
    private Color color = colorRotator.getNextColor();
    private RemoteHttpTimeSeries wrappedSeries;

    public ChartingTimeSeries(RemoteHttpTimeSeries wrappedSeries) {
        super(wrappedSeries);
        this.wrappedSeries = wrappedSeries;
        addPropertyListeners();
    }

    public ChartingTimeSeries(RemoteHttpTimeSeries wrappedSeries, UiTimeSeriesConfig c) {
        super(wrappedSeries);
        this.wrappedSeries = wrappedSeries;
        setDisplayName(c.getDisplayName());
        setColor(c.getColor());
        setSelected(c.isSelected());
        addPropertyListeners();
    }

    private void addPropertyListeners() {
        //listen to and propagate the change events from wrapped series
        addEventPropagatingListeners(URL_PROPERTY_NAME);
        addEventPropagatingListeners(LAST_REFRESH_TIME_PROPERTY);
        addEventPropagatingListeners(REFRESH_TIME_SECONDS_PROPERTY);
        addEventPropagatingListeners(STALE_PROPERTY);
    }

    private void addEventPropagatingListeners(String propertyName) {
        //propagate events from the wrapped series
        wrappedSeries.addPropertyChangeListener(propertyName, getProxyingPropertyListener(propertyName));
        wrappedSeries.addTreeListener(new WrappedSeriesTreeListener());
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
        firePropertyChange(UIPropertiesTimeSeries.DISPLAY_NAME_PROPERTY, oldValue, this.displayName);
    }


    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        boolean oldValue = this.selected;
        this.selected = selected;
        wrappedSeries.chartSeriesChanged(
            new ChartSeriesEvent(this,
                selected ?
                ChartSeriesEvent.ChartSeriesEventType.SERIES_CHART_DISPLAYED :
                ChartSeriesEvent.ChartSeriesEventType.SERIES_CHART_HIDDEN
            )
        );
        firePropertyChange(SELECTED_PROPERTY, oldValue, this.selected);
        fireNodeChanged(SELECTED_PROPERTY);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        Color oldValue = this.color;
        this.color = color;
        firePropertyChange(UIPropertiesTimeSeries.COLOUR_PROPERTY, oldValue, color);
    }

    public URL getTimeSeriesURL() {
        return wrappedSeries.getTimeSeriesURL();
    }

    public void setTimeSeriesURL(URL url) {
        wrappedSeries.setTimeSeriesURL(url);
    }

    public boolean isStale() {
        return wrappedSeries.isStale();
    }

    public void setStale(boolean seriesStale) {
        wrappedSeries.setStale(seriesStale);
    }

    public int getRefreshTimeSeconds() {
        return wrappedSeries.getRefreshTimeSeconds();
    }

    public void setLastRefreshTime(Date time) {
        wrappedSeries.setLastRefreshTime(time);
    }

    public Date getLastRefreshTime() {
        return wrappedSeries.getLastRefreshTime();
    }

    public void setRefreshTimeSeconds(int refreshTimeSeconds) {
        wrappedSeries.setRefreshTimeSeconds(refreshTimeSeconds);
    }

    private class WrappedSeriesTreeListener implements IdentifiableTreeListener {

        public void nodeChanged(Identifiable node, Object changeDescription) {
            fireNodeChanged(changeDescription);
        }

        public void descendantChanged(IdentifiableTreeEvent contextTreeEvent) {
        }

        public void descendantAdded(IdentifiableTreeEvent contextTreeEvent) {
        }

        public void descendantRemoved(IdentifiableTreeEvent contextTreeEvent) {
        }
    }
}
