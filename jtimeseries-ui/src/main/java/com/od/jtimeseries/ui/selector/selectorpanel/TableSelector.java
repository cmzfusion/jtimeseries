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
package com.od.jtimeseries.ui.selector.selectorpanel;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.ui.timeseries.RemoteChartingTimeSeries;
import com.od.swing.action.ListSelectionActionModel;
import com.od.jtimeseries.ui.util.PopupTriggerMouseAdapter;
import com.jidesoft.grid.BeanTableModel;
import com.jidesoft.grid.SortableTable;
import com.jidesoft.grid.AutoFilterTableHeader;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.beans.IntrospectionException;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 25-May-2009
 * Time: 11:37:55
 * To change this template use File | Settings | File Templates.
 */
public class TableSelector extends SelectorPanel {

    private static final Color STALE_SERIES_COLOR = new Color(248,165,169);
    private TimeSeriesContext rootContext;
    private java.util.List<Action> seriesActions;
    private RemoteSeriesTableModel tableModel;
    private SortableTable sortableTable;
    private JPopupMenu tablePopupMenu;

    public TableSelector(ListSelectionActionModel<RemoteChartingTimeSeries> seriesActionModel, TimeSeriesContext rootContext, java.util.List<Action> seriesActions) {
        super(seriesActionModel);
        this.rootContext = rootContext;
        this.seriesActions = seriesActions;

        tableModel = createTableModels();
        refreshSeries();
        createPopupMenu();
        createTable();

        sortableTable.setClearSelectionOnTableDataChanges(false);
        sortableTable.setRowResizable(true);
        sortableTable.setVariousRowHeights(true);
        sortableTable.setSelectInsertedRows(false);
        sortableTable.setClickCountToStart(2);
        sortableTable.setAutoSelectTextWhenStartsEditing(true);
        sortableTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        AutoFilterTableHeader _header = new AutoFilterTableHeader(sortableTable);
        _header.setAutoFilterEnabled(true);
        sortableTable.setTableHeader(_header);

        sortableTable.addMouseListener(new PopupTriggerMouseAdapter(tablePopupMenu, sortableTable));

        setLayout(new BorderLayout());
        add(new JScrollPane(sortableTable), BorderLayout.CENTER);
        addSeriesSelectionListener();
    }

    private void createPopupMenu() {
        tablePopupMenu = new JPopupMenu("Series Actions");
        for ( Action a : seriesActions) {
            tablePopupMenu.add(a);
        }
    }

    private void createTable() {
        sortableTable = new SortableTable(tableModel) {
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (! ((RemoteChartingTimeSeries) tableModel.getObject(row)).isConnected()) {
                    c.setBackground(STALE_SERIES_COLOR);
                } else {
                    if (isCellSelected(row, column)) {
                        c.setBackground(sortableTable.getSelectionBackground());
                    } else {
                        c.setBackground(sortableTable.getBackground());
                    }
                }
                return c;
            }
        };
    }

    private RemoteSeriesTableModel createTableModels() {
        BeanTableModel<RemoteChartingTimeSeries> model = null;
        try {
            model = new BeanTableModel<RemoteChartingTimeSeries>(
                new ArrayList(),
                RemoteChartingTimeSeries.class,
                new String[] {
                        "selected", "Selected",
                        "displayName", "Display Name",
                        "id", "Id",
                        "maxDaysHistory", "Max Days",
                        "refreshTimeSeconds", "Refresh Time (s)",
                        "contextPath", "Path",
                        "URL", "URL"
                }
            );
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        int[] editableCols = new int[] {0, 1, 3, 4};
        return new RemoteSeriesTableModel(model, editableCols);
    }

    private void addSeriesSelectionListener() {
        sortableTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        sortableTable.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if ( sortableTable.getSelectedRow() > -1 ) {
                        RemoteChartingTimeSeries series = (RemoteChartingTimeSeries)tableModel.getObject(sortableTable.getSelectedRow());
                        getSeriesActionModel().setSelected(series);
                        fireSelectedForDescription(series);
                    }
                }
            }
        );
    }

    public void refreshSeries() {
        tableModel.clearTable();
        tableModel.addRowData(rootContext.findAllTimeSeries().getAllMatches().toArray());
    }

    public void removeSeries(java.util.List<RemoteChartingTimeSeries> series) {
        tableModel.removeRowData(series.toArray(new RemoteChartingTimeSeries[series.size()]));
    }

}
