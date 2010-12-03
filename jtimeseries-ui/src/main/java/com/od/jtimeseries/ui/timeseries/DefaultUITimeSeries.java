package com.od.jtimeseries.ui.timeseries;

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

    private boolean selected;
    private volatile boolean stale = false;
    protected String displayName;
    private Date lastRefreshTime;
    protected URL timeSeriesUrl;
    protected volatile int refreshTimeSeconds;
    private Color color;

    public DefaultUITimeSeries(String id, String description) {
        super(id, description);
    }

    public boolean isStale() {
        return stale;
    }

    public void setStale(boolean stale) {
        boolean oldValue = this.stale;
        this.stale = stale;
        firePropertyChange(STALE_PROPERTY, oldValue, stale);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        boolean oldValue = this.selected;
        this.selected = selected;
        firePropertyChange(UIPropertiesTimeSeries.SELECTED_PROPERTY, oldValue, selected);
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
        firePropertyChange(UIPropertiesTimeSeries.DISPLAY_NAME_PROPERTY, oldValue, displayName);
    }

    public Date getLastRefreshTime() {
        return lastRefreshTime;
    }

    public void setLastRefreshTime(Date time) {
        Date oldValue = lastRefreshTime;
        this.lastRefreshTime = time;
        firePropertyChange(UIPropertiesTimeSeries.LAST_REFRESH_TIME_PROPERTY, oldValue, time);
    }

    public URL getTimeSeriesURL() {
        return timeSeriesUrl;
    }

    public void setTimeSeriesURL(URL url) {
        URL oldValue = this.timeSeriesUrl;
        timeSeriesUrl = url;
        firePropertyChange(URL_PROPERTY_NAME, oldValue, url);
    }

    public int getRefreshTimeSeconds() {
        return refreshTimeSeconds;
    }

    public void setRefreshTimeSeconds(int refreshTimeSeconds) {
        long oldValue = this.refreshTimeSeconds;
        this.refreshTimeSeconds = refreshTimeSeconds;
        firePropertyChange(UIPropertiesTimeSeries.REFRESH_TIME_SECONDS_PROPERTY, oldValue, refreshTimeSeconds);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        Color oldColor = this.color;
        this.color = color;
        firePropertyChange(UIPropertiesTimeSeries.COLOUR_PROPERTY, oldColor, color);
    }
}
