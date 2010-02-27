package com.od.jtimeseries.ui.selector.table;

import com.od.jtimeseries.ui.util.TableModelEventParser;
import com.od.jtimeseries.ui.util.TableEventDispatcher;

import javax.swing.table.AbstractTableModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 24-Feb-2010
 * Time: 17:53:38
 */
public abstract class DynamicColumnsTableModel<E> extends AbstractTableModel implements BeanPerRowModel<E>{

    private TableEventDispatcher eventDispatcher = new TableEventDispatcher(this);
    private BeanPerRowModel<E> wrappedModel;

    public DynamicColumnsTableModel(BeanPerRowModel<E> wrappedModel) {
        this.wrappedModel = wrappedModel;
        SeriesModelEventParserListener eventListener = new SeriesModelEventParserListener();
        TableModelEventParser eventParser = new TableModelEventParser(eventListener);
        wrappedModel.addTableModelListener(eventParser);
    }

    protected void initialize() {
        //initialize the maxPathElements
        requiresStructureChange();
    }

    private boolean requiresStructureChange() {
        return updateRequiresStructureChange(0, wrappedModel.getRowCount() - 1);
    }

    protected abstract boolean updateRequiresStructureChange(int firstRow, int lastRow);

    public E getObject(int row) {
        return wrappedModel.getObject(row);
    }

    public int getRowCount() {
        return wrappedModel.getRowCount();
    }

    public int getColumnCount() {
        return wrappedModel.getColumnCount() + getDynamicColumnCount();
    }

    public String getColumnName(int columnIndex) {
        if (isColumnInWrappedModel(columnIndex)) {
            return wrappedModel.getColumnName(columnIndex);
        } else {
            int extraColsIndex = getExtraColsIndex(columnIndex);
            return getDynamicColumnName(extraColsIndex);
        }
    }

    protected abstract String getDynamicColumnName(int extraColsIndex);

    public Class<?> getColumnClass(int columnIndex) {
        if ( isColumnInWrappedModel(columnIndex)) {
            return wrappedModel.getColumnClass(columnIndex);
        } else {
            int extraColsIndex = getExtraColsIndex(columnIndex);
            return getDynamicColumnClass(extraColsIndex);
        }
    }

    protected abstract Class<?> getDynamicColumnClass(int extraColsIndex);

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return wrappedModel.isCellEditable(rowIndex, columnIndex);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if ( isColumnInWrappedModel(columnIndex)) {
            return wrappedModel.getValueAt(rowIndex, columnIndex);
        } else {
            int extraColsIndex = getExtraColsIndex(columnIndex);
            return getValueForDynamicColumn(rowIndex, extraColsIndex);
        }
    }

    protected abstract Object getValueForDynamicColumn(int rowIndex, int extraColsIndex);


    public void addDynamicColumn(String columnName) {
        //handle this dynamic column if it should belong to this model, or delegate
        if ( isDynamicColumnInThisModel(columnName)) {
            doAddDynamicColumn(columnName);
        } else {
            wrappedModel.addDynamicColumn(columnName);
        }
    }

    protected abstract void doAddDynamicColumn(String columnName);

    protected abstract boolean isDynamicColumnInThisModel(String columnName);
    
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if ( isColumnInWrappedModel(columnIndex)) {
            wrappedModel.setValueAt(aValue, rowIndex, columnIndex);
        }
    }

    public void addTableModelListener(TableModelListener l) {
        eventDispatcher.addTableModelListener(l);
    }

    public void removeTableModelListener(TableModelListener l) {
        eventDispatcher.removeTableModelListener(l);
    }

    private int getExtraColsIndex(int columnIndex) {
        return columnIndex - wrappedModel.getColumnCount();
    }

    private boolean isColumnInWrappedModel(int columnIndex) {
        return columnIndex < wrappedModel.getColumnCount();
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

    public abstract int getDynamicColumnCount();

    //If the changes to wrapped table model data require more dynamic columns, fire a strucuture change,
    //otherwise propagate the event as is.
    //In general we do not ever remove columns dynamically, so delete rows just propagates
    protected class SeriesModelEventParserListener implements TableModelEventParser.TableModelEventParserListener {

        public void tableStructureChanged(TableModelEvent e) {
            initialize();
            eventDispatcher.fireTableStructureChanged();
        }

        public void tableDataChanged(TableModelEvent e) {
            boolean requiresStructureChange = requiresStructureChange();
            if ( requiresStructureChange ) {
                eventDispatcher.fireTableStructureChanged();
            } else {
                 eventDispatcher.fireTableDataChanged();
            }
        }

        public void tableRowsUpdated(int firstRow, int lastRow, TableModelEvent e) {
            boolean requiresStructureChange = updateRequiresStructureChange(firstRow, lastRow);
            if ( requiresStructureChange ) {
                eventDispatcher.fireTableStructureChanged();
            } else {
                eventDispatcher.fireTableRowsUpdated(firstRow, lastRow);
            }
        }

        public void tableCellsUpdated(int firstRow, int lastRow, int column, TableModelEvent e) {
            boolean requiresStructureChange = updateRequiresStructureChange(firstRow, lastRow);
            if ( requiresStructureChange ) {
                eventDispatcher.fireTableStructureChanged();
            } else {
                eventDispatcher.fireTableCellsUpdated(firstRow, lastRow, column);
            }
        }

        public void tableRowsDeleted(int firstRow, int lastRow, TableModelEvent e) {
            eventDispatcher.fireTableRowsDeleted(firstRow, lastRow);
        }

        public void tableRowsInserted(int firstRow, int lastRow, TableModelEvent e) {
            boolean requiresStructureChange = updateRequiresStructureChange(firstRow, lastRow);
            if ( requiresStructureChange ) {
                eventDispatcher.fireTableStructureChanged();
            } else {
                eventDispatcher.fireTableRowsInserted(firstRow, lastRow);
            }
        }
    }
}
