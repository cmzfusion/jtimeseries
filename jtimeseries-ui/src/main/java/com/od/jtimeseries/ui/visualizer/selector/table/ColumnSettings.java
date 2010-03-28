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
package com.od.jtimeseries.ui.visualizer.selector.table;

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
