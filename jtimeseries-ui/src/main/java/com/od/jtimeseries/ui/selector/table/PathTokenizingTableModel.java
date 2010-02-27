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

import com.od.jtimeseries.ui.timeseries.RemoteChartingTimeSeries;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 25-May-2009
 * Time: 21:20:57
 * To change this template use File | Settings | File Templates.
 *
 * A dynamic columns table model to show the elements of the context path as additional columns
 */
public class PathTokenizingTableModel extends DynamicColumnsTableModel<RemoteChartingTimeSeries> {

    private int maxPathElements;

    public PathTokenizingTableModel(BeanPerRowModel<RemoteChartingTimeSeries> wrappedModel) {
        super(wrappedModel);
        initialize();
    }

    protected boolean updateRequiresStructureChange(int firstRow, int lastRow) {
        int oldMax = maxPathElements;
        for ( int row = firstRow; row <= lastRow; row++) {
            RemoteChartingTimeSeries s = getObject(row);
            maxPathElements = Math.max(maxPathElements, s.getPathElements().size());
        }
        return oldMax != maxPathElements;
    }

    protected Object getValueForDynamicColumn(int rowIndex, int extraColsIndex) {
        List<String> pathElements = (getObject(rowIndex)).getPathElements();
        return pathElements.size() > extraColsIndex ?  pathElements.get(extraColsIndex) : null;
    }

    protected void doAddDynamicColumn(String columnName) {
        int elementNumber = Integer.parseInt(columnName.substring(4));
        if ( maxPathElements < elementNumber ) {
            maxPathElements = elementNumber;
            fireTableStructureChanged();
        }
    }

    protected boolean isDynamicColumnInThisModel(String columnName) {
        return columnName.matches("path\\d+");
    }

    public int getDynamicColumnCount() {
        return maxPathElements;
    }

    protected String getDynamicColumnName(int extraColsIndex) {
        return String.valueOf("path " + extraColsIndex);
    }

    protected Class<?> getDynamicColumnClass(int extraColsIndex) {
        return String.class;
    }
}
