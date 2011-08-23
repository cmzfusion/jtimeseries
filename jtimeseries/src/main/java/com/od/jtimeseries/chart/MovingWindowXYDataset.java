package com.od.jtimeseries.chart;

import com.od.jtimeseries.timeseries.TimeSeriesEvent;
import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.timeseries.TimeSeriesListener;
import com.od.jtimeseries.timeseries.impl.MovingWindowTimeSeries;
import com.od.jtimeseries.timeseries.impl.WeakReferenceTimeSeriesListener;
import com.od.jtimeseries.util.time.TimeSource;
import org.jfree.data.general.SeriesChangeEvent;
import org.jfree.data.xy.AbstractXYDataset;

import java.util.ArrayList;
import java.util.List;

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

    private Object sourceSeriesLock = new Object();
    private volatile boolean modified;

    private List<WrappedSourceSeries> sourceSeries = new ArrayList<WrappedSourceSeries>();
    private List<List<TimeSeriesItem>> snapshotData = new ArrayList<List<TimeSeriesItem>>();
    private List<String> seriesKeys = new ArrayList<String>();
    private TimeSource startTime = TimeSource.OPEN_START_TIME;
    private TimeSource endTime = TimeSource.OPEN_END_TIME;
    private boolean forceRefreshAll;

    public synchronized void addTimeSeries(String key, MovingWindowTimeSeries series) {
        synchronized (sourceSeriesLock) {
            modified = true;
            sourceSeries.add(new WrappedSourceSeries(series));
            seriesKeys.add(key);
        }
    }

    public void setStartTime(TimeSource s) {
        synchronized (sourceSeriesLock) {
            this.startTime = s;
            for ( WrappedSourceSeries w : sourceSeries) {
                w.setStartTime(s);
            }
        }
        forceRefreshAll = true;
        refresh();
    }

    public void setEndTime(TimeSource s) {
        synchronized (sourceSeriesLock) {
            this.endTime = s;
            for ( WrappedSourceSeries w : sourceSeries) {
                w.setEndTime(s);
            }
        }
        forceRefreshAll = true;
        refresh();
    }

    public void refresh() {
        boolean changesExist = refreshSnapshotsForChangedSeries(forceRefreshAll);
        if ( changesExist ) {
            //fire the jfreechart change
            seriesChanged(new SeriesChangeEvent(this));
        }
    }

    private boolean refreshSnapshotsForChangedSeries(boolean forceRefresh) {
        synchronized (sourceSeriesLock) {
            boolean changesExist = false;
            int series = 0;
            for (WrappedSourceSeries s : sourceSeries) {
                if ( s.isModified() || forceRefresh) {
                    snapshotData.set(series, s.getSnapshot());
                    changesExist = true;
                }
                series++;
            }
            return changesExist;
        }
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

        private final MovingWindowTimeSeries series;
        private boolean modified = true;

        private final Object modCountLock = new Object();
        private long lastModCountOnRefresh;
        private TimeSeriesListener setModifiedFlagListener = new SetModifiedFlagListener();

        public List<TimeSeriesItem> getSnapshot() {
            synchronized (modCountLock) {
                synchronized (series) {
                    lastModCountOnRefresh = series.getModCount();
                    modified = false;
                    return series.getSnapshot();
                }
            }
        }

        public boolean isModified() {
            return modified;
        }

        public WrappedSourceSeries(MovingWindowTimeSeries series) {
            this.series = series;
            addListener();
        }

        private void addListener() {
            //we don't want to retain the MovingWindowXYDataset through the references in
            //the source timeseries listener list alone, so use WeakReferenceListener
            WeakReferenceTimeSeriesListener l = new WeakReferenceTimeSeriesListener(
                series,
                setModifiedFlagListener
            );
            series.addTimeSeriesListener(l);
        }

        public void setStartTime(TimeSource s) {
            series.setStartTime(s);
        }

        public void setEndTime(TimeSource s) {
            series.setEndTime(s);
        }


        private class SetModifiedFlagListener implements TimeSeriesListener {

            public void itemsAddedOrInserted(TimeSeriesEvent e) {
                setModified(e);
            }

            public void itemsRemoved(TimeSeriesEvent e) {
                setModified(e);
            }

            public void seriesChanged(TimeSeriesEvent e) {
                setModified(e);
            }

            private void setModified(TimeSeriesEvent e) {
                synchronized (modCountLock) {
                    //there could have been old events queued in the time series event thread
                    //when we synchronized the swing model with the timeseries
                    //we need to ignore these old events, which will have an older modCount
                    if ( e.getSeriesModCount() > lastModCountOnRefresh) {
                        modified = true;
                    }
                }
            }
        }
    }
}
