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
package com.od.jtimeseries.ui.selector.table;

import com.jidesoft.grid.ColorCellRenderer;
import com.od.jtimeseries.ui.config.ColumnSettings;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;

import javax.swing.table.TableCellRenderer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
* User: nick
* Date: 28-Feb-2010
* Time: 18:00:19
* To change this template use File | Settings | File Templates.
*
* Predefined static columns
*/
public enum FixedColumn {

    Selected(UIPropertiesTimeSeries.SELECTED_PROPERTY, "Selected", 25, true, "Select a series"),
    DisplayName(UIPropertiesTimeSeries.DISPLAY_NAME_PROPERTY, "Display Name", 175, true, "Name to use when displaying series in chart"),
    Id("id", "Id", 150, false, "Series ID"),
    RefreshFrequency(UIPropertiesTimeSeries.REFRESH_FREQUENCY_PROPERTY, "Refresh Frequency(s)", 120, true, "Frequency of series data refresh"),
    Path("path", "Path", 120, false, "Full path identifying series"),
    Url(UIPropertiesTimeSeries.URL_PROPERTY_NAME, "URL", 120, false, "URL for series data subscription"),
    LastRefreshTime(UIPropertiesTimeSeries.LAST_REFRESH_TIME_PROPERTY, "Last Refresh Time", 120, new TimeRenderer(), false, "Last series data refresh time"),
    StatsRefreshTime(UIPropertiesTimeSeries.STATS_REFRESH_TIME_PROPERTY, "Stats Refresh Time", 120, new TimeRenderer(), false, "Last refresh time for series stats metadata"),
    Color(UIPropertiesTimeSeries.COLOUR_PROPERTY, "Colour", 30, new ColorCellRenderer() {{setColorValueVisible(false);}}, true, "Colour to use for series in chart"),
    IsTicking(UIPropertiesTimeSeries.TICKING_PROPERTY, "Ticking", 30, false, "Is Ticking"),
    IsLoaded(UIPropertiesTimeSeries.LOADED_PROPERTY, "Loaded", 30, false, "Is Series Data Loaded"),
    IsLoading(UIPropertiesTimeSeries.LOADING_PROPERTY, "Loading", 30, false, "Is Series Query Running");

    private static Map<String, Integer> columnWidths = new HashMap<String,Integer>();
    static {
        for ( FixedColumn f : FixedColumn.values()) {
           columnWidths.put(f.getColumnName(), f.getDefaultWidth());
        }
    }

    private String propertyName;
    private String columnName;
    private int defaultWidth;
    private TableCellRenderer cellRenderer;
    private boolean isEditable;
    private String description;

    FixedColumn(String propertyName, String columnName, int defaultWidth, boolean isEditable, String description) {
        this(propertyName, columnName, defaultWidth, null, isEditable, description);
    }

    FixedColumn(String propertyName, String columnName, int defaultWidth, TableCellRenderer cellRenderer, boolean isEditable, String description) {
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
        for ( FixedColumn i : FixedColumn.values()) {
            if ( i.getColumnName().equals(columnName)) {
                result = i.getCellRenderer();
            }
        }
        return result;
    }

    public String getDescription() {
        return description;
    }

    public static void addFixedColumn(List<ColumnSettings> columns, FixedColumn fixedColumn) {
        columns.add(new ColumnSettings(fixedColumn.getColumnName(), fixedColumn.getDefaultWidth()));
    }

    public static List<FixedColumn> getDefaultVisualizerColumns() {
        return Arrays.asList(Selected, DisplayName, Color);
    }
}
