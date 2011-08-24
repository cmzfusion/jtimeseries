package com.od.jtimeseries.chart;

import com.od.jtimeseries.timeseries.TimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.util.NamedExecutors;
import com.od.jtimeseries.util.time.TimePeriod;
import com.od.jtimeseries.util.time.TimeSource;
import org.jfree.data.general.SeriesChangeEvent;
import org.jfree.data.xy.AbstractXYDataset;

import javax.swing.*;
import java.lang.ref.WeakReference;
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
 * Adapt one or more TimeSeries to the XYDataSet interface so that they can be charted in JFreeChart
 *
 * This class is mainly for use in Swing uis.
 * When the moving window is refreshed change events will be fired on the Swing event thread
 * (You could create a MovingWindowXYDataset in another thread as a one off snapshot, and it would be consistent view to the creating thread,
 * but in that case you can't use the moving window functionality.)
 *
 * Models used for Swing ui cannot be modified on threads other than the AWT event thread, if consistency is to be maintained.
 * This doesn't fit easily with time series which can be modified asynchronously.
 * To preserve consistency for Swing, this model keeps a snapshot of the data from each source series. The snapshots are only updated
 * on the AWT thread. When we refresh the window, new snapshots are created if the underlying series data has changed - since this
 * takes place on AWT thread the snapshots are safe to use for Swing models
 */
public class MovingWindowXYDataset<E extends TimeSeries> extends AbstractXYDataset {

    private static ScheduledExecutorService scheduledExecutorService = NamedExecutors.newScheduledThreadPool(MovingWindowXYDataset.class.getSimpleName(), 2);

    private List<WrappedSourceSeries<E>> sourceSeries = new ArrayList<WrappedSourceSeries<E>>();
    private List<List<TimeSeriesItem>> snapshotData = new ArrayList<List<TimeSeriesItem>>();
    private TimeSource startTime = TimeSource.OPEN_START_TIME;
    private TimeSource endTime = TimeSource.OPEN_END_TIME;
    private volatile Future movingWindowRefreshTask;

    public MovingWindowXYDataset(TimeSource startTime, TimeSource endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void addTimeSeries(String key, TimeSeries series) {
        sourceSeries.add(new WrappedSourceSeries(key, series));
        refresh();
    }

    public void setStartTime(TimeSource s) {
        this.startTime = s;
        refresh();
    }

    public void setEndTime(TimeSource s) {
        this.endTime = s;
        refresh();
    }

    public void startMovingWindow(TimePeriod timePeriod) {
        stopMovingWindow();
        MoveWindowTask t = new MoveWindowTask(this);
        movingWindowRefreshTask = scheduledExecutorService.scheduleWithFixedDelay(
            t,
            timePeriod.getLengthInMillis(),
            timePeriod.getLengthInMillis(),
            TimeUnit.MILLISECONDS
        );
        t.setFuture(movingWindowRefreshTask);
    }

    public void stopMovingWindow() {
        if ( movingWindowRefreshTask != null) {
            movingWindowRefreshTask.cancel(false);
        }
    }

    public E getTimeSeries(int index) {
        return sourceSeries.get(index).getSeries();
    }

    public int getSeriesCount() {
        return snapshotData.size();
    }

    public Comparable getSeriesKey(int series) {
        return sourceSeries.get(series).getKey();
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

    private void refresh() {
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

        if (! SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(refreshRunnable);
        } else {
            refreshRunnable.run();
        }
    }

    private boolean refreshSnapshotsForChangedSeries() {
        long start = startTime.getTime();
        long end = endTime.getTime();
        boolean changesExist = false;
        int seriesIndex = 0;
        for (WrappedSourceSeries s : sourceSeries) {
            if ( s.isModified()) {
                List snapshot = s.getSnapshotAndUpdateLastModCount(start, end);
                if ( snapshotData.size() <= seriesIndex) {
                    snapshotData.add(snapshot);
                } else {
                    snapshotData.set(seriesIndex, snapshot);
                }
                changesExist = true;
            }
            seriesIndex++;
        }
        return changesExist;
    }

    private class WrappedSourceSeries<E extends TimeSeries> {

        private String key;
        private final E series;
        private final Object modCountLock = new Object();
        private volatile long lastModCountOnRefresh = -1;

        public WrappedSourceSeries(String key, E series) {
            this.key = key;
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

        public E getSeries() {
            return series;
        }

        public String getKey() {
            return key;
        }
    }

    //a task to fresh the window, keeping only a weak reference to the XYDataset to
    //prevent the executor tasks keeping it in memory once other references have cleared
    //cancel the task once the XYDataset is collected
    private static class MoveWindowTask implements Runnable {

        public WeakReference<MovingWindowXYDataset> xyDatasetWeakReference;
        private Future future;

        public MoveWindowTask(MovingWindowXYDataset xyDataset) {
            xyDatasetWeakReference = new WeakReference<MovingWindowXYDataset>(xyDataset);
        }

        public void setFuture(Future future) {
            this.future = future;
        }

        public void run() {
            MovingWindowXYDataset d = xyDatasetWeakReference.get();
            if ( d != null) {
                d.refresh();
            } else {
                //xy dataset is collected, cancel this task
                future.cancel(false);
            }
        }
    }
}
