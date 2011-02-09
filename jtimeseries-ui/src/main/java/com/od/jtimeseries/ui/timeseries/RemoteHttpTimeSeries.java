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
import com.od.jtimeseries.ui.util.ExecuteWeakReferencedCommandTask;
import com.od.jtimeseries.util.NamedExecutors;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.util.time.Time;
import com.od.jtimeseries.util.time.TimePeriod;
import swingcommand.BackgroundTask;
import swingcommand.SwingCommand;
import swingcommand.Task;
import swingcommand.TaskListenerAdapter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
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
public class RemoteHttpTimeSeries extends DefaultUITimeSeries implements ChartSeriesListener {

    private static final LogMethods logMethods = LogUtils.getLogMethods(ChartingTimeSeries.class);

    private final Map<UIPropertiesTimeSeries, UIPropertiesTimeSeries> weakClientSeries = new WeakHashMap<UIPropertiesTimeSeries, UIPropertiesTimeSeries>();

    private static ScheduledExecutorService refreshExecutor = NamedExecutors.newSingleThreadScheduledExecutor("RemoteHttpTimeSeriesRefresh");
    private static final int MIN_REFRESH_TIME_SECONDS = 10;

    private RefreshDataCommand refreshDataCommand = new RefreshDataCommand();
    private volatile ScheduledFuture refreshTask;
    private volatile int displayedChartCount;
    private volatile int errorCount;
    private volatile boolean ticking = true;

    //a series starts not 'stale' and remains not stale until a set number of consecutive download failures have occurred
    private static final int MAX_ERRORS_BEFORE_DISCONNECT = 4;
    private static final int NOT_TICKING_REFRESH_TIME = 1800; //half an hour


    private RemoteHttpTimeSeries(UiTimeSeriesConfig config) throws MalformedURLException {
        this(config.getId(), config.getDescription(), new URL(config.getTimeSeriesUrl()), Time.seconds(config.getRefreshTimeSeconds()));
        this.displayName = config.getDisplayName();
    }

    private RemoteHttpTimeSeries(String id, String description, URL timeSeriesUrl, TimePeriod refreshTime) {
        super(id, description);
        setTimeSeriesURL(timeSeriesUrl);
        setRefreshTimeSeconds((int)refreshTime.getLengthInMillis() / 1000);
    }

    public void setRefreshTimeSeconds(int seconds) {
        super.setRefreshTimeSeconds(Math.max(seconds, MIN_REFRESH_TIME_SECONDS));
        RemoteHttpTimeSeries.logMethods.logInfo("Changing refresh time for series " + getId() + " to " + getRefreshTimeSeconds() + " seconds");
        scheduleRefreshIfDisplayed(false);
    }

    public void setStale(boolean stale) {
        if (!stale) {
            errorCount = 0;
        }
        super.setStale(stale);
    }

    //Cancel any existing task and schedule a new one if series selected
    private synchronized void scheduleRefreshIfDisplayed(boolean immediateRefresh) {
        updateDisplayedChartCount();

        if ( refreshTask != null) {
            refreshTask.cancel(false);
        }

        if ( displayedChartCount > 0) {
            //A task which doesn't hold a strong reference to this series
            //the series can be collected if no longer referenced elsewhere, even if a refresh is scheduled
            ExecuteWeakReferencedCommandTask runCommandTask = new ExecuteWeakReferencedCommandTask(refreshDataCommand);

            int refreshTime = calculateRefreshTime();
            int delayTime = immediateRefresh ? 1 : refreshTime;

            refreshTask = refreshExecutor.scheduleWithFixedDelay(
                runCommandTask, delayTime, refreshTime, TimeUnit.SECONDS
            );

            //cancel refresh task if this series is gc'd
            runCommandTask.setFutureToCancel(refreshTask);
        }
    }

    private int calculateRefreshTime() {
        return ticking ? refreshTimeSeconds : NOT_TICKING_REFRESH_TIME;
    }

    private boolean setTickingFlag() {
        long timeSinceUpdate = System.currentTimeMillis() - getLatestTimestamp();
        boolean ticking = timeSinceUpdate > Time.hours(1).getLengthInMillis();
        boolean result = ticking != this.ticking;
        this.ticking = ticking;
        return result;
    }

    public void chartSeriesChanged(ChartSeriesEvent e) {
        boolean refreshImmediately = false;
        switch(e.getChartSeriesEventType()) {
            case SERIES_CHART_DISPLAYED:
                refreshImmediately = true;
                weakClientSeries.put(e.getSourceSeries(), e.getSourceSeries());
                break;
            case SERIES_CHART_HIDDEN:
                weakClientSeries.put(e.getSourceSeries(), e.getSourceSeries());
                break;
            case SERIES_CHART_DISPOSED:
                weakClientSeries.remove(e.getSourceSeries());
                break;
            default:
        }
        scheduleRefreshIfDisplayed(refreshImmediately);
    }

    private void updateDisplayedChartCount() {
        Set<UIPropertiesTimeSeries> series;
        synchronized(weakClientSeries) {
            series = new HashSet<UIPropertiesTimeSeries>(weakClientSeries.keySet());
        }

        int displayedCount = 0;
        for ( UIPropertiesTimeSeries s : series) {
            if ( s.isSelected()) {
                displayedCount++;
            }
        }
        this.displayedChartCount = displayedCount;
    }


    private class RefreshDataCommand extends SwingCommand {

        public RefreshDataCommand() {

            //if we exceed the error count when running the load, set the series to stale
            //the user will need to re-enable it to start the load off again
            addTaskListener(new SetStaleOnErrorListener());
            addTaskListener(new RescheduleListener());
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

        //perform disconnection if task failed too many times
        private class SetStaleOnErrorListener extends TaskListenerAdapter {

            public void error(Task task, Throwable error) {
                errorCount++;
                if ( errorCount >= MAX_ERRORS_BEFORE_DISCONNECT) {
                    setStale(true);
                }
            }
        }

        private class RescheduleListener extends TaskListenerAdapter {
            public void finished(Task task) {
                boolean tickingChanged = setTickingFlag();
                if ( tickingChanged ) {
                    scheduleRefreshIfDisplayed(false);
                }
            }
        }
    }



    //Factory methods to construct, ensuring refresh is also scheduled after construction

    public static RemoteHttpTimeSeries createRemoteHttpTimeSeries(String id, String description, URL timeSeriesUrl, TimePeriod refreshTime) {
        RemoteHttpTimeSeries r = new RemoteHttpTimeSeries(id, description, timeSeriesUrl, refreshTime);
        r.scheduleRefreshIfDisplayed(true);
        return r;
    }

    public static RemoteHttpTimeSeries createRemoteHttpTimeSeries(UiTimeSeriesConfig config) throws MalformedURLException {
        RemoteHttpTimeSeries r = new RemoteHttpTimeSeries(config);
        r.scheduleRefreshIfDisplayed(true);
        return r;
    }

}
