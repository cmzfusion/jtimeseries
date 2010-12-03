package com.od.jtimeseries.ui.timeseries;

import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 27-Nov-2010
 * Time: 23:57:31
 * To change this template use File | Settings | File Templates.
 */
public interface UIPropertiesTimeSeries extends IdentifiableTimeSeries {

    public static final String SELECTED_PROPERTY = "selected";
    public static final String STALE_PROPERTY = "stale";
    public static final String DISPLAY_NAME_PROPERTY = "displayName";
    public static final String COLOUR_PROPERTY = "color";
    public static final String LAST_REFRESH_TIME_PROPERTY = "lastRefreshTime";
    public static final String REFRESH_TIME_SECONDS_PROPERTY = "refreshTimeSeconds";
    public static final String URL_PROPERTY_NAME = "timeSeriesURL";

    boolean isSelected();

    void setSelected(boolean selected);

    boolean isStale();

    void setStale(boolean stale);

    void addPropertyChangeListener(String selectedProperty, PropertyChangeListener selectionPropertyListener);

    void removePropertyChangeListener(PropertyChangeListener selectionPropertyListener);

    String getDisplayName();

    void setDisplayName(String displayName);

    Color getColor();

    void setColor(Color color);

    URL getTimeSeriesURL();

    void setTimeSeriesURL(URL url);

    int getRefreshTimeSeconds();

    void setLastRefreshTime(Date time);

    Date getLastRefreshTime();

    void setRefreshTimeSeconds(int refreshTimeSeconds);
}
