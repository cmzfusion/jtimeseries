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
package com.od.jtimeseries.ui.selector;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.ui.selector.shared.SelectorPanel;
import com.od.jtimeseries.ui.selector.table.ColumnSelectionDialog;
import com.od.jtimeseries.ui.selector.table.ColumnSettings;
import com.od.jtimeseries.ui.selector.table.TableSelector;
import com.od.jtimeseries.ui.selector.tree.TreeSelector;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
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
public class SeriesSelectionPanel<E extends UIPropertiesTimeSeries> extends JPanel implements SelectionManager<E> {

    private static final int WIDTH = 250;
    private TimeSeriesContext context;
    private Class<E> seriesClass;
    private SeriesSelectionList<E> selectionList;
    private SeriesDescriptionPanel seriesDescriptionPanel = new SeriesDescriptionPanel();
    private JRadioButton useTreeRadio = new JRadioButton("Tree", true);
    private JRadioButton useTableRadio = new JRadioButton("Table");
    private JButton columnSelectorButton = new JButton("Columns");
    private TreeSelector<E> treeSelector;
    private TableSelector<E> tableSelector;
    private JPanel selectorPanel;
    private Box titleBox;
    private CardLayout cardLayout;
    private DescriptionListener descriptionSettingSelectorListener = new DescriptionListener();
    private List<E> timeSeries = new ArrayList<E>();
    private ListSelectionActionModel<E> seriesSelectionModel = new ListSelectionActionModel<E>();
    private RemoveSeriesAction removeSeriesAction = new RemoveSeriesAction(seriesSelectionModel);
    private ReconnectSeriesAction reconnectSeriesAction = new ReconnectSeriesAction(seriesSelectionModel);
    private PropertyChangeListener selectionPropertyListener = new SelectedSeriesPropertyChangeListener();
    private PropertyChangeListener seriesConnectionPropertyListener = new SeriesConnectionPropertyChangeListener();

    public SeriesSelectionPanel(TimeSeriesContext context, Class seriesClass) {
        this(context, "Selected", seriesClass);
    }

    /**
     * Create a time series selector which allows the user to select time series in the supplied context using a tree or table view
     * Selection listeners may be added, or the getSelectedTimeSeries() method invoked to get the current selections
     *
     * @param selectionText, text name for boolean 'selected' column (e.g. if the selected series will be charted, this might be 'Chart')
     */
    public SeriesSelectionPanel(TimeSeriesContext context, String selectionText, Class<E> seriesClass) {
        this.context = context;
        this.seriesClass = seriesClass;
        this.selectionList = new SeriesSelectionList<E>();
        setupTimeseries(context.findAll(seriesClass).getAllMatches());

        List<Action> seriesActions = Arrays.asList(new Action[]{removeSeriesAction, reconnectSeriesAction});
        treeSelector = new TreeSelector<E>(seriesSelectionModel, context, seriesActions);
        tableSelector = new TableSelector<E>(seriesSelectionModel, context, seriesActions, selectionText, seriesClass);
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

    public List<E> getSelectedTimeSeries() {
        return selectionList.getSelectedTimeSeries();
    }

    public void addSelectionListener(TimeSeriesSelectorListener<E> l) {
        selectionList.addSelectionListener(l);
    }

    public void removeSelectionListener(TimeSeriesSelectorListener<E> l) {
        selectionList.removeSelectionListener(l);
    }

    public void addSelection(E s) {
        selectionList.addSelection(s);
    }

    public void removeSelection(E s) {
        selectionList.removeSelection(s);
    }

    public void setSelectedTimeSeries(List<E> selections) {
        selectionList.setSelectedTimeSeries(selections);
    }

    public void setColumns(List<ColumnSettings> columnSettings) {
        tableSelector.setColumns(columnSettings);
    }

    public List<ColumnSettings> getColumnSettings() {
        return tableSelector.getColumnSettings();
    }

    public void refresh() {
        setupTimeseries(context.findAll(seriesClass).getAllMatches());
        treeSelector.refreshSeries();
        tableSelector.refreshSeries();
    }

    public void addAllDynamicColumns() {
        tableSelector.addAllDynamicColumns();
    }

    private void setupTimeseries(List<E> l) {
        removePropertyListenerFromCurrentSeries();
        addPropertyListenerToNewSeries(l);
        updateSelections(l);
    }

    private void updateSelections(List<E> l) {
        List<E> selections = new ArrayList<E>();
        for ( E s : l) {
            if ( s.isSelected() ) {
                selections.add(s);
            }
        }
        selectionList.setSelectedTimeSeries(selections);
    }

    private void addPropertyListenerToNewSeries(List<E> l) {
        for ( E s : l) {
            s.addPropertyChangeListener(
                    UIPropertiesTimeSeries.SELECTED_PROPERTY,
                    selectionPropertyListener
            );

            s.addPropertyChangeListener(
                    UIPropertiesTimeSeries.STALE_PROPERTY,
                    seriesConnectionPropertyListener
            );
        }
    }

    private void removePropertyListenerFromCurrentSeries() {
        for ( E s : timeSeries ) {
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

    private class DescriptionListener<E extends UIPropertiesTimeSeries> extends SelectorPanel.SelectorPanelListenerAdapter<E> {

        public void seriesSelectedForDescription(E s) {
            seriesDescriptionPanel.setSelectedSeries(s);
        }
    }

    private class SelectedSeriesPropertyChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            E s = (E)evt.getSource();
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

    public class RemoveSeriesAction extends ModelDrivenAction<ListSelectionActionModel<E>> {

        public RemoveSeriesAction(ListSelectionActionModel<E> seriesSelectionModel) {
            super(seriesSelectionModel, "Remove Series", ImageUtils.REMOVE_ICON_16x16);
        }

        public void actionPerformed(ActionEvent e) {
            List<E> series = getActionModel().getSelected();
            for ( E s : series) {
                TimeSeriesContext c = (TimeSeriesContext)s.getParent();
                s.setSelected(false);
                c.removeChild(s);
            }
            treeSelector.removeSeries(series);
            tableSelector.removeSeries(series);
        }
    }

    public class ReconnectSeriesAction extends ModelDrivenAction<ListSelectionActionModel<E>> {

        public ReconnectSeriesAction(ListSelectionActionModel<E> seriesSelectionModel) {
            super(seriesSelectionModel, "Reconnect Time Series to Server", ImageUtils.CONNECT_ICON_16x16);
        }

        public void actionPerformed(ActionEvent e) {
            List<E> series = getActionModel().getSelected();
            for ( E s : series) {
               if ( s.isStale()) {
                   s.setStale(false);
               }
            }
            repaint();
        }

        protected boolean isModelStateActionable() {
            for ( E s : getActionModel().getSelected()) {
                if (s.isStale() ) {
                    return true;
                }
            }
            return false;
        }

    }
}