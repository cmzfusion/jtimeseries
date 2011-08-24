package com.od.jtimeseries.chart;

import com.od.jtimeseries.timeseries.ModCount;
import com.od.jtimeseries.timeseries.TimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.timeseries.impl.MovingWindowTimeSeries;
import com.od.jtimeseries.util.NamedExecutors;
import com.od.jtimeseries.util.time.TimePeriod;
import com.od.jtimeseries.util.time.TimeSource;
import org.jfree.data.general.SeriesChangeEvent;
import org.jfree.data.xy.AbstractXYDataset;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 22/08/11
 * Time: 09:01
 *
 * Adapt one or more MovingWindowTimeSeries to the XYDataSet interface so
 * that they can be charted in JFreeChart
 *
 * This class is mainly for use in Swing ui
 * Data held by swing models should not be changed outside the AWT event thread. This doesn't fit
 * nicely with timeseries which can be modified asynchronously.
 * To solve this, this model keeps a snapshot of the data for each source series. The snapshots are only updated
 * on the AWT thread. When we recalculate the window, we create new snapshots if the underlying series data has
 * changed
 */
public class MovingWindowXYDataset extends AbstractXYDataset {

    private static ScheduledExecutorService scheduledExecutorService = NamedExecutors.newScheduledThreadPool(MovingWindowXYDataset.class.getSimpleName(), 2);

    private final Object sourceSeriesLock = new Object();
    private List<WrappedSourceSeries> sourceSeries = new ArrayList<WrappedSourceSeries>();
    private List<List<TimeSeriesItem>> snapshotData = new ArrayList<List<TimeSeriesItem>>();
    private List<String> seriesKeys = new ArrayList<String>();
    private TimeSource startTime = TimeSource.OPEN_START_TIME;
    private TimeSource endTime = TimeSource.OPEN_END_TIME;
    private volatile Future movingWindowRefreshTask;
    private boolean updateOnSwingThread = true;

    public MovingWindowXYDataset(TimeSource startTime, TimeSource endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public synchronized void addTimeSeries(String key, TimeSeries series) {
        synchronized (sourceSeriesLock) {
            sourceSeries.add(new WrappedSourceSeries(series));
            seriesKeys.add(key);
        }
        refresh();
    }

    public void startMovingWindow(TimePeriod timePeriod) {
        stopMovingWindow();
        movingWindowRefreshTask = scheduledExecutorService.scheduleWithFixedDelay(
            new MoveWindowTask(),
            0,
            timePeriod.getLengthInMillis(),
            TimeUnit.MILLISECONDS
        );
    }

    public void stopMovingWindow() {
        if ( movingWindowRefreshTask != null) {
            movingWindowRefreshTask.cancel(false);
        }
    }


    public void setStartTime(TimeSource s) {
        synchronized (sourceSeriesLock) {
            this.startTime = s;
        }
        refresh();
    }

    public void setEndTime(TimeSource s) {
        synchronized (sourceSeriesLock) {
            this.endTime = s;
        }
        refresh();
    }

    public void refresh() {
        new MoveWindowTask().run();
    }

    public void setUpdateOnSwingThread(boolean updateOnSwingThread) {
        this.updateOnSwingThread = updateOnSwingThread;
    }

    public int getSeriesCount() {
        return 0;
    }

    public Comparable getSeriesKey(int series) {
        return seriesKeys.get(series);
    }

    public int getItemCount(int series) {
        return snapshotData.get(series).size();
    }

    public Number getX(int series, int item) {
        return snapshotData.get(series).get(item).getTimestamp();
    }

    public Number getY(int series, int item) {
        return snapshotData.get(series).get(item).doubleValue();
    }

    private class WrappedSourceSeries {

        private final TimeSeries series;
        private final Object modCountLock = new Object();
        private volatile long lastModCountOnRefresh;

        public WrappedSourceSeries(TimeSeries series) {
            this.series = series;
        }

        public List<TimeSeriesItem> getSnapshotAndUpdateLastModCount(long start, long end) {
            synchronized (modCountLock) {
                synchronized (series) {
                    lastModCountOnRefresh = series.getModCount();
                    return series.getItemsInRange(start, end);
                }
            }
        }

        public boolean isModified() {
            return lastModCountOnRefresh != series.getModCount();
        }
    }

    private class MoveWindowTask implements Runnable {

        public void run() {
            //call refresh on the swing thread, to update the snapshots for any changed series
            Runnable refreshRunnable = new Runnable() {
                public void run() {
                    boolean changesExist = refreshSnapshotsForChangedSeries();
                    if ( changesExist ) {
                        //fire the jfreechart change
                        seriesChanged(new SeriesChangeEvent(this));
                    }
                }
            };

            if ( updateOnSwingThread && ! SwingUtilities.isEventDispatchThread()) {
                SwingUtilities.invokeLater(refreshRunnable);
            } else {
                refreshRunnable.run();
            }
        }

        private boolean refreshSnapshotsForChangedSeries() {
            long start = startTime.getTime();
            long end = endTime.getTime();
            synchronized (sourceSeriesLock) {
                boolean changesExist = false;
                int series = 0;
                for (WrappedSourceSeries s : sourceSeries) {
                    if ( s.isModified()) {
                        snapshotData.set(series, s.getSnapshotAndUpdateLastModCount(start, end));
                        changesExist = true;
                    }
                    series++;
                }
                return changesExist;
            }
        }
    }
}
