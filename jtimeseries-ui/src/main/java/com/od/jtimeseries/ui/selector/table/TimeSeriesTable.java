package com.od.jtimeseries.ui.selector.table;

import com.jidesoft.grid.SortableTable;
import com.jidesoft.grid.AutoFilterTableHeader;
import com.od.jtimeseries.ui.timeseries.RemoteChartingTimeSeries;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
* User: nick
* Date: 27-Feb-2010
* Time: 15:57:14
* To change this template use File | Settings | File Templates.
*/
class TimeSeriesTable extends SortableTable {

    private static final Color STALE_SERIES_COLOR = new Color(248,165,169);
    private BeanPerRowModel<RemoteChartingTimeSeries> tableModel;
    private TableColumnManager columnManager;

    public TimeSeriesTable(BeanPerRowModel<RemoteChartingTimeSeries> tableModel, TableColumnManager columnManager) {
        super(tableModel);
        setColumnModel(columnManager.getColumnModel());
        this.tableModel = tableModel;
        this.columnManager = columnManager;
        setClearSelectionOnTableDataChanges(false);
        setRowResizable(true);
        setVariousRowHeights(true);
        setSelectInsertedRows(false);
        setAutoSelectTextWhenStartsEditing(true);
        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setRowResizable(false);
        AutoFilterTableHeader header = new AutoFilterTableHeader(this);
        header.setAutoFilterEnabled(true);
        header.setShowFilterName(true);
        header.setAllowMultipleValues(true);
        header.setShowFilterNameAsToolTip(true);
        setTableHeader(header);
        setModel(tableModel);
    }

    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);
        if (tableModel.getObject(row).isSeriesStale()) {
            c.setBackground(STALE_SERIES_COLOR);
        } else {
            if (isCellSelected(row, column)) {
                c.setBackground(getSelectionBackground());
            } else {
                c.setBackground(getBackground());
            }
        }
        return c;
    }

    public void createDefaultColumnsFromModel() {
        if (columnManager != null) {
            columnManager.tableModelStructureChanged();
        }
    }
}
