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

import com.od.jtimeseries.context.ContextProperties;
import com.od.jtimeseries.ui.config.UiTimeSeriesConfig;
import com.od.jtimeseries.ui.util.Disposable;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.identifiable.IdentifiableTreeEvent;
import com.od.jtimeseries.util.identifiable.IdentifiableTreeListener;
import com.od.swing.util.UIUtilities;
import com.od.swing.weakreferencelistener.WeakReferenceListener;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 12-Jan-2009
 * Time: 09:43:39
 *
 * A time series used by the visualizer
 *
 * Delegates most property handling to the wrapped timeseries, but holds local state for those which are visualizer-local properties
 */
public class ChartingTimeSeries extends ProxyingPropertyChangeTimeseries implements UIPropertiesTimeSeries, Disposable {

    private static ColorRotator colorRotator = new ColorRotator();

    private final RemoteHttpTimeSeries wrappedSeries;
    private volatile boolean selected;
    private volatile String displayName;
    private Color color = colorRotator.getNextColor();
    public WrappedSeriesTreeListener wrappedSeriesTreeListener;
    public PropertyChangeListener proxyingPropertyListener;

    public ChartingTimeSeries(RemoteHttpTimeSeries wrappedSeries, UiTimeSeriesConfig c) {
        super(wrappedSeries);
        this.wrappedSeries = wrappedSeries;
        setDisplayName(c.getDisplayName());
        setColor(c.getColor());
        setSelected(c.isSelected());
        addListenersToWrappedSeries();
    }

    //all listeners are weak reference listeners
    //we want the charting series to remain available for gc, so don't want the listeners to
    //keep strong references
    private void addListenersToWrappedSeries() {
        //listen to and propagate the change events from wrapped series
        proxyingPropertyListener = getProxyingPropertyListener();
        addEventPropagatingListeners(URL_PROPERTY_NAME);
        addEventPropagatingListeners(LAST_REFRESH_TIME_PROPERTY);
        addEventPropagatingListeners(REFRESH_FREQUENCY_PROPERTY);
        addEventPropagatingListeners(STALE_PROPERTY);
        addEventPropagatingListeners(STATS_REFRESH_TIME_PROPERTY);
        addEventPropagatingListeners(LOADED_PROPERTY);
        addEventPropagatingListeners(LOADING_PROPERTY);
        addEventPropagatingListeners(TICKING_PROPERTY);

        wrappedSeriesTreeListener = new WrappedSeriesTreeListener();
        WeakReferenceListener l = new WeakReferenceListener(wrappedSeriesTreeListener);
        l.addListenerTo(wrappedSeries);
    }

    private void addEventPropagatingListeners(String propertyName) {
        //propagate events from the wrapped series
        WeakReferenceListener p = new WeakReferenceListener(propertyName, proxyingPropertyListener);
        p.addListenerTo(wrappedSeries);
    }

    public String getDisplayName() {
        if ( displayName == null ) {
            setDisplayName(getId());
        }
        return displayName;
    }

    public void setDisplayName(String displayName) {
        if ( ! UIUtilities.equals(displayName, this.displayName)) {
            String oldValue = this.displayName;
            this.displayName = displayName;
            firePropertyChange(UIPropertiesTimeSeries.DISPLAY_NAME_PROPERTY, oldValue, this.displayName);
            fireNodeChanged(UIPropertiesTimeSeries.DISPLAY_NAME_PROPERTY);
        }
    }


    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        if (! UIUtilities.equals(selected, this.selected)) {
            if ( selected) {
                //clear stale flag, user may be unselecting/reselecting instead of triggering reconnect
                setStale(false);
            }
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
    }

    //support summary stats properties from wrapped series
    public Properties getProperties_Locked() {
        Properties properties = super.getProperties_Locked();
        synchronized (wrappedSeries) {
            ContextProperties.addStatsProperties(
                wrappedSeries.getProperties(),
                properties
            );
        }
        return properties;
    }

    //support summary stats properties from wrapped series
    public String getProperty_Locked(String propertyName) {
        String result = null;
        if ( ContextProperties.isSummaryStatsProperty(propertyName)) {
            result = wrappedSeries.getProperty(propertyName);
        }
        return result;
    }

    public Date getStatsRefreshTime() {
        return wrappedSeries.getStatsRefreshTime();
    }

    public UiTimeSeriesConfig getConfig() {
        return DefaultUITimeSeries.getConfig(this);
    }

    public void setStatsRefreshTime(Date statsRefreshTime) {
        wrappedSeries.setStatsRefreshTime(statsRefreshTime);
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

    public boolean isLoaded() {
        return wrappedSeries.isLoaded();
    }

    public void setLoaded(boolean loaded) {
        wrappedSeries.setLoaded(loaded);
    }

    public boolean isLoading() {
        return wrappedSeries.isLoading();
    }

    public void setLoading(boolean loading) {
        wrappedSeries.setLoading(loading);
    }

    public boolean isTicking() {
        return wrappedSeries.isTicking();
    }

    public void setTicking(boolean ticking) {
        wrappedSeries.setTicking(ticking);
    }

    public int getRefreshFrequencySeconds() {
        return wrappedSeries.getRefreshFrequencySeconds();
    }

    public void setLastRefreshTime(Date time) {
        wrappedSeries.setLastRefreshTime(time);
    }

    public Date getLastRefreshTime() {
        return wrappedSeries.getLastRefreshTime();
    }

    public void setRefreshFrequencySeconds(int refreshTimeSeconds) {
        wrappedSeries.setRefreshFrequencySeconds(refreshTimeSeconds);
    }

    public void dispose() {
        if ( selected ) {
            wrappedSeries.chartSeriesChanged(new ChartSeriesEvent(
                this,
                ChartSeriesEvent.ChartSeriesEventType.SERIES_CHART_DISPOSED
            ));
        }
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
