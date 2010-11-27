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
    RefreshTimeSeconds("refreshTimeSeconds", "Refresh(s)", 120, true, "Frequency of series data refresh"),
    Path("path", "Path", 120, false, "Full path identifying series"),
    Url("timeSeriesURL", "URL", 120, false, "URL for series data subscription"),
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
