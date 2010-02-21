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
package com.od.jtimeseries.ui.selector.selectorpanel;

import com.jidesoft.grid.*;
import com.jidesoft.converter.ConverterContext;
import com.od.jtimeseries.ui.timeseries.RemoteChartingTimeSeries;
import com.od.jtimeseries.ui.util.TableModelEventParser;
import com.od.jtimeseries.ui.util.TableEventDispatcher;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 25-May-2009
 * Time: 21:20:57
 * To change this template use File | Settings | File Templates.
 *
 * A simple model to show the elements of the context path as additional columns
 * More work will be needed to handle events optimally
 */
public class PathTokenizingTableModel extends AbstractTableModel {

    private TableEventDispatcher eventDispatcher = new TableEventDispatcher(this);
    private BeanTableModel<RemoteChartingTimeSeries> wrappedModel;
    private int maxPathElements;

    public PathTokenizingTableModel(BeanTableModel<RemoteChartingTimeSeries> wrappedModel) {
        this.wrappedModel = wrappedModel;
        SeriesModelEventParserListener eventListener = new SeriesModelEventParserListener();
        TableModelEventParser eventParser = new TableModelEventParser(eventListener);
        wrappedModel.addTableModelListener(eventParser);
        recalculate(false);
    }

    private void recalculate(boolean fireUpdate) {
        recalculate(fireUpdate, 0, wrappedModel.getRowCount() - 1);
    }

    private void recalculate(boolean fireUpdate, int firstRow, int lastRow) {
        int oldMax = maxPathElements;
        for ( int row = firstRow; row <= lastRow; row++) {
            RemoteChartingTimeSeries s = wrappedModel.getObject(row);
            maxPathElements = Math.max(maxPathElements, s.getPathElements().size());
        }

        if ( oldMax != maxPathElements) {
            eventDispatcher.fireTableStructureChanged();
        } else {
            if ( fireUpdate ) {
                eventDispatcher.fireTableDataChanged();
            } else {
                eventDispatcher.fireTableStructureChanged();
            }
        }
    }

    public Object getObject(int rowIndex) {
        return wrappedModel.getObject(rowIndex);
    }

    public int getRowCount() {
        return wrappedModel.getRowCount();
    }

    public int getColumnCount() {
        return wrappedModel.getColumnCount() + maxPathElements;
    }

    public String getColumnName(int columnIndex) {
        if (isColumnInWrappedModel(columnIndex)) {
            return wrappedModel.getColumnName(columnIndex);
        } else {
            return String.valueOf("path " + (columnIndex - wrappedModel.getColumnCount()));
        }
    }

    public Class<?> getColumnClass(int columnIndex) {
        if ( isColumnInWrappedModel(columnIndex)) {
            return wrappedModel.getColumnClass(columnIndex);
        } else {
            return String.class;
        }
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return wrappedModel.isCellEditable(rowIndex, columnIndex);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if ( isColumnInWrappedModel(columnIndex)) {
            return wrappedModel.getValueAt(rowIndex, columnIndex);
        } else {
            int extraColsIndex = getExtraColsIndex(columnIndex);
            List<String> pathElements = ((RemoteChartingTimeSeries)wrappedModel.getObject(rowIndex)).getPathElements();
            return pathElements.size() > extraColsIndex ?  pathElements.get(extraColsIndex) : null;
        }
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if ( isColumnInWrappedModel(columnIndex)) {
            wrappedModel.setValueAt(aValue, rowIndex, columnIndex);
        }
    }

    public void addTableModelListener(TableModelListener l) {
        eventDispatcher.addTableModelListener(l);
    }

    public void removeTableModelListener(TableModelListener l) {
        eventDispatcher.removeTableModelListener(l);
    }

    private int getExtraColsIndex(int columnIndex) {
        return columnIndex - wrappedModel.getColumnCount();
    }

    private boolean isColumnInWrappedModel(int columnIndex) {
        return columnIndex < wrappedModel.getColumnCount();
    }

    private class SeriesModelEventParserListener implements TableModelEventParser.TableModelEventParserListener {

        public void tableStructureChanged(TableModelEvent e) {
            recalculate(false);
        }

        public void tableDataChanged(TableModelEvent e) {
            recalculate(true);
        }

        public void tableRowsUpdated(int firstRow, int lastRow, TableModelEvent e) {
            eventDispatcher.fireTableRowsUpdated(firstRow, lastRow);
        }

        public void tableCellsUpdated(int firstRow, int lastRow, int column, TableModelEvent e) {
            eventDispatcher.fireTableCellsUpdated(firstRow, lastRow, column);
        }

        public void tableRowsDeleted(int firstRow, int lastRow, TableModelEvent e) {
            eventDispatcher.fireTableRowsDeleted(firstRow, lastRow);
        }

        public void tableRowsInserted(int firstRow, int lastRow, TableModelEvent e) {
            recalculate(true, firstRow, lastRow);
        }
    }
}
