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

import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.ui.config.UiTimeSeriesConfig;
import com.od.jtimeseries.ui.util.Displayable;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 27-Nov-2010
 * Time: 23:57:31
 *
 *  At present all UI times series properties must
 *  be set on the swing event thread since jide
 *  bean table model expects this
 */
public interface UIPropertiesTimeSeries extends IdentifiableTimeSeries, Displayable {

    public static final String SELECTED_PROPERTY = "selected";
    public static final String STALE_PROPERTY = "stale";
    public static final String DISPLAY_NAME_PROPERTY = "displayName";
    public static final String COLOUR_PROPERTY = "color";
    public static final String LAST_REFRESH_TIME_PROPERTY = "lastRefreshTime";
    public static final String STATS_REFRESH_TIME_PROPERTY = "statsRefreshTime";
    public static final String REFRESH_FREQUENCY_PROPERTY = "refreshFrequencySeconds";
    public static final String URL_PROPERTY_NAME = "timeSeriesURL";
    public static final String LOADED_PROPERTY = "loaded";
    public static final String LOADING_PROPERTY = "loaded";
    public static final String TICKING_PROPERTY = "ticking";

    boolean isSelected();

    void setSelected(boolean selected);

    /**
    *  @return true, if queries to load the data for this series failed
    */
    boolean isStale();

    void setStale(boolean stale);

    /**
     * @return true, if at least one query for series data has completed successfully
     */
    boolean isLoaded();

    void setLoaded(boolean loaded);

    /**
     * @return true if the query to load or refresh timeseries data is currently running
     */
    boolean isLoading();

    void setLoading(boolean loading);
    /**
     * @return true, if it this series currently appears to be getting updates
     */
    boolean isTicking();

    void setTicking(boolean ticking);

    void addPropertyChangeListener(String selectedProperty, PropertyChangeListener selectionPropertyListener);

    void removePropertyChangeListener(PropertyChangeListener selectionPropertyListener);

    String getDisplayName();

    void setDisplayName(String displayName);

    Color getColor();

    void setColor(Color color);

    URL getTimeSeriesURL();

    void setTimeSeriesURL(URL url);

    int getRefreshFrequencySeconds();

    void setLastRefreshTime(Date time);

    Date getLastRefreshTime();

    void setRefreshFrequencySeconds(int refreshTimeSeconds);

    void setStatsRefreshTime(Date time);

    Date getStatsRefreshTime();

    UiTimeSeriesConfig getConfig();
}
