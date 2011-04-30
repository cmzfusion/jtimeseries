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

import com.jidesoft.grid.SortableTable;
import com.jidesoft.grid.TableModelWrapperUtils;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.ui.config.ColumnSettings;
import com.od.jtimeseries.ui.selector.shared.IdentifiableListActionModel;
import com.od.jtimeseries.ui.selector.shared.NoImportsSelectorTransferHandler;
import com.od.jtimeseries.ui.selector.shared.RightClickSelectionPopupListener;
import com.od.jtimeseries.ui.selector.shared.SelectorComponent;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.jtimeseries.ui.visualizer.AbstractUIRootContext;
import com.od.jtimeseries.util.identifiable.Identifiable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 25-May-2009
 * Time: 11:37:55
 * To change this template use File | Settings | File Templates.
 */
public class TableSelector<E extends UIPropertiesTimeSeries> extends SelectorComponent<E> {

    private AbstractUIRootContext rootContext;
    private String selectionText;
    private Class<E> seriesClass;
    private BeanPerRowModel<E> tableModel;
    private SortableTable table;
    private TableColumnManager<E> tableColumnManager;
    private JToolBar toolbar = new JToolBar();

    public TableSelector(IdentifiableListActionModel selectionsActionModel,
                         AbstractUIRootContext rootContext,
                         String selectionText,
                         Class<E> seriesClass) {
        super(rootContext, selectionsActionModel);
        this.rootContext = rootContext;
        this.selectionText = selectionText;
        this.seriesClass = seriesClass;
        createTable();
        createToolbar();
        setupSeries();
        createPopupMenu();
        setupDragAndDrop();

        setLayout(new BorderLayout());
        add(toolbar, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        addSeriesSelectionListener();
    }

    private void createToolbar() {
        class ShowColumnSelectionDialogAction extends AbstractAction {

            public ShowColumnSelectionDialogAction() {
                putValue(NAME, "Select Columns");
                putValue(SMALL_ICON, ImageUtils.TABLE_COLUMN_ADD_16x16);
                putValue(SHORT_DESCRIPTION, "Select Columns");
            }

            public void actionPerformed(ActionEvent e) {
                showColumnSelectionDialog();
            }
        }

        toolbar.add(new ShowColumnSelectionDialogAction());
    }

    private void showColumnSelectionDialog() {
        ColumnSelectionDialog d = new ColumnSelectionDialog(getFrameForComponent(this), this, getTableColumnManager());
        d.setVisible(true);
        d.dispose();
    }

    private void setupDragAndDrop() {
        table.setDragEnabled(true);
        table.setDropMode(DropMode.ON);
        setTransferHandler(new NoImportsSelectorTransferHandler(rootContext, getSelectionsActionModel()));
    }

    public void setTransferHandler(TransferHandler h) {
        super.setTransferHandler(h); //if we drop beneath the table rows
        table.setTransferHandler(h);
    }

    private Frame getFrameForComponent(Component parentComponent) throws HeadlessException {
        if (parentComponent instanceof Frame)  return (Frame)parentComponent;
        return getFrameForComponent(parentComponent.getParent());
    }

    @Override
    protected void addContextTreeListener() {
        rootContext.addTreeListener(
            new CoalescingTableUpdater<E>(seriesClass, tableModel, this)
        );
    }

    private TableColumnManager getTableColumnManager() {
        return tableColumnManager;
    }

    public void setColumns(List<ColumnSettings> columnSettings) {
        tableColumnManager.setColumns(columnSettings);
    }

    public List<ColumnSettings> getColumnSettings() {
        return tableColumnManager.getColumnSettings();
    }

    private void createPopupMenu() {
        table.addMouseListener(
            new RightClickSelectionPopupListener(this, table) {

                protected void setSelectedItemsOnPopupTrigger(MouseEvent e) {
                    int row = table.rowAtPoint(e.getPoint());
                    boolean selected = table.getSelectionModel().isSelectedIndex(row);
                    if ( ! selected ) {
                        table.getSelectionModel().setSelectionInterval(row, row);
                    }
                }

                protected List<Identifiable> getSelectedIdentifiable(MouseEvent mouseEvent) {
                    return new LinkedList<Identifiable>(getSelectionsActionModel().getSelected());
                }

                protected SelectorComponent getSelectorComponent() {
                    return TableSelector.this;
                }
            }
        );
    }

    private void createTable() {
        tableModel = new TableModelCreator().createTableModel(seriesClass);
        tableColumnManager = new TableColumnManager<E>(tableModel, selectionText);
        table = new TimeSeriesTable<E>(tableModel, tableColumnManager);
    }

    private void addSeriesSelectionListener() {
        table.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        table.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if ( ! e.getValueIsAdjusting() ) {
                        if ( table.getSelectedRow() > -1 ) {
                            int[] selectedRows = table.getSelectedRows();
                            List<Identifiable> selectedSeries = new LinkedList<Identifiable>();
                            for (int selectedRow : selectedRows) {
                                int modelRow = TableModelWrapperUtils.getActualRowAt(table.getModel(),selectedRow);
                                selectedSeries.add(tableModel.getObject(modelRow));
                            }
                            getSelectionsActionModel().setSelected(selectedSeries);
                        } else {
                            getSelectionsActionModel().clearActionModelState();
                        }
                    }
                }
            }
        );
    }

    @Override
    protected void buildView() {
        List<E> l = rootContext.findAll(seriesClass).getAllMatches();
        tableModel.addObjects(l);
    }

    public void addAllDynamicColumns() {
        tableColumnManager.addAllDynamicColumns();    
    }

    public void showSelections(List<Identifiable> selected) {
        Set<Identifiable> idInThisContext = convertToIdentifiableInThisContext(selected);
        List<Integer> rows = findRows(idInThisContext);

        int minRow = Integer.MAX_VALUE;
        table.getSelectionModel().clearSelection();
        for ( Integer r : rows) {
            table.getSelectionModel().addSelectionInterval(r, r);
            minRow = Math.min(minRow, r);
        }

        if ( rows.size() > 0) {
            table.scrollRowToVisible(minRow);
        }
    }

    private List<Integer> findRows(Set<Identifiable> idInThisContext) {
        List<Integer> rows = new ArrayList<Integer>();
        for ( int row = 0; row < tableModel.getRowCount(); row ++) {
            if (idInThisContext.contains(tableModel.getObject(row))) {
                rows.add(row);
            }
        }
        return rows;
    }

    public void setSeriesSelectionEnabled(boolean selectable) {
        if (selectable) {
            tableColumnManager.addColumn(FixedColumn.Selected.getColumnName());
        } else {
            tableColumnManager.removeColumn(FixedColumn.Selected.getColumnName());
        }
    }
}
