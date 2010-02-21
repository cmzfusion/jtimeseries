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

import com.od.jtimeseries.net.httpd.AttributeName;
import com.od.jtimeseries.timeseries.impl.DefaultIdentifiableTimeSeries;
import com.od.jtimeseries.ui.query.DownloadRemoteTimeSeriesDataQuery;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.time.Time;
import com.od.jtimeseries.util.time.TimePeriod;
import swingcommand.BackgroundTask;
import swingcommand.SwingCommand;
import swingcommand.Task;
import swingcommand.TaskListenerAdapter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 12-Jan-2009
 * Time: 09:43:39
 *
 * A time series used by the ui, which loads its data from a remote server
 *
 * It is assumed that the extra methods this class provides over and above the standard TimeSeries methods are
 * to be called on the AWT only, and so there is no need for extra synchronization here. Furthermore property change
 * events should only be fired on the AWT
 */
public class RemoteChartingTimeSeries extends DefaultIdentifiableTimeSeries {

    private static ScheduledExecutorService refreshExecutor = Executors.newSingleThreadScheduledExecutor();
    private static int startOfDayOffsetMinutes = 0;
    private static final int ONE_DAY_MILLIS = 24*60*60*1000;

    private int MIN_REFRESH_TIME_SECONDS = 10;
    private URL timeSeriesUrl;
    private LogMethods logMethods = LogUtils.getLogMethods(RemoteChartingTimeSeries.class);
    private boolean selected;
    private List<String> pathElements;
    private long startOfDay = calcStartOfDay();
    private volatile int maxDaysHistory;
    private volatile ScheduledFuture refreshTask;
    private volatile int refreshTimeSeconds;
    private String displayName;
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private RefreshDataCommand refreshDataCommand = new RefreshDataCommand();
    private boolean neverRefresh;
    private Date lastRefreshTime;

    //a series starts 'connected' and remains connected until a set number of consecutive download failures have occurred
    private volatile int errorCount;
    private static final int MAX_ERRORS_BEFORE_DISCONNECT = 4;
    private volatile boolean connected = true;

    public RemoteChartingTimeSeries(RemoteChartingTimeSeriesConfig config) throws MalformedURLException {
        this(config.getId(), config.getDescription(), new URL(config.getTimeSeriesUrl()), Time.seconds(config.getRefreshTimeSeconds()), config.getMaxDaysHistory());
        setSelected(config.isSelected());
        this.displayName = config.getDisplayName();
    }

    public RemoteChartingTimeSeries(String id, String description, URL timeSeriesUrl, TimePeriod refreshTime, int maxDaysHistory) {
        super(id, description);
        this.timeSeriesUrl = timeSeriesUrl;
        this.maxDaysHistory = maxDaysHistory;
        this.refreshTimeSeconds = Math.max((int)(refreshTime.getLengthInMillis() / 1000), 10);
    }

    public URL getURL() {
        return timeSeriesUrl;
    }

    private long calcStartOfDay() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND,0);
        return c.getTime().getTime() + (startOfDayOffsetMinutes * 60 * 1000);
    }

    public List<String> getPathElements() {
        if ( pathElements == null) {
            pathElements = splitPath(getParentPath());
        }
        return pathElements;
    }

    public static final String DISPLAY_NAME_PROPERTY = "displayName";
    public String getDisplayName() {
        if ( displayName == null ) {
            setDisplayName(getPath());
        }
        return displayName;
    }

    public void setDisplayName(String displayName) {
        String oldValue = this.displayName;
        this.displayName = displayName;
        propertyChangeSupport.firePropertyChange("displayName", oldValue, this.displayName);
    }

    public static final String CONNECTED_PROPERTY = "connected";
    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        boolean oldValue = this.connected;
        this.connected = connected;
        if (connected) {
            errorCount = 0;
        }
        propertyChangeSupport.firePropertyChange("connected", oldValue, this.connected);
    }

    public static final String SELECTED_PROPERTY = "selected";
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        boolean oldValue = this.selected;
        this.selected = selected;
        scheduleRefreshTask();
        propertyChangeSupport.firePropertyChange("selected", oldValue, this.selected);
    }

    public int getMaxDaysHistory() {
        return maxDaysHistory;
    }

    public void setMaxDaysHistory(int maxDaysHistory) {
        int oldValue = this.maxDaysHistory;
        this.maxDaysHistory = maxDaysHistory;
        clear();
        scheduleRefreshTask();
        propertyChangeSupport.firePropertyChange("maxDaysHistory", oldValue, this.maxDaysHistory);
    }

    public int getRefreshTimeSeconds() {
        return refreshTimeSeconds;
    }

    public void setRefreshTimeSeconds(int refreshTimeSeconds) {
        long oldValue = this.refreshTimeSeconds;
        this.refreshTimeSeconds = Math.max(refreshTimeSeconds, MIN_REFRESH_TIME_SECONDS);
        logMethods.logInfo("Changing refresh time for series " + getId() + " to " + refreshTimeSeconds + " seconds");
        scheduleRefreshTask();
        propertyChangeSupport.firePropertyChange("refreshTimeSeconds", oldValue, this.refreshTimeSeconds);
    }

    public Date getLastRefreshTime() {
        return lastRefreshTime;
    }

    public void setLastRefreshTime(Date time) {
        Date oldValue = lastRefreshTime;
        this.lastRefreshTime = time;
        propertyChangeSupport.firePropertyChange("lastRefreshTime", oldValue, time);
    }

    /**
     * Set this timeseries so that the load data task will never run
     * this is somtimes useful when using the series as a placeholder to represent a timeseries on a remote server only
     */
    public void setNeverLoadRemoteSeriesData(boolean neverRefresh) {
        this.neverRefresh = neverRefresh;
    }

    //Cancel any existing task and schedule a new one if series selected
    private void scheduleRefreshTask() {
        if ( refreshTask != null ) {
            refreshTask.cancel(false);
        }

        Runnable runCommandTask = new Runnable() {
            public void run() {
                refreshDataCommand.execute();
            }
        };

        if ( isSelected() && ! neverRefresh) {
            refreshTask = refreshExecutor.scheduleAtFixedRate(
                    runCommandTask, 0, (long) this.refreshTimeSeconds, TimeUnit.SECONDS
            );
        }
    }

    public RemoteChartingTimeSeriesConfig getConfig() {
        return new RemoteChartingTimeSeriesConfig(
            getParentPath(), getId(), getDescription(), timeSeriesUrl.toExternalForm(), refreshTimeSeconds, maxDaysHistory, selected, displayName
        );
    }

    private class RefreshDataCommand extends SwingCommand {

        public RefreshDataCommand() {

            //if we exceed the error count when running the load, set the series to stale
            //the user will need to re-enable it to start the load off again
            addTaskListener(new DisconnectingTaskListener());
        }

        protected Task createTask() {
            return new BackgroundTask() {
                protected void doInBackground() throws Exception {
                    if ( isConnected()) {
                        URL urlForQuery = getUrlWithTimestamp();
                        new DownloadRemoteTimeSeriesDataQuery(RemoteChartingTimeSeries.this, urlForQuery).runQuery();
                    }
                }

                private URL getUrlWithTimestamp() throws MalformedURLException {
                    return new URL(
                        timeSeriesUrl + "?" + AttributeName.moreRecentThanTimestamp.name() + "=" + getEarliestItemToFetch()
                    );
                }

                //the later of the last current timepoint or the earliest point calculated using max days history
                private long getEarliestItemToFetch() {
                    long time = startOfDay;
                    time -= (ONE_DAY_MILLIS * (maxDaysHistory - 1));
                    return Math.max(getLatestTimestamp(), time);
                }

                protected void doInEventThread() throws Exception {
                    errorCount = 0;
                }
            };
        }
    }

    public static void setStartOfDayOffsetMinutes(int startOfDayOffsetMinutes) {
        RemoteChartingTimeSeries.startOfDayOffsetMinutes = startOfDayOffsetMinutes;
    }

    //perform disconnection if task failed too many times
    private class DisconnectingTaskListener extends TaskListenerAdapter {

        public void error(Task task, Throwable error) {
            errorCount++;
            if ( errorCount >= MAX_ERRORS_BEFORE_DISCONNECT) {
                setConnected(false);
            }
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
    }

    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        return propertyChangeSupport.getPropertyChangeListeners(propertyName);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return propertyChangeSupport.getPropertyChangeListeners();
    }
}
