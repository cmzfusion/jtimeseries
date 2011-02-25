package com.od.jtimeseries.ui.selector.table;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import java.util.IdentityHashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 25/02/11
 * Time: 17:33
 */
public abstract class AbstractRowLookupTableModel<E> extends AbstractTableModel implements BeanPerRowModel<E>{

    private BeanPerRowModel<E> wrappedModel;

    private IdentityHashMap<E, Integer> rowsByBean = new IdentityHashMap<E, Integer>();
    private boolean initialized;

    public AbstractRowLookupTableModel(BeanPerRowModel<E> wrappedModel) {
        this.wrappedModel = wrappedModel;
        wrappedModel.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                fireTableChanged(
                    new TableModelEvent(AbstractRowLookupTableModel.this, e.getFirstRow(), e.getLastRow(), e.getColumn(), e.getType())
                );
            }
        });
    }

    public BeanPerRowModel<E> getWrappedModel() {
        return wrappedModel;
    }

    protected int getRow(E bean) {
        if ( ! initialized ) {
            initialize();
        }
        Integer result = rowsByBean.get(bean);
        return result == null ? -1 : result;
    }

    private void initialize() {
        rowsByBean.clear();
        for ( int row = 0; row < wrappedModel.getRowCount(); row++) {
            rowsByBean.put(wrappedModel.getObject(row), row);
        }
        initialized = true;
    }

    public E getObject(int row) {
        return wrappedModel.getObject(row);
    }

    public void clear() {
        rowsByBean.clear();
        initialized = true;
        wrappedModel.clear();
    }

    public void addObjects(List timeSeries) {
        initialized = false;
        wrappedModel.addObjects(timeSeries);
    }

    public void removeObject(E s) {
        initialized = false;
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
        return wrappedModel.isCellEditable(rowIndex, columnIndex);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return wrappedModel.getValueAt(rowIndex, columnIndex);
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        wrappedModel.setValueAt(aValue, rowIndex, columnIndex);
    }

    public void addTableModelListener(TableModelListener l) {
        wrappedModel.addTableModelListener(l);
    }

    public void removeTableModelListener(TableModelListener l) {
        wrappedModel.removeTableModelListener(l);
    }
}
