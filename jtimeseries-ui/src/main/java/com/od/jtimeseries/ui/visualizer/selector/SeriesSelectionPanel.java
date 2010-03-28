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
package com.od.jtimeseries.ui.visualizer.selector;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.ui.visualizer.selector.shared.*;
import com.od.jtimeseries.ui.visualizer.selector.tree.TreeSelector;
import com.od.jtimeseries.ui.visualizer.selector.table.TableSelector;
import com.od.jtimeseries.ui.visualizer.selector.table.ColumnSelectionDialog;
import com.od.jtimeseries.ui.visualizer.selector.table.ColumnSettings;
import com.od.jtimeseries.ui.timeseries.RemoteChartingTimeSeries;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.swing.action.ListSelectionActionModel;
import com.od.swing.action.ModelDrivenAction;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 06-Jan-2009
 * Time: 17:25:36
 */
public class SeriesSelectionPanel extends JPanel implements SelectionManager {

    private static final int WIDTH = 250;
    private TimeSeriesContext rootContext;
    private SeriesSelectionList selectionList;
    private SeriesDescriptionPanel seriesDescriptionPanel = new SeriesDescriptionPanel();
    private JRadioButton useTreeRadio = new JRadioButton("Tree", true);
    private JRadioButton useTableRadio = new JRadioButton("Table");
    private JButton columnSelectorButton = new JButton("Columns");
    private TreeSelector treeSelector;
    private TableSelector tableSelector;
    private JPanel selectorPanel;
    private Box titleBox;
    private CardLayout cardLayout;
    private DiscriptionListener descriptionSettingSelectorListener = new DiscriptionListener();
    private List<RemoteChartingTimeSeries> timeSeries = new ArrayList<RemoteChartingTimeSeries>();
    private ListSelectionActionModel<RemoteChartingTimeSeries> seriesSelectionModel = new ListSelectionActionModel<RemoteChartingTimeSeries>();
    private RemoveSeriesAction removeSeriesAction = new RemoveSeriesAction(seriesSelectionModel);
    private ReconnectSeriesAction reconnectSeriesAction = new ReconnectSeriesAction(seriesSelectionModel);
    private PropertyChangeListener selectionPropertyListener = new SelectedSeriesPropertyChangeListener();
    private PropertyChangeListener seriesConnectionPropertyListener = new SeriesConnectionPropertyChangeListener();

    public SeriesSelectionPanel(TimeSeriesContext rootContext) {
        this(rootContext, "Selected");
    }

    /**
     * @param rootContext
     * @param selectionText, text name for boolean 'selected' column (e.g. if the selected series will be charted, this might be 'Chart')
     */
    public SeriesSelectionPanel(TimeSeriesContext rootContext, String selectionText) {
        this.rootContext = rootContext;
        this.selectionList = new SeriesSelectionList();
        setupTimeseries(rootContext.findAllTimeSeries().getAllMatches());

        List<Action> seriesActions = Arrays.asList(new Action[]{removeSeriesAction, reconnectSeriesAction});
        treeSelector = new TreeSelector(seriesSelectionModel, rootContext, seriesActions);
        tableSelector = new TableSelector(seriesSelectionModel, rootContext, seriesActions, selectionText);
        createSelectorPanel();
        createTitlePanel();
        addComponents();
        addListeners();
    }

    private void createTitlePanel() {
        titleBox = Box.createHorizontalBox();
        titleBox.add(new JLabel("Series Selector"));
        titleBox.add(Box.createHorizontalGlue());

        ActionListener radioListener = new RadioButtonSelectionListener();
        useTreeRadio.addActionListener(radioListener);
        useTableRadio.addActionListener(radioListener);

        columnSelectorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showColumnSelectionDialog();
            }
        });

        ButtonGroup group = new ButtonGroup();
        group.add(useTreeRadio);
        group.add(useTableRadio);
        titleBox.add(useTreeRadio);
        titleBox.add(useTableRadio);
        titleBox.add(Box.createHorizontalStrut(10));
        titleBox.add(columnSelectorButton);
    }

    private void showColumnSelectionDialog() {
        ColumnSelectionDialog d = new ColumnSelectionDialog(getFrameForComponent(this), this, tableSelector.getTableColumnManager());
        d.setVisible(true);
        d.dispose();
    }

    private Frame getFrameForComponent(Component parentComponent) throws HeadlessException {
        if (parentComponent instanceof Frame)  return (Frame)parentComponent;
        return getFrameForComponent(parentComponent.getParent());
    }

    private void createSelectorPanel() {
        cardLayout = new CardLayout();
        selectorPanel = new JPanel(cardLayout);
        selectorPanel.add(treeSelector, "tree");
        selectorPanel.add(tableSelector, "table");
    }

    private void addListeners() {
        treeSelector.addSelectorListener(descriptionSettingSelectorListener);
        tableSelector.addSelectorListener(descriptionSettingSelectorListener);
    }

    private void addComponents() {
        seriesDescriptionPanel.setPreferredSize(new Dimension(WIDTH, 110));
        setLayout(new BorderLayout());
        add(titleBox,BorderLayout.NORTH);
        add(selectorPanel, BorderLayout.CENTER);
        add(seriesDescriptionPanel, BorderLayout.SOUTH);
        setBorder(new EmptyBorder(5,5,5,5));
    }

    public SeriesSelectionList getSelectionList() {
        return selectionList;
    }

    public List<RemoteChartingTimeSeries> getSelectedTimeSeries() {
        return selectionList.getSelectedTimeSeries();
    }

    public void addSelectionListener(TimeSeriesSelectorListener l) {
        selectionList.addSelectionListener(l);
    }

    public void removeSelectionListener(TimeSeriesSelectorListener l) {
        selectionList.removeSelectionListener(l);
    }

    public void addSelection(RemoteChartingTimeSeries s) {
        selectionList.addSelection(s);
    }

    public void removeSelection(RemoteChartingTimeSeries s) {
        selectionList.removeSelection(s);
    }

    public void setSelectedTimeSeries(List<RemoteChartingTimeSeries> selections) {
        selectionList.setSelectedTimeSeries(selections);
    }

    public void setColumns(List<ColumnSettings> columnSettings) {
        tableSelector.setColumns(columnSettings);
    }

    public List<ColumnSettings> getColumnSettings() {
        return tableSelector.getColumnSettings();
    }

    public void refresh() {
        setupTimeseries(rootContext.findAllTimeSeries().getAllMatches());
        treeSelector.refreshSeries();
        tableSelector.refreshSeries();
    }

    public void addAllDynamicColumns() {
        tableSelector.addAllDynamicColumns();
    }

    private void setupTimeseries(List<IdentifiableTimeSeries> l) {
        removePropertyListenerFromCurrentSeries();
        addPropertyListenerToNewSeries(l);
        updateSelections(l);
    }

    private void updateSelections(List<IdentifiableTimeSeries> l) {
        List<RemoteChartingTimeSeries> selections = new ArrayList<RemoteChartingTimeSeries>();
        for ( IdentifiableTimeSeries s : l) {
            RemoteChartingTimeSeries r = (RemoteChartingTimeSeries)s;
            if ( r.isSelected() ) {
                selections.add(r);
            }
        }
        selectionList.setSelectedTimeSeries(selections);
    }

    private void addPropertyListenerToNewSeries(List<IdentifiableTimeSeries> l) {
        for ( IdentifiableTimeSeries s : l) {
            RemoteChartingTimeSeries r = (RemoteChartingTimeSeries)s;
            r.addPropertyChangeListener(
                    RemoteChartingTimeSeries.SELECTED_PROPERTY,
                    selectionPropertyListener
            );

            r.addPropertyChangeListener(
                    RemoteChartingTimeSeries.SERIES_STALE_PROPERTY,
                    seriesConnectionPropertyListener
            );
        }
    }

    private void removePropertyListenerFromCurrentSeries() {
        for ( RemoteChartingTimeSeries s : timeSeries ) {
            s.removePropertyChangeListener(selectionPropertyListener);
            s.removePropertyChangeListener(seriesConnectionPropertyListener);
        }
    }

    private class RadioButtonSelectionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if ( useTreeRadio.isSelected()) {
                SeriesSelectionPanel.this.showTree();
                columnSelectorButton.setEnabled(false);
            } else {
                SeriesSelectionPanel.this.showTable();
                columnSelectorButton.setEnabled(true);
            }
        }
    }

    public boolean isTableSelectorVisible() {
        return useTableRadio.isSelected();
    }

    public void setTableSelectorVisible(boolean isVisible) {
        if (isVisible) {
            showTable();
        } else {
            showTree();
        }
    }
    
    public void showTable() {
        useTableRadio.setSelected(true);
        cardLayout.show(selectorPanel, "table");
    }

    public void showTree() {
        useTreeRadio.setSelected(true);
        cardLayout.show(selectorPanel, "tree");
    }

    private class DiscriptionListener extends SelectorPanel.SelectorPanelListenerAdapter {

        public void seriesSelectedForDescription(IdentifiableTimeSeries s) {
            seriesDescriptionPanel.setSelectedSeries(s);
        }
    }

    private class SelectedSeriesPropertyChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            RemoteChartingTimeSeries s = (RemoteChartingTimeSeries)evt.getSource();
            if (s.isSelected()) {
                selectionList.addSelection(s);
            } else {
                selectionList.removeSelection(s);
            }
        }
    }

    private class SeriesConnectionPropertyChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            tableSelector.repaint();
        }
    }

    public class RemoveSeriesAction extends ModelDrivenAction<ListSelectionActionModel<RemoteChartingTimeSeries>> {

        public RemoveSeriesAction(ListSelectionActionModel<RemoteChartingTimeSeries> seriesSelectionModel) {
            super(seriesSelectionModel, "Remove Series", ImageUtils.REMOVE_ICON_16x16);
        }

        public void actionPerformed(ActionEvent e) {
            List<RemoteChartingTimeSeries> series = getActionModel().getSelected();
            for ( RemoteChartingTimeSeries s : series) {
                TimeSeriesContext c = (TimeSeriesContext)s.getParent();
                s.setSelected(false);
                c.removeChild(s);
            }
            treeSelector.removeSeries(series);
            tableSelector.removeSeries(series);
        }
    }

    public class ReconnectSeriesAction extends ModelDrivenAction<ListSelectionActionModel<RemoteChartingTimeSeries>> {

        public ReconnectSeriesAction(ListSelectionActionModel<RemoteChartingTimeSeries> seriesSelectionModel) {
            super(seriesSelectionModel, "Reconnect Time Series to Server", ImageUtils.CONNECT_ICON_16x16);
        }

        public void actionPerformed(ActionEvent e) {
            List<RemoteChartingTimeSeries> series = getActionModel().getSelected();
            for ( RemoteChartingTimeSeries s : series) {
                if ( s.isSeriesStale()) {
                    s.setSeriesStale(false);
                }
            }
            repaint();
        }

        protected boolean isModelStateActionable() {
            for ( RemoteChartingTimeSeries s : getActionModel().getSelected()) {
                if (s.isSeriesStale() ) {
                    return true;
                }
            }
            return false;
        }

    }
}
