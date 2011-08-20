/**
 * Copyright (C) 2011 (nick @ objectdefinitions.com)
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
 */
public class TimeSeriesTableModelAdapter extends AbstractTableModel {

    private String[] columnNames;
    private IndexedTimeSeries wrappedSeries = new DefaultTimeSeries();

    public TimeSeriesTableModelAdapter(IdentifiableTimeSeries series) {
        columnNames = new String[]{"Date Time", series.getDescription()};
        wrappedSeries = new DefaultTimeSeries(series.getSnapshot());
    }

    public int getRowCount() {
        return wrappedSeries.size();
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
                wrappedSeries.getItem(rowIndex).getTimestamp() :
                wrappedSeries.getItem(rowIndex).getValue();
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        throw new UnsupportedOperationException();
    }
//
//    /**
//     * Process series events on the swing thread, but only if the modCount of the timeseries event
//     * is > the modCount of the series at the point the table model was initialized
//     * (this protects against processing older events queued up from the timeseries event thread)
//     */
//    abstract class ModCountAwareSeriesListener implements TimeSeriesListener {
//
//        public final void itemsAddedOrInserted(final TimeSeriesEvent e) {
//            Runnable runnable = new Runnable() {
//                public void run() {
//                    if (e.getSeriesModCount() > wrappedSeriesModCount) {
//                        doItemsAddedOrInserted(e);
//                    }
//                }
//            };
//            SwingUtilities.invokeLater(runnable);
//        }
//
//        protected abstract void doItemsAddedOrInserted(TimeSeriesEvent e);
//
//        public final void itemsRemoved(final TimeSeriesEvent e) {
//            Runnable runnable = new Runnable() {
//                public void run() {
//                    if (e.getSeriesModCount() > wrappedSeriesModCount) {
//                        doItemsRemoved(e);
//                    }
//                }
//            };
//            SwingUtilities.invokeLater(runnable);
//        }
//
//        protected abstract void doItemsRemoved(TimeSeriesEvent e);
//
//        public final void seriesChanged(final TimeSeriesEvent e) {
//            Runnable runnable = new Runnable() {
//                public void run() {
//                    if (e.getSeriesModCount() > wrappedSeriesModCount) {
//                        doSeriesChanged(e);
//                    }
//                }
//            };
//            SwingUtilities.invokeLater(runnable);
//        }
//
//        protected abstract void doSeriesChanged(TimeSeriesEvent e);
//    }

}
