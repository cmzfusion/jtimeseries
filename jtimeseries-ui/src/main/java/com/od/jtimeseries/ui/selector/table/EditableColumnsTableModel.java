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

import com.jidesoft.grid.ContextSensitiveTableModel;
import com.jidesoft.grid.EditorContext;
import com.jidesoft.grid.BooleanCheckBoxCellEditor;
import com.jidesoft.converter.ConverterContext;

import javax.swing.table.AbstractTableModel;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 21-Feb-2010
 * Time: 16:40:44
 * To change this template use File | Settings | File Templates.
 *
 * Just manage which columns are editable, and also support the jide ContextSensitiveTableModel
 */
public class EditableColumnsTableModel<E> extends AbstractTableModel implements BeanPerRowModel<E>, ContextSensitiveTableModel {

    private BeanPerRowModel<E> wrappedModel;
    private FixedColumns[] fixedColumns = FixedColumns.values();

    public EditableColumnsTableModel(BeanPerRowModel<E> wrappedModel) {
        this.wrappedModel = wrappedModel;
        wrappedModel.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                fireTableChanged(
                    new TableModelEvent(EditableColumnsTableModel.this, e.getFirstRow(), e.getLastRow(), e.getColumn(), e.getType())
                );
            }
        });
    }

    public int getRowCount() {
        return wrappedModel.getRowCount();
    }

    public int getColumnCount() {
        return wrappedModel.getColumnCount();
    }

    public String getColumnName(int columnIndex) {
        return wrappedModel.getColumnName(columnIndex);
    }

    public Class<?> getColumnClass(int columnIndex) {
        return wrappedModel.getColumnClass(columnIndex);
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        boolean result = false;
        if ( fixedColumns.length > columnIndex ) {
            result = fixedColumns[columnIndex].isEditable();
        }
        return result;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return wrappedModel.getValueAt(rowIndex, columnIndex);
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        wrappedModel.setValueAt(aValue, rowIndex, columnIndex);
    }

    public ConverterContext getConverterContextAt(int i, int i1) {
        return null;
    }

    public EditorContext getEditorContextAt(int rowIndex, int columnIndex) {
        return getColumnClass(columnIndex) == Boolean.class ? BooleanCheckBoxCellEditor.CONTEXT : null;
    }

    public Class<?> getCellClassAt(int rowIndex, int columnIndex) {
        return getColumnClass(columnIndex);
    }

    public E getObject(int row) {
        return wrappedModel.getObject(row);
    }

    public void clear() {
        wrappedModel.clear();
    }

    public void addObjects(List<E> timeSeries) {
        wrappedModel.addObjects(timeSeries);
    }

    public void removeObject(E s) {
        wrappedModel.removeObject(s);
    }

    public void addDynamicColumn(String columnName) {
        wrappedModel.addDynamicColumn(columnName);
    }

    public boolean isDynamicColumn(int colIndex) {
        return wrappedModel.isDynamicColumn(colIndex);
    }

    public String getColumnDescription(int colIndex) {
        return wrappedModel.getColumnDescription(colIndex);
    }

}
