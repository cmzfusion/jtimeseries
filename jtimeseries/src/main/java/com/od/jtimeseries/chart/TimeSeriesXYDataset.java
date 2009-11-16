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

import com.od.jtimeseries.util.numeric.Numeric;
import org.jfree.data.xy.AbstractXYDataset;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 30-Dec-2008
 * Time: 16:30:29
 */
public class TimeSeriesXYDataset extends AbstractXYDataset {

    private String sourceName;
    private TableModel timeSeriesTableModel;

    public TimeSeriesXYDataset(String sourceName, TableModel timeSeriesTableModel) {
        this.sourceName = sourceName;
        this.timeSeriesTableModel = timeSeriesTableModel;
        timeSeriesTableModel.addTableModelListener(new TimeSeriesTableListener());
    }

    public int getSeriesCount() {
        return timeSeriesTableModel.getColumnCount() - 1;
    }

    public Comparable getSeriesKey(int series) {
        return sourceName;
    }

    public int getItemCount(int series) {
        return timeSeriesTableModel.getRowCount();
    }

    public Number getX(int series, int item) {
        return ((Long) timeSeriesTableModel.getValueAt(item, 0));
    }

    public Number getY(int series, int item) {
        return ((Numeric) timeSeriesTableModel.getValueAt(item, series + 1)).doubleValue();
    }

    private class TimeSeriesTableListener implements TableModelListener {

        public void tableChanged(TableModelEvent e) {
            fireDatasetChanged();
        }
    }
}
