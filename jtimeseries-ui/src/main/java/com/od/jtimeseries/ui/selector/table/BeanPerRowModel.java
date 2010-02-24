package com.od.jtimeseries.ui.selector.table;

import com.jidesoft.grid.BeanTableModel;

import javax.swing.table.TableModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 24-Feb-2010
 * Time: 18:04:58
 */
public interface BeanPerRowModel<E> extends TableModel {

    E getObject(int row);


    /**
     * Wrap the jide model as a BeanPerRowModel
     */
    public static class JideBeanModelWrapper<E> extends AbstractTableModel implements BeanPerRowModel<E>  {

        private BeanTableModel<E> wrappedModel;

        public JideBeanModelWrapper(BeanTableModel<E> wrappedModel) {
            this.wrappedModel = wrappedModel;

            //interpose this model as the source for events
            wrappedModel.addTableModelListener(new TableModelListener() {
                public void tableChanged(TableModelEvent e) {
                    fireTableChanged(new TableModelEvent(JideBeanModelWrapper.this, e.getFirstRow(), e.getLastRow(), e.getColumn(), e.getType()));
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
            return wrappedModel.isCellEditable(rowIndex, columnIndex);
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            return wrappedModel.getValueAt(rowIndex, columnIndex);
        }

        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            wrappedModel.setValueAt(aValue, rowIndex, columnIndex);
        }

        public E getObject(int row) {
            return wrappedModel.getObject(row);
        }
    }
}
