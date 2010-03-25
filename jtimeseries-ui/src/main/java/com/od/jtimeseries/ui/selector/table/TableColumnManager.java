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

import com.od.jtimeseries.context.ContextProperties;
import com.od.jtimeseries.ui.timeseries.RemoteChartingTimeSeries;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableCellRenderer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 27-Feb-2010
 * Time: 16:19:16
 * To change this template use File | Settings | File Templates.
 */
public class TableColumnManager {

    private TableColumnModel columnModel = new DefaultTableColumnModel();
    private BeanPerRowModel<RemoteChartingTimeSeries> tableModel;
    private String selectionColumnName;

    public TableColumnManager(BeanPerRowModel<RemoteChartingTimeSeries> tableModel, String selectionColumnName) {
        this.tableModel = tableModel;
        this.selectionColumnName = selectionColumnName;
        setupDefaultColumns();
    }

    private void setupDefaultColumns() {
        setColumns(getDefaultColumnSettings());
    }

    public TableColumnModel getColumnModel() {
        return columnModel;
    }

    public void setColumns(java.util.List<ColumnSettings> columnSettings) {
        removeAllColumns();
        for ( ColumnSettings s : columnSettings) {
            TableColumn c = addColumn(s.getColumnName());
            c.setPreferredWidth(s.getColumnWidth());
        }
    }

    private void removeAllColumns() {
        for ( int loop=columnModel.getColumnCount() - 1; loop >=0 ; loop --) {
            columnModel.removeColumn(columnModel.getColumn(loop));
        }
    }


    public List<ColumnSettings> getColumnSettings() {
        List<ColumnSettings> l = new ArrayList<ColumnSettings>();
        for ( int col = 0; col < columnModel.getColumnCount(); col ++ ) {
            TableColumn c = columnModel.getColumn(col);
            l.add(new ColumnSettings(
                tableModel.getColumnName(c.getModelIndex()),
                c.getPreferredWidth()
            ));
        }
        return l;
    }

    public String[] getAllColumnNames() {
        java.util.List<String> columnNames = new ArrayList<String>();
        for ( int col=0; col < tableModel.getColumnCount(); col ++) {
            columnNames.add(tableModel.getColumnName(col));
        }
        return columnNames.toArray(new String[columnNames.size()]);
    }

    public TableColumn addColumn(String columnName) {
        addDynamicColumnIfRequired(columnName);
        TableColumn newColumn = null;
        if ( ! existsInColumnModel(columnName)) {
            newColumn = createColumn(columnName);
            columnModel.addColumn(newColumn);
        }
        return newColumn;
    }

    public void removeColumn(String columnName) {
        for ( int col = 0; col < columnModel.getColumnCount(); col++) {
            TableColumn column = columnModel.getColumn(col);
            if ( tableModel.getColumnName(column.getModelIndex()).equals(columnName) ) {
                columnModel.removeColumn(column);
                break;
            }
        }
    }

    public boolean isInTable(String columnName) {
        boolean result = false;
        for ( int col = 0; col < columnModel.getColumnCount(); col++) {
            TableColumn column = columnModel.getColumn(col);
            if ( tableModel.getColumnName(column.getModelIndex()).equals(columnName) ) {
                result = true;
                break;
            }
        }
        return result;
    }

    private TableColumn createColumn(String columnName) {
        TableColumn newColumn = new TableColumn(
            getColumnIndex(columnName),
            FixedColumns.getDefaultColumnWidth(columnName)
        );
        TableCellRenderer r = FixedColumns.getCellRenderer(columnName);
        if ( r != null ) {
            newColumn.setCellRenderer(r);
        }
        setColumnIdentifier(columnName, newColumn);
        return newColumn;
    }

    private void setColumnIdentifier(String columnName, TableColumn newColumn) {
        //support special column name for selected column
        String columnIdentifier = columnName;
        if ( columnName.equals(FixedColumns.Selected.getColumnName())) {
            columnIdentifier = selectionColumnName;
        }
        //handle the special stats column names
        String id = ContextProperties.isSummaryStatsProperty(columnIdentifier) ? ContextProperties.parseStatisticName(columnIdentifier) : columnIdentifier;
        newColumn.setHeaderValue(id);
    }


    private void addDynamicColumnIfRequired(String columnName) {
        int index = getColumnIndex(columnName);
        if ( index == -1 ) {
            //if it is not a predefined column which should already exist, then it must be a dynamic column
            tableModel.addDynamicColumn(columnName);
        }
    }

    private int getColumnIndex(String columnName) {
        int index = -1;
        for ( int col = 0 ; col < tableModel.getColumnCount(); col ++) {
            if ( tableModel.getColumnName(col).equals(columnName)) {
               index = col;
               break;
            }
        }
        return index;
    }

    private boolean existsInColumnModel(String columnName) {
        boolean result = false;
        for ( int col = 0; col < columnModel.getColumnCount(); col++) {
            TableColumn column = columnModel.getColumn(col);
            if ( tableModel.getColumnName(column.getModelIndex()).equals(columnName) ) {
                result = true;
                break;
            }
        }
        return result;
    }

    public String getColumnDescription(String columnName) {
        int index = getColumnIndex(columnName);
        return tableModel.getColumnDescription(index);
    }

    public void addAllDynamicColumns() {
        for ( int col = 0; col < tableModel.getColumnCount(); col ++ ) {
            if ( tableModel.isDynamicColumn(col)) {
                String colName = tableModel.getColumnName(col);
                if ( ! existsInColumnModel(colName)) {
                    addColumn(colName);
                }
            }
        }
    }

    private List<ColumnSettings> getDefaultColumnSettings() {
        List<ColumnSettings> columns = new ArrayList<ColumnSettings>();
        FixedColumns.addFixedColumn(columns, FixedColumns.Selected);
        FixedColumns.addFixedColumn(columns, FixedColumns.DisplayName);
        FixedColumns.addFixedColumn(columns, FixedColumns.Color);
        return columns;
    }
}
