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

import com.od.jtimeseries.ui.config.UiTimeSeriesConfig;
import com.od.jtimeseries.ui.util.InternStringFieldOptimiser;
import com.od.swing.util.UIUtilities;

import java.awt.*;
import java.net.URL;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 29-Nov-2010
 * Time: 13:31:30
 */
public class DefaultUITimeSeries extends PropertyChangeTimeSeries implements UIPropertiesTimeSeries {

    private static InternStringFieldOptimiser<URL> urlOptimiser = new InternStringFieldOptimiser<URL>(URL.class, "host", "protocol", "authority");
    private static final ColorRotator colorRotator = new ColorRotator();
    private static final int MIN_REFRESH_TIME_SECONDS = 10;
    private static final int DEFAULT_REFRESH_FREQUENCY_SECONDS = 300;

    private volatile int refreshFrequencySeconds = DEFAULT_REFRESH_FREQUENCY_SECONDS;
    private volatile boolean selected;
    private volatile boolean stale;
    private volatile boolean loaded;
    private volatile boolean loading;
    private volatile boolean ticking;
    private volatile String displayName;
    private Date lastRefreshTime;
    private URL timeSeriesUrl;
    private Color color = colorRotator.getNextColor();
    private Date statsRefreshTime;

    public DefaultUITimeSeries(String id, String description) {
        //intern the id and descriptions since there is generally massive duplication with these
        //we'll accept the risk the out of permgen space error which could result from too many differing long descriptions
        //in exchange for the big reduction in overall memory usage
        super(id.intern(), description.intern());
    }

    public boolean isStale() {
        return stale;
    }

    public void setStale(boolean stale) {
        if ( ! UIUtilities.equals(stale, this.stale)) {
            boolean oldValue = this.stale;
            this.stale = stale;
            firePropertyChange(STALE_PROPERTY, oldValue, stale);
            fireNodeChanged(UIPropertiesTimeSeries.STALE_PROPERTY);
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        if ( ! UIUtilities.equals(selected, this.selected)) {
            boolean oldValue = this.selected;
            this.selected = selected;
            firePropertyChange(UIPropertiesTimeSeries.SELECTED_PROPERTY, oldValue, selected);
            fireNodeChanged(UIPropertiesTimeSeries.SELECTED_PROPERTY);
        }
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        if ( ! UIUtilities.equals(loaded, this.loaded)) {
            boolean oldValue = this.loaded;
            this.loaded = loaded;
            firePropertyChange(UIPropertiesTimeSeries.LOADED_PROPERTY, oldValue, loaded);
            fireNodeChanged(UIPropertiesTimeSeries.LOADED_PROPERTY);
        }
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        if ( ! UIUtilities.equals(loading, this.loading)) {
            boolean oldValue = this.loading;
            this.loading = loading;
            firePropertyChange(UIPropertiesTimeSeries.LOADING_PROPERTY, oldValue, loading);
            fireNodeChanged(UIPropertiesTimeSeries.LOADING_PROPERTY);
        }
    }

     public boolean isTicking() {
        return ticking;
    }

    public void setTicking(boolean ticking) {
        if ( ! UIUtilities.equals(ticking, this.ticking)) {
            boolean oldValue = this.ticking;
            this.ticking = ticking;
            firePropertyChange(UIPropertiesTimeSeries.TICKING_PROPERTY, oldValue, ticking);
            fireNodeChanged(UIPropertiesTimeSeries.TICKING_PROPERTY);
        }
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
            firePropertyChange(UIPropertiesTimeSeries.DISPLAY_NAME_PROPERTY, oldValue, displayName);
            fireNodeChanged(UIPropertiesTimeSeries.DISPLAY_NAME_PROPERTY);
        }
    }

    public Date getLastRefreshTime() {
        return lastRefreshTime;
    }

    public void setLastRefreshTime(Date time) {
        if ( ! UIUtilities.equals(time, lastRefreshTime)) {
            Date oldValue = lastRefreshTime;
            this.lastRefreshTime = time;
            firePropertyChange(UIPropertiesTimeSeries.LAST_REFRESH_TIME_PROPERTY, oldValue, time);
            fireNodeChanged(UIPropertiesTimeSeries.LAST_REFRESH_TIME_PROPERTY);
        }
    }

    public URL getTimeSeriesURL() {
        return timeSeriesUrl;
    }

    public void setTimeSeriesURL(URL url) {
        if ( ! UIUtilities.equals(url,  this.timeSeriesUrl)) {
            URL oldValue = this.timeSeriesUrl;
            timeSeriesUrl = url;
            urlOptimiser.optimise(timeSeriesUrl);
            firePropertyChange(URL_PROPERTY_NAME, oldValue, url);
            fireNodeChanged(URL_PROPERTY_NAME);
        }
    }

    public int getRefreshFrequencySeconds() {
        return refreshFrequencySeconds;
    }

    public void setRefreshFrequencySeconds(int refreshTimeSeconds) {
        if ( ! UIUtilities.equals(refreshTimeSeconds, this.refreshFrequencySeconds)) {
            long oldValue = this.refreshFrequencySeconds;
            this.refreshFrequencySeconds = Math.max(refreshTimeSeconds, MIN_REFRESH_TIME_SECONDS);
            firePropertyChange(UIPropertiesTimeSeries.REFRESH_FREQUENCY_PROPERTY, oldValue, this.refreshFrequencySeconds);
            fireNodeChanged(UIPropertiesTimeSeries.REFRESH_FREQUENCY_PROPERTY);
        }
    }

    public Date getStatsRefreshTime() {
        return statsRefreshTime;
    }

    public UiTimeSeriesConfig getConfig() {
        return getConfig(this);
    }

    public static UiTimeSeriesConfig getConfig(UIPropertiesTimeSeries s) {
        return new UiTimeSeriesConfig(
            s.getParentPath(),
            s.getId(),
            s.getDescription(),
            s.getTimeSeriesURL().toExternalForm(),
            s.getRefreshFrequencySeconds(),
            s.isSelected(),
            s.getDisplayName(),
            s.getColor()
        );
    }

    public void setStatsRefreshTime(Date statsRefreshTime) {
        if ( ! UIUtilities.equals(statsRefreshTime, this.statsRefreshTime)) {
            Date oldValue = this.statsRefreshTime;
            this.statsRefreshTime = statsRefreshTime;
            firePropertyChange(UIPropertiesTimeSeries.STATS_REFRESH_TIME_PROPERTY, oldValue, this.statsRefreshTime);
            fireNodeChanged(UIPropertiesTimeSeries.STATS_REFRESH_TIME_PROPERTY);
        }
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        if ( ! UIUtilities.equals(color, this.color)) {
            Color oldColor = this.color;
            this.color = color;
            firePropertyChange(UIPropertiesTimeSeries.COLOUR_PROPERTY, oldColor, color);
            fireNodeChanged(UIPropertiesTimeSeries.COLOUR_PROPERTY);
        }
    }
}
