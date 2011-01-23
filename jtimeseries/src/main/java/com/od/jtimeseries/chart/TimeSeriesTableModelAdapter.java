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

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 30-Dec-2008
 * Time: 15:03:02
 *
 * This class adapts a TimeSeries to the Swing TableModel interface
 *
 * TimeSeries instances are not generally exclusive to the event thread, and their values are changed asynchronously
 * by non-swing threads in the app - so a TimeSeries instance on its own is unlikely to be a suitable Swing 'model' in the MVC sense.
 *
 * To work around this, the TimeSeriesTableModelAdapter maintains its own list of series items, created from the
 * series while holding its lock, and then kept up to date by subscribing to TimeSeries events.
 *
 * All updates to the TimeSeriesTableModelAdapter internal list are performed on the Swing event thread.
 */
public class TimeSeriesTableModelAdapter extends AbstractTableModel {

    private String[] columnNames;
    private DefaultTimeSeries timeSeries;
    private String name;
    private long timeSeriesModCountOnTableCreation;

    //keep this as a field - it is wrapped as a WeakReferenceListener and would otherwise get gc'd
    private TimeSeriesListener seriesListener = new TableModelAdapterSeriesListener();

    public TimeSeriesTableModelAdapter(IdentifiableTimeSeries timeSeries) {
        this(timeSeries, false);
        name = timeSeries.getDescription();
    }

    public TimeSeriesTableModelAdapter(IdentifiableTimeSeries timeSeries, boolean snapshotOnly) {
        columnNames = new String[] { "Date Time", timeSeries.getDescription() };

        synchronized (timeSeries) {
            //if you remove getSnapshot() from the line below, you'll need to change RemoteChartingTimeSeries
            //to trigger its lazy load on another method call
            this.timeSeries = new DefaultTimeSeries(timeSeries);

            if ( ! snapshotOnly) {
                timeSeriesModCountOnTableCreation = timeSeries.getModCount();

                // Wrap the timeseries listener in a weak reference listener so that
                // the listener/table model can be garbage collected if all other references are cleared
                timeSeries.addTimeSeriesListener(
                    new WeakReferenceTimeSeriesListener(
                            timeSeries,
                            seriesListener
                    )
                );
            }
        }
    }

    public String getName() {
        return name;
    }

    public int getRowCount() {
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
        return columnIndex == 0 ?
                timeSeries.get(rowIndex).getTimestamp() :
                timeSeries.get(rowIndex).getValue();
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        throw new UnsupportedOperationException();
    }

    private class TableModelAdapterSeriesListener extends ModCountAwareSeriesListener {

        public void doItemsAddedOrInserted(TimeSeriesEvent e) {
            if (e.getFirstItemTimestamp() >= timeSeries.getLatestTimestamp()) {
                timeSeries.addAll(e.getItems());
            }
        }

        public void doItemsRemoved(TimeSeriesEvent e) {

        }

        public void doItemsChanged(TimeSeriesEvent e) {

        }

        public void doSeriesChanged(final TimeSeriesEvent h) {

        }

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
                     if ( e.getSeriesModCount() > timeSeriesModCountOnTableCreation) {
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
                     if ( e.getSeriesModCount() > timeSeriesModCountOnTableCreation) {
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
                     if ( e.getSeriesModCount() > timeSeriesModCountOnTableCreation) {
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
                     if ( e.getSeriesModCount() > timeSeriesModCountOnTableCreation) {
                         doSeriesChanged(e);
                     }
                 }
             };
             SwingUtilities.invokeLater(runnable);
         }

         protected abstract void doSeriesChanged(TimeSeriesEvent e);
     }
    
}
