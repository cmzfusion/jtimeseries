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
            this.timeSeries = new DefaultTimeSeries(timeSeries.getSnapshot());

            if ( ! snapshotOnly) {
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

    private class TableModelAdapterSeriesListener implements TimeSeriesListener {

        public void itemsAddedOrInserted(TimeSeriesEvent e) {
            final ListTimeSeriesEvent h = getListEvent(e);
            SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        int index = h.getStartIndex();
                        timeSeries.addAll(index, h.getItems());
                        fireTableRowsInserted(h.getStartIndex(), h.getEndIndex());
                    }
                }
            );
        }

        public void itemsRemoved(TimeSeriesEvent e) {
            final ListTimeSeriesEvent h = getListEvent(e);
            SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        //TODO - need to add a remove range to ListTimeSeries to implement this efficiently
                        for (int loop=0; loop < h.getItems().size(); loop++) {
                            timeSeries.remove(h.getStartIndex());
                        }
                        fireTableRowsDeleted(h.getStartIndex(), h.getEndIndex());
                    }
                }
            );
        }

        public void itemChanged(TimeSeriesEvent e) {
            final ListTimeSeriesEvent h = getListEvent(e);
            SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        int index = h.getStartIndex();
                        for (TimeSeriesItem i : h.getItems()) {
                            timeSeries.set(index++, i);
                        }
                        fireTableRowsUpdated(h.getStartIndex(), h.getEndIndex());
                    }
                }
            );
        }

        private ListTimeSeriesEvent getListEvent(TimeSeriesEvent e) {
            if ( e instanceof ListTimeSeriesEvent) {
                return (ListTimeSeriesEvent)e;
            } else {
                throw new UnsupportedOperationException("TimeSeriesTableModelAdapter only supports ListTimeSeries presently");
            }
        }

        public void seriesChanged(final TimeSeriesEvent h) {
            SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        timeSeries = new DefaultTimeSeries(h.getItems());
                        fireTableDataChanged();
                    }
                }
            );
        }

     }
    
}
