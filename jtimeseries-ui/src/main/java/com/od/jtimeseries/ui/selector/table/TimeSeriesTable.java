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
package com.od.jtimeseries.ui.selector.table;

import com.jidesoft.grid.AutoFilterTableHeader;
import com.jidesoft.grid.SortableTable;
import com.jidesoft.grid.TableModelWrapperUtils;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.util.NamedExecutors;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
* User: nick
* Date: 27-Feb-2010
* Time: 15:57:14
* To change this template use File | Settings | File Templates.
*/
class TimeSeriesTable<E extends UIPropertiesTimeSeries> extends SortableTable {

    private static final Color STALE_SERIES_COLOR = new Color(248,165,169);
    private BeanPerRowModel<E> tableModel;

    private static final ScheduledExecutorService resortExecutor = NamedExecutors.newSingleThreadScheduledExecutor("TableResort");
    private ScheduledFuture resortFuture;

    public TimeSeriesTable(BeanPerRowModel<E> tableModel, TableColumnManager<E> columnManager) {
        super(tableModel);
        setColumnModel(columnManager.getColumnModel());
        this.tableModel = tableModel;
        setClearSelectionOnTableDataChanges(false);
        setRowResizable(true);
        setVariousRowHeights(true);
        setSelectInsertedRows(false);
        setAutoSelectTextWhenStartsEditing(true);
        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setRowResizable(false);
        AutoFilterTableHeader header = new AutoFilterTableHeader(this);
        header.setAutoFilterEnabled(true);
        header.setShowFilterName(true);
        header.setAllowMultipleValues(true);
        header.setShowFilterNameAsToolTip(true);
        setAutoCreateColumnsFromModel(false);
        setTableHeader(header);
        setModel(tableModel);

        //this has to be done after setting the model
        //it seems these values are reset otherwise:
        setOptimized(true);
        setAutoResort(false);
        //////
        addResortListener(tableModel);
    }

    //if we resort on every update performance is terrible for multiple cell change/row change events
    //Here we queue a delayed resort when the table model changes affect sorted columns
    private void addResortListener(TableModel m) {
        m.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                if ( e.getColumn() == TableModelEvent.ALL_COLUMNS
                || getSortableTableModel().isColumnSorted(e.getColumn())) {
                    queueForResort();
                }
            }
        });
    }

    private void queueForResort() {
        if( resortFuture == null) {
            resortFuture = resortExecutor.schedule(
                new ResortRunnable(), 3000, TimeUnit.MILLISECONDS
            );
        }
    }

    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);

        int actualRow = TableModelWrapperUtils.getActualRowAt(getModel(), row);
        E object = tableModel.getObject(actualRow);
        if (object.isStale()) {
            c.setBackground(STALE_SERIES_COLOR);
        } else {
            if (isCellSelected(row, column)) {
                c.setBackground(getSelectionBackground());
            } else {
                c.setBackground(getBackground());
            }
        }
        return c;
    }

    //stop the creation of initial columns
    public void createDefaultColumnsFromModel() {
    }

    private class ResortRunnable implements Runnable {
        public void run() {
            SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        resort();
                        resortFuture = null;
                    }
                }
            );
        }
    }
}
