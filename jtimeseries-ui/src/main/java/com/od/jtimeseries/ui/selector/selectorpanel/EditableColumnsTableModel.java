package com.od.jtimeseries.ui.selector.selectorpanel;

import com.jidesoft.grid.ContextSensitiveTableModel;
import com.jidesoft.grid.EditorContext;
import com.jidesoft.grid.BooleanCheckBoxCellEditor;
import com.jidesoft.converter.ConverterContext;

import javax.swing.table.TableModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 21-Feb-2010
 * Time: 16:40:44
 * To change this template use File | Settings | File Templates.
 *
 * Just manage which columns are editable, and also support the jide ContextSensitiveTableModel
 */
public class EditableColumnsTableModel extends AbstractTableModel implements TableModel, ContextSensitiveTableModel {

    private TableModel wrappedModel;
    private int[] editableColumnIndexes;

    public EditableColumnsTableModel(TableModel wrappedModel, int[] editableColumnIndexes) {
        this.wrappedModel = wrappedModel;
        this.editableColumnIndexes = editableColumnIndexes;
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
        for (int col : editableColumnIndexes) {
            if ( col == columnIndex) {
                result = true;
                break;
            }
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

}
