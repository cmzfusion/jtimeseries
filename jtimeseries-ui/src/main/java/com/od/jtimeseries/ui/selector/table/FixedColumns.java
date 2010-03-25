package com.od.jtimeseries.ui.selector.table;

import com.jidesoft.grid.ColorCellRenderer;

import javax.swing.table.TableCellRenderer;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
* User: nick
* Date: 28-Feb-2010
* Time: 18:00:19
* To change this template use File | Settings | File Templates.
*
* Predefined static columns
*/
public enum FixedColumns {

    Selected("selected", "Selected", 110, true, "Select a series"),
    DisplayName("displayName", "Display Name", 175, true, "Name to use when displaying series in chart"),
    Id("id", "Id", 150, false, "Series ID"),
    MaxDaysHistory("maxDaysHistory", "Max Days", 120, true, "Maximum number of days of data to display in chart"),
    RefreshTimeSeconds("refreshTimeSeconds", "Refresh(s)", 120, true, "Frequency of series data refresh"),
    Path("path", "Path", 120, false, "Full path identifying series"),
    Url("URL", "URL", 120, false, "URL for series data subscription"),
    LastRefreshTime("lastRefreshTime", "Last Refresh", 120, new TimeRenderer(), false, "Last series data refresh time"),
    Color("color", "Colour", 110, new ColorCellRenderer() {{setColorValueVisible(false);}}, true, "Colour to use for series in chart");

    private static Map<String, Integer> columnWidths = new HashMap<String,Integer>();
    static {
        for ( FixedColumns f : FixedColumns.values()) {
           columnWidths.put(f.getColumnName(), f.getDefaultWidth());
        }
    }

    private String propertyName;
    private String columnName;
    private int defaultWidth;
    private TableCellRenderer cellRenderer;
    private boolean isEditable;
    private String description;

    FixedColumns(String propertyName, String columnName, int defaultWidth, boolean isEditable, String description) {
        this(propertyName, columnName, defaultWidth, null, isEditable, description);
    }

    FixedColumns(String propertyName, String columnName, int defaultWidth, TableCellRenderer cellRenderer, boolean isEditable, String description) {
        this.propertyName = propertyName;
        this.columnName = columnName;
        this.defaultWidth = defaultWidth;
        this.cellRenderer = cellRenderer;
        this.isEditable = isEditable;
        this.description = description;
    }

    public TableCellRenderer getCellRenderer() {
        return cellRenderer;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getColumnName() {
        return columnName;
    }

    public int getDefaultWidth() {
        return defaultWidth;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public static int getDefaultColumnWidth(String columnName) {
        Integer i = columnWidths.get(columnName);
        return i == null ? 75 : i;
    }

    public static TableCellRenderer getCellRenderer(String columnName) {
        TableCellRenderer result = null;
        for ( FixedColumns i : FixedColumns.values()) {
            if ( i.getColumnName().equals(columnName)) {
                result = i.getCellRenderer();
            }
        }
        return result;
    }

    public String getDescription() {
        return description;
    }

    public static void addFixedColumn(List<ColumnSettings> columns, FixedColumns fixedColumn) {
        columns.add(new ColumnSettings(fixedColumn.getColumnName(), fixedColumn.getDefaultWidth()));
    }
}
