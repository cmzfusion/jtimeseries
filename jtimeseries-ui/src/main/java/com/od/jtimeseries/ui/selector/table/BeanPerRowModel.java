package com.od.jtimeseries.ui.selector.table;

import com.jidesoft.grid.BeanTableModel;
import com.od.jtimeseries.ui.timeseries.RemoteChartingTimeSeries;

import javax.swing.table.TableModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 24-Feb-2010
 * Time: 18:04:58
 */
public interface BeanPerRowModel<E> extends TableModel {

    E getObject(int row);

    void clear();

    void addObjects(List<E> timeSeries);

    void removeObject(E s);

    void addDynamicColumn(String columnName);

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
            //this is the bottom level model
            //no decorator models managed to handle this - so we failed to add this dynamic column
            throw new UnsupportedOperationException();
        }

    }
}
