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

import com.od.jtimeseries.net.httpd.HttpParameterName;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.time.Time;
import com.od.jtimeseries.util.time.TimePeriod;
import swingcommand.BackgroundTask;
import swingcommand.SwingCommand;
import swingcommand.Task;
import swingcommand.TaskListenerAdapter;

import java.net.MalformedURLException;
import java.net.URL;
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
public class RemoteHttpTimeSeries extends PropertyChangeTimeSeries implements ChartSeriesListener, UIPropertiesTimeSeries {

    private static final LogMethods logMethods = LogUtils.getLogMethods(ChartingTimeSeries.class);

    public static final String DISPLAY_NAME_PROPERTY = "displayName";
    public static final String LAST_REFRESH_TIME_PROPERTY = "lastRefreshTime";
    public static final String REFRESH_TIME_SECONDS_PROPERTY = "refreshTimeSeconds";
    public static final String URL_PROPERTY_NAME = "timeSeriesURL";

    private static ScheduledExecutorService refreshExecutor = Executors.newSingleThreadScheduledExecutor();
    private static final int STATS_ONLY_REFRESH_TIME_SECONDS = 60 * 30;
    private static final int MIN_REFRESH_TIME_SECONDS = 10;

    private URL timeSeriesUrl;
    private volatile ScheduledFuture refreshTask;
    private volatile int refreshTimeSeconds;
    private String displayName;
    private RefreshDataCommand refreshDataCommand = new RefreshDataCommand();
    private Date lastRefreshTime;
    private volatile int displayedChartCount;
    private boolean selected;

    //a series starts not 'stale' and remains not stale until a set number of consecutive download failures have occurred
    private volatile int errorCount;
    private static final int MAX_ERRORS_BEFORE_DISCONNECT = 4;
    private volatile boolean stale = false;

    private RemoteHttpTimeSeries(RemoteChartingTimeSeriesConfig config) throws MalformedURLException {
        this(config.getId(), config.getDescription(), new URL(config.getTimeSeriesUrl()), Time.seconds(config.getRefreshTimeSeconds()));
        this.displayName = config.getDisplayName();
    }

    private RemoteHttpTimeSeries(String id, String description, URL timeSeriesUrl, TimePeriod refreshTime) {
        super(id, description);
        this.timeSeriesUrl = timeSeriesUrl;
        this.refreshTimeSeconds = Math.max((int)(refreshTime.getLengthInMillis() / 1000), 10);
    }

    public URL getTimeSeriesURL() {
        return timeSeriesUrl;
    }

    public void setTimeSeriesURL(URL url) {
        URL oldValue = this.timeSeriesUrl;
        timeSeriesUrl = url;
        firePropertyChange(URL_PROPERTY_NAME, oldValue, url);
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
        firePropertyChange(DISPLAY_NAME_PROPERTY, oldValue, displayName);
    }

    public boolean isStale() {
        return stale;
    }

    public void setStale(boolean stale) {
        boolean oldValue = this.stale;
        this.stale = stale;
        if (!stale) {
            errorCount = 0;
        }
        firePropertyChange(STALE_PROPERTY, oldValue, stale);
    }

    public int getRefreshTimeSeconds() {
        return refreshTimeSeconds;
    }

    public void setRefreshTimeSeconds(int refreshTimeSeconds) {
        long oldValue = this.refreshTimeSeconds;
        this.refreshTimeSeconds = Math.max(refreshTimeSeconds, MIN_REFRESH_TIME_SECONDS);
        logMethods.logInfo("Changing refresh time for series " + getId() + " to " + refreshTimeSeconds + " seconds");
        scheduleRefresh();
        firePropertyChange(REFRESH_TIME_SECONDS_PROPERTY, oldValue, refreshTimeSeconds);
    }

    public Date getLastRefreshTime() {
        return lastRefreshTime;
    }

    public void setLastRefreshTime(Date time) {
        Date oldValue = lastRefreshTime;
        this.lastRefreshTime = time;
        firePropertyChange(LAST_REFRESH_TIME_PROPERTY, oldValue, time);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        boolean oldValue = this.selected;
        this.selected = selected;
        firePropertyChange(UIPropertiesTimeSeries.SELECTED_PROPERTY, oldValue, selected);
    }

    //Cancel any existing task and schedule a new one if series selected
    private void scheduleRefresh() {
        if ( refreshTask != null ) {
            refreshTask.cancel(false);
        }

        Runnable runCommandTask = new Runnable() {
            public void run() {
                refreshDataCommand.execute();
            }
        };

        //if the user has not selected to chart the series, we only refresh the stats, and much less frequently
        int refreshTime = (displayedChartCount > 0) ? this.refreshTimeSeconds : STATS_ONLY_REFRESH_TIME_SECONDS;
        refreshTask = refreshExecutor.scheduleAtFixedRate(
            runCommandTask, 0, refreshTime, TimeUnit.SECONDS
        );
    }

    public void chartSeriesChanged(ChartSeriesEvent e) {
        switch(e.getChartSeriesEventType()) {
            case SERIES_CHART_DISPLAYED:
                displayedChartCount++;
                if ( displayedChartCount == 1) {
                    scheduleRefresh(); //may want to increase refresh rate
                }
                break;
            case SERIES_CHART_HIDDEN:
                displayedChartCount = Math.max(0, displayedChartCount - 1);
                break;
            default:
        }
    }


    private class RefreshDataCommand extends SwingCommand {

        public RefreshDataCommand() {

            //if we exceed the error count when running the load, set the series to stale
            //the user will need to re-enable it to start the load off again
            addTaskListener(new SetStaleOnErrorListener());
        }

        protected Task createTask() {
            return new BackgroundTask() {
                protected void doInBackground() throws Exception {
                    if ( ! isStale() ) {
                        URL urlForQuery = getUrlWithTimestamp();
                        new DownloadRemoteTimeSeriesDataQuery(RemoteHttpTimeSeries.this, urlForQuery).runQuery();
                    }
                }

                private URL getUrlWithTimestamp() throws MalformedURLException {
                    return new URL(
                        timeSeriesUrl + "?" + HttpParameterName.moreRecentThanTimestamp.name() + "=" + getEarliestItemToFetch() + "&" + HttpParameterName.statsOnly + "=" + (displayedChartCount == 0)
                    );
                }

                //the later of the last current timepoint or the earliest point calculated using max days history
                private long getEarliestItemToFetch() {
                    return getLatestTimestamp();
                }

                protected void doInEventThread() throws Exception {
                    errorCount = 0;
                    setLastRefreshTime(new Date());
                }
            };
        }
    }

    //perform disconnection if task failed too many times
    private class SetStaleOnErrorListener extends TaskListenerAdapter {

        public void error(Task task, Throwable error) {
            errorCount++;
            if ( errorCount >= MAX_ERRORS_BEFORE_DISCONNECT) {
                setStale(true);
            }
        }
    }

    //Factory methods to construct, ensuring refresh is also scheduled after construction

    public static RemoteHttpTimeSeries createRemoteHttpTimeSeries(String id, String description, URL timeSeriesUrl, TimePeriod refreshTime) {
        RemoteHttpTimeSeries r = new RemoteHttpTimeSeries(id, description, timeSeriesUrl, refreshTime);
        r.scheduleRefresh();
        return r;
    }

    public static RemoteHttpTimeSeries createRemoteHttpTimeSeries(RemoteChartingTimeSeriesConfig config) throws MalformedURLException {
        RemoteHttpTimeSeries r = new RemoteHttpTimeSeries(config);
        r.scheduleRefresh();
        return r;
    }

}
