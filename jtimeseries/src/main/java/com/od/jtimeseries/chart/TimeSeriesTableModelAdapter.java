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
package com.od.jtimeseries.chart;

import com.od.jtimeseries.timeseries.*;
import com.od.jtimeseries.timeseries.impl.MovingWindowTimeSeries;
import com.od.jtimeseries.util.numeric.Numeric;
import com.od.jtimeseries.timeseries.impl.WeakReferenceTimeSeriesListener;
import com.od.jtimeseries.util.time.Time;
import com.od.jtimeseries.util.time.TimePeriod;
import com.od.jtimeseries.util.time.TimeSource;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.concurrent.Executor;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 30-Dec-2008
 * Time: 15:03:02
 * <p/>
 * This class adapts a TimeSeries to the Swing TableModel interface
 * <p/>
 * TimeSeries instances are not generally exclusive to the event thread, and their values are changed asynchronously
 * by non-swing threads in the app - so a TimeSeries instance on its own is unlikely to be a suitable Swing 'model' in the MVC sense.
 * <p/>
 * To work around this, the TimeSeriesTableModelAdapter maintains its own list of series items, created from the
 * series while holding its lock, and then kept up to date by subscribing to TimeSeries events.
 * <p/>
 * All updates to the TimeSeriesTableModelAdapter internal list are performed on the Swing event thread.
 */
public class TimeSeriesTableModelAdapter extends AbstractTableModel {

    private String[] columnNames;
    private MovingWindowTimeSeries movingWindowSeries;
    private long wrappedSeriesModCount;

    //keep this as a field - it is wrapped as a WeakReferenceListener and would otherwise get gc'd
    private TimeSeriesListener seriesListener = new TableModelAdapterSeriesListener();
    private boolean snapshotOnly;
    private final IdentifiableTimeSeries wrappedSeries;
    private WeakReferenceTimeSeriesListener weakRefListener;
    private volatile TimeSource startTime;
    private boolean valid;

    public TimeSeriesTableModelAdapter(IdentifiableTimeSeries wrappedSeries, boolean snapshotOnly) {
        this(wrappedSeries, snapshotOnly, MovingWindowTimeSeries.OPEN_START_TIME);
    }

    public TimeSeriesTableModelAdapter(IdentifiableTimeSeries wrappedSeries) {
        this(wrappedSeries, false, MovingWindowTimeSeries.OPEN_START_TIME);
    }

    public TimeSeriesTableModelAdapter(IdentifiableTimeSeries wrappedSeries, TimeSource startTime) {
        this(wrappedSeries, false, startTime);
    }

    public TimeSeriesTableModelAdapter(IdentifiableTimeSeries wrappedSeries, boolean snapshotOnly, TimeSource startTime) {
        this.snapshotOnly = snapshotOnly;
        this.startTime = startTime;
        columnNames = new String[]{"Date Time", wrappedSeries.getDescription()};
        this.wrappedSeries = wrappedSeries;

        TimePeriod t = snapshotOnly ? null : Time.seconds(10);

        this.movingWindowSeries = new MovingWindowTimeSeries(startTime, MovingWindowTimeSeries.OPEN_END_TIME, t) {
            //fire events synchronously, since this moving window series becomes part of the swing
            //table model and should only be updated / fire events on the swing event thread
            protected Executor getSeriesEventExecutor() {
                return new Executor() {
                    public void execute(Runnable command) {
                        command.run();
                    }
                };
            }
        };
        movingWindowSeries.setCheckWindowInSwingThread(true);

        movingWindowSeries.addTimeSeriesListener(
            new TimeSeriesListener() {
                public void itemsAddedOrInserted(TimeSeriesEvent e) {
                    ListTimeSeriesEvent l = (ListTimeSeriesEvent)e;
                    fireTableRowsInserted(l.getStartIndex(), l.getEndIndex());
                }

                public void itemsRemoved(TimeSeriesEvent e) {
                    ListTimeSeriesEvent l = (ListTimeSeriesEvent)e;
                    fireTableRowsDeleted(l.getStartIndex(), l.getEndIndex());
                }

                public void itemsChanged(TimeSeriesEvent e) {
                    ListTimeSeriesEvent l = (ListTimeSeriesEvent)e;
                    fireTableRowsUpdated(l.getStartIndex(), l.getEndIndex());
                }

                public void seriesChanged(TimeSeriesEvent e) {
                    fireTableDataChanged();
                }
            }
        );
    }

    public TimeSource getStartTime() {
        return startTime;
    }

    public void setStartTime(TimeSource startTime) {
        this.startTime = startTime;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                invalidateAndFireDataChange();
            }
        });
    }

    //table model series is a snapshot of wrappedSeries owned by swing event thread, kept up to date by subscribing to
    //series events from wrappedSeries
    private void createTableModelSeries() {
        valid = true;
        synchronized (wrappedSeries) {

            // Wrap the timeseries listener in a weak reference listener so that
            // the listener/table model can be garbage collected if all other references are cleared
            if (weakRefListener != null) {
                wrappedSeries.removeTimeSeriesListener(weakRefListener);
            }

            movingWindowSeries.clear();
            //if you remove getSnapshot() from the line below, you'll need to change RemoteChartingTimeSeries
            //to trigger its lazy load on another method call
            movingWindowSeries.addAll(wrappedSeries.getSnapshot());

            if (!snapshotOnly) {
                wrappedSeriesModCount = movingWindowSeries.getModCount();

                weakRefListener = new WeakReferenceTimeSeriesListener(
                        wrappedSeries,
                        seriesListener
                );
                wrappedSeries.addTimeSeriesListener(
                        weakRefListener
                );
            }
        }
    }

    public int getRowCount() {
        checkAndInitializeSeries();
        return movingWindowSeries.size();
    }

    public int getColumnCount() {
        return 2;
    }

    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    public Class<?> getColumnClass(int columnIndex) {
        return columnIndex == 0 ? Long.class : Numeric.class;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        checkAndInitializeSeries();
        return columnIndex == 0 ?
                movingWindowSeries.get(rowIndex).getTimestamp() :
                movingWindowSeries.get(rowIndex).getValue();
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        throw new UnsupportedOperationException();
    }

    private void checkAndInitializeSeries() {
        if (! valid) {
            createTableModelSeries();
        }
    }

    //To best handle this our local timeSeries (which is part of the tableModel, so owned and only ever updated by the
    //swing thread) would need to be able to handle inserts and removes efficiently, as well as providing very efficient
    //random access to indexes for table row lookups.
    //We don't yet have a series implementation which would do this, so for the time being
    //we will just implement efficient handling for append and prepend (which comprise 99% of our events at the current
    //time. Change or remove events, or inserts within the series, will be inefficient currently
    private class TableModelAdapterSeriesListener extends ModCountAwareSeriesListener {

        public void doItemsAddedOrInserted(TimeSeriesEvent e) {
            if (e.getFirstItemTimestamp() > movingWindowSeries.getWrappedSeries().getLatestTimestamp()) {
                movingWindowSeries.addAll(e.getItems());
            } else if (e.getLastItemTimestamp() < movingWindowSeries.getWrappedSeries().getEarliestTimestamp()) {
                movingWindowSeries.addAll(0, e.getItems());
            } else {
                invalidateAndFireDataChange();
            }
        }

        public void doItemsRemoved(TimeSeriesEvent e) {
            //TODO - handle remove events efficiently
            invalidateAndFireDataChange();
        }

        public void doItemsChanged(TimeSeriesEvent e) {
            //TODO - handle change events efficiently
            invalidateAndFireDataChange();
        }

        public void doSeriesChanged(final TimeSeriesEvent h) {
            invalidateAndFireDataChange();
        }
    }

    private void invalidateAndFireDataChange() {
        valid = false;
        fireTableDataChanged();
    }

    /**
     * Process series events on the swing thread, but only if the modCount of the timeseries event
     * is > the modCount of the series at the point the table model was initialized
     * (this protects against processing older events queued up from the timeseries event thread)
     */
    abstract class ModCountAwareSeriesListener implements TimeSeriesListener {

        public final void itemsAddedOrInserted(final TimeSeriesEvent e) {
            Runnable runnable = new Runnable() {
                public void run() {
                    if (e.getSeriesModCount() > wrappedSeriesModCount) {
                        doItemsAddedOrInserted(e);
                    }
                }
            };
            SwingUtilities.invokeLater(runnable);
        }

        protected abstract void doItemsAddedOrInserted(TimeSeriesEvent e);

        public final void itemsRemoved(final TimeSeriesEvent e) {
            Runnable runnable = new Runnable() {
                public void run() {
                    if (e.getSeriesModCount() > wrappedSeriesModCount) {
                        doItemsRemoved(e);
                    }
                }
            };
            SwingUtilities.invokeLater(runnable);
        }

        protected abstract void doItemsRemoved(TimeSeriesEvent e);

        public final void itemsChanged(final TimeSeriesEvent e) {
            Runnable runnable = new Runnable() {
                public void run() {
                    if (e.getSeriesModCount() > wrappedSeriesModCount) {
                        doItemsChanged(e);
                    }
                }
            };
            SwingUtilities.invokeLater(runnable);
        }

        protected abstract void doItemsChanged(TimeSeriesEvent e);

        public final void seriesChanged(final TimeSeriesEvent e) {
            Runnable runnable = new Runnable() {
                public void run() {
                    if (e.getSeriesModCount() > wrappedSeriesModCount) {
                        doSeriesChanged(e);
                    }
                }
            };
            SwingUtilities.invokeLater(runnable);
        }

        protected abstract void doSeriesChanged(TimeSeriesEvent e);
    }

}
