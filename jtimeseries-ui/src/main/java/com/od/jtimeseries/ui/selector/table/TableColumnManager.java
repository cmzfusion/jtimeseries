package com.od.jtimeseries.ui.selector.table;

import com.od.jtimeseries.context.ContextProperties;
import com.od.jtimeseries.ui.timeseries.RemoteChartingTimeSeries;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableCellRenderer;
import java.util.ArrayList;
import java.util.Enumeration;
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
    private DefaultColumnSettings defaultColumnSettings;

    public TableColumnManager(BeanPerRowModel<RemoteChartingTimeSeries> tableModel, DefaultColumnSettings defaultColumnSettings, String selectionColumnName) {
        this.tableModel = tableModel;
        this.defaultColumnSettings = defaultColumnSettings;
        addColumn(selectionColumnName);
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


    public List<ColumnSettings> getColumns() {
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
            defaultColumnSettings.getDefaultColumnWidth(columnName)
        );
        TableCellRenderer r = defaultColumnSettings.getCellRenderer(columnName);
        if ( r != null ) {
            newColumn.setCellRenderer(r);
        }
        setColumnIdentifier(columnName, newColumn);
        return newColumn;
    }


    private void setColumnIdentifier(String columnName, TableColumn newColumn) {
        //handle the special stats column names
        String id = ContextProperties.isSummaryStatsProperty(columnName) ? ContextProperties.parseStatisticName(columnName) : columnName;
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

    //we don't currently persist users changes to column order/sizes, but perhaps we should..
    private void sizeColumns(TableColumnModel m) {
        Enumeration<TableColumn> e = m.getColumns();
        while(e.hasMoreElements()) {
            TableColumn col = e.nextElement();
            String name = tableModel.getColumnName(col.getModelIndex());
            col.setPreferredWidth(defaultColumnSettings.getDefaultColumnWidth(name));
        }
    }

    public static interface DefaultColumnSettings {

        int getDefaultColumnWidth(String columnName);

        TableCellRenderer getCellRenderer(String columnName);
    }
}
