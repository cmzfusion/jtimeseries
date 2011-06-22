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
package com.od.jtimeseries.ui.util;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.HashSet;


/**
 * @author EbbuttN
 *         <p/>
 *         Utility class to make it easy for table models to maintain their listener list and propagate table events
 */
public class TableEventDispatcher {

    private TableModel owner;
    private final HashSet<TableModelListener> tableModelListeners = new HashSet<TableModelListener>();

    public TableEventDispatcher(TableModel owner) {
        this.owner = owner;
    }

    public void addTableModelListener(TableModelListener l) {
        synchronized (tableModelListeners) {
            tableModelListeners.add(l);
        }
    }

    public void removeTableModelListener(TableModelListener l) {
        synchronized (tableModelListeners) {
            tableModelListeners.remove(l);
        }
    }

    public void fireTableRowsInserted(int firstRow, int lastRow) {
        fireTableModelListeners(new TableModelEvent(owner, firstRow, lastRow, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
    }

    public void fireTableRowsUpdated(int firstRow, int lastRow) {
        fireTableModelListeners(new TableModelEvent(owner, firstRow, lastRow, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
    }

    public void fireTableRowsDeleted(int firstRow, int lastRow) {
        fireTableModelListeners(new TableModelEvent(owner, firstRow, lastRow, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE));
    }

    public void fireTableCellsUpdated(int firstRow, int lastRow, int column) {
        fireTableModelListeners(new TableModelEvent(owner, firstRow, lastRow, column, TableModelEvent.UPDATE));
    }


    public void fireTableDataChanged() {
        fireTableModelListeners(new TableModelEvent(owner));
    }

    public void fireTableStructureChanged() {
        fireTableModelListeners(new TableModelEvent(owner, TableModelEvent.HEADER_ROW));
    }

    // Handle table model listeners registered to this view
    private void fireTableModelListeners(TableModelEvent t) {
        synchronized (tableModelListeners) {
            for (TableModelListener tableModelListener : tableModelListeners) {
                tableModelListener.tableChanged(t);
            }
        }
    }

}
