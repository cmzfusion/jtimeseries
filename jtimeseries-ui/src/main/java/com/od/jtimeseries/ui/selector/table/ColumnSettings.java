package com.od.jtimeseries.ui.selector.table;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 27-Feb-2010
 * Time: 22:57:14
 * To change this template use File | Settings | File Templates.
 */
public class ColumnSettings {

    private String columnName;
    private int columnWidth;

    //no params constructor required for bean xml persistence
    public ColumnSettings() {}

    public ColumnSettings(String columnName, int columnWidth) {
        this.columnName = columnName;
        this.columnWidth = columnWidth;
    }


    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public int getColumnWidth() {
        return columnWidth;
    }

    public void setColumnWidth(int columnWidth) {
        this.columnWidth = columnWidth;
    }
}
