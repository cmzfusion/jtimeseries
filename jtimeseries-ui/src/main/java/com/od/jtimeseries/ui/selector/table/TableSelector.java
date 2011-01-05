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
import com.od.jtimeseries.ui.selector.shared.SelectorComponent;
import com.od.jtimeseries.ui.selector.shared.SelectorPopupMouseListener;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.swing.action.ListSelectionActionModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 25-May-2009
 * Time: 11:37:55
 * To change this template use File | Settings | File Templates.
 */
public class TableSelector<E extends UIPropertiesTimeSeries> extends SelectorComponent<E> {

    private TimeSeriesContext rootContext;
    private String selectionText;
    private Class<E> seriesClass;
    private BeanPerRowModel<E> tableModel;
    private SortableTable timeSeriesTable;
    private JPopupMenu tablePopupMenu;
    private TableColumnManager<E> tableColumnManager;
    private JToolBar toolbar = new JToolBar();

    public TableSelector(ListSelectionActionModel<E> seriesActionModel,
                         TimeSeriesContext rootContext,
                         String selectionText,
                         Class<E> seriesClass) {
        super(rootContext, seriesActionModel);
        this.rootContext = rootContext;
        this.selectionText = selectionText;
        this.seriesClass = seriesClass;
        createTable();
        createToolbar();
        setupSeries();
        createPopupMenu();

        setLayout(new BorderLayout());
        add(toolbar, BorderLayout.NORTH);
        add(new JScrollPane(timeSeriesTable), BorderLayout.CENTER);
        addSeriesSelectionListener();
    }

    private void createToolbar() {
        class ShowColumnSelectionDialogAction extends AbstractAction {

            public ShowColumnSelectionDialogAction() {
                putValue(NAME, "Select Columns");
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

    private Frame getFrameForComponent(Component parentComponent) throws HeadlessException {
        if (parentComponent instanceof Frame)  return (Frame)parentComponent;
        return getFrameForComponent(parentComponent.getParent());
    }

    @Override
    protected void addContextTreeListener() {
        rootContext.addTreeListener(
            new CoalescingTreeListener<E>(seriesClass, tableModel, this)
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
        timeSeriesTable.addMouseListener(
            new SelectorPopupMouseListener(timeSeriesTable, getSelectorActionFactory()) {

                protected List<Identifiable> getSelectedIdentifiable(MouseEvent mouseEvent) {
                    return new LinkedList<Identifiable>(getSeriesActionModel().getSelected());
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
        timeSeriesTable = new TimeSeriesTable<E>(tableModel, tableColumnManager);
    }

    private void addSeriesSelectionListener() {
        timeSeriesTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        timeSeriesTable.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if ( ! e.getValueIsAdjusting() && timeSeriesTable.getSelectedRow() > -1 ) {
                        int modelRow = TableModelWrapperUtils.getActualRowAt(timeSeriesTable.getModel(),timeSeriesTable.getSelectedRow());
                        E series = tableModel.getObject(modelRow);
                        getSeriesActionModel().setSelected(series);
                        fireSelectedForDescription(series);
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

    public void setSeriesSelectionEnabled(boolean selectable) {
        if (selectable) {
            tableColumnManager.addColumn(FixedColumns.Selected.getColumnName());
        } else {
            tableColumnManager.removeColumn(FixedColumns.Selected.getColumnName());
        }
    }
}
