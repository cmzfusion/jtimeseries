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

import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
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

    boolean isDynamicColumn(int colIndex);

    String getColumnDescription(int colIndex);

    /**
     * Wrap the jide model as a BeanPerRowModel
     */
    public static class JideBeanModelWrapper<E> extends AbstractTableModel implements BeanPerRowModel<E>  {

        private static LogMethods logMethods = LogUtils.getLogMethods(JideBeanModelWrapper.class);

        private FixedColumnsBeanModel<E> wrappedModel;

        public JideBeanModelWrapper(FixedColumnsBeanModel<E> wrappedModel) {
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

        public void addObjects(List<E> beans) {
            wrappedModel.addObjects(beans);
        }

        public void removeObject(E s) {
            wrappedModel.removeObject(s);
        }

        public void addDynamicColumn(String columnName) {
            //this is the bottom level model
            //no decorator models managed to handle this - so we failed to add this dynamic column
            logMethods.error("Cannot add column " + columnName + " this column is not supported");
            throw new UnsupportedOperationException();
        }

        public boolean isDynamicColumn(int colIndex) {
            return false;
        }

        public String getColumnDescription(int colIndex) {
            return wrappedModel.getColumnDescription(colIndex);
        }


    }
}
