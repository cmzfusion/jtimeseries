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
import com.od.jtimeseries.timeseries.impl.DefaultTimeSeries;
import com.od.jtimeseries.util.numeric.Numeric;
import com.od.jtimeseries.timeseries.impl.WeakReferenceTimeSeriesListener;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

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
    private DefaultTimeSeries timeSeries;
    private long timeSeriesModCountOnTableCreation;

    //keep this as a field - it is wrapped as a WeakReferenceListener and would otherwise get gc'd
    private TimeSeriesListener seriesListener = new TableModelAdapterSeriesListener();
    private boolean snapshotOnly;
    private final IdentifiableTimeSeries wrappedSeries;
    private WeakReferenceTimeSeriesListener weakRefListener;
    private volatile long startTime;

    public TimeSeriesTableModelAdapter(IdentifiableTimeSeries timeSeries) {
        this(timeSeries, false, 0);
    }

    public TimeSeriesTableModelAdapter(IdentifiableTimeSeries timeSeries, long startTime) {
        this(timeSeries, false, startTime);
    }

    public TimeSeriesTableModelAdapter(IdentifiableTimeSeries timeSeries, boolean snapshotOnly, long startTime) {
        this.snapshotOnly = snapshotOnly;
        this.startTime = startTime;
        columnNames = new String[]{"Date Time", timeSeries.getDescription()};
        this.wrappedSeries = timeSeries;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
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

        synchronized (wrappedSeries) {
            //if you remove getSnapshot() from the line below, you'll need to change RemoteChartingTimeSeries
            //to trigger its lazy load on another method call
            TimeSeries seriesToCopy = wrappedSeries;
            if ( startTime > 0) {
                seriesToCopy = wrappedSeries.getSubSeries(startTime);
            }
            this.timeSeries = new DefaultTimeSeries(seriesToCopy);

            if (!snapshotOnly) {
                timeSeriesModCountOnTableCreation = timeSeries.getModCount();


                // Wrap the timeseries listener in a weak reference listener so that
                // the listener/table model can be garbage collected if all other references are cleared
                if (weakRefListener != null) {
                    wrappedSeries.removeTimeSeriesListener(weakRefListener);
                }

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
        return timeSeries.size();
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
                timeSeries.get(rowIndex).getTimestamp() :
                timeSeries.get(rowIndex).getValue();
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        throw new UnsupportedOperationException();
    }

    private void checkAndInitializeSeries() {
        if (timeSeries == null) {
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
            if (e.getFirstItemTimestamp() > timeSeries.getLatestTimestamp()) {
                int insertIndex = timeSeries.size();
                timeSeries.addAll(e.getItems());
                fireTableRowsInserted(insertIndex, timeSeries.size() - 1);
            } else if (e.getLastItemTimestamp() < timeSeries.getEarliestTimestamp()) {
                List<TimeSeriesItem> toPrepend = new ArrayList(e.getItems()); //ensure random access
                for (int loop = toPrepend.size() - 1; loop >= 0; loop--) {
                    timeSeries.prepend(toPrepend.get(loop));
                }
                fireTableRowsInserted(0, toPrepend.size() - 1);
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
        this.timeSeries = null;
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
                    if (e.getSeriesModCount() > timeSeriesModCountOnTableCreation) {
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
                    if (e.getSeriesModCount() > timeSeriesModCountOnTableCreation) {
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
                    if (e.getSeriesModCount() > timeSeriesModCountOnTableCreation) {
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
                    if (e.getSeriesModCount() > timeSeriesModCountOnTableCreation) {
                        doSeriesChanged(e);
                    }
                }
            };
            SwingUtilities.invokeLater(runnable);
        }

        protected abstract void doSeriesChanged(TimeSeriesEvent e);
    }

}
