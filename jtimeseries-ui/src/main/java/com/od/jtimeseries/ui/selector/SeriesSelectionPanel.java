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
package com.od.jtimeseries.ui.selector;

import com.od.jtimeseries.identifiable.Identifiable;
import com.od.jtimeseries.identifiable.IdentifiableTreeEvent;
import com.od.jtimeseries.identifiable.IdentifiableTreeListener;
import com.od.jtimeseries.ui.config.ColumnSettings;
import com.od.jtimeseries.ui.selector.shared.SelectorComponent;
import com.od.jtimeseries.ui.selector.shared.SelectorPopupMenuPopulator;
import com.od.jtimeseries.ui.selector.table.TableSelector;
import com.od.jtimeseries.ui.selector.tree.SelectorTreeNodeFactory;
import com.od.jtimeseries.ui.selector.tree.TreeSelector;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.uicontext.AbstractUIRootContext;
import com.od.jtimeseries.ui.uicontext.IdentifiableListActionModel;
import com.od.swing.action.ActionModelListener;
import com.od.swing.util.AwtSafeListener;
import com.od.swing.weakreferencelistener.WeakReferenceListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 06-Jan-2009
 * Time: 17:25:36
 *
 * A component which allows selection of timeseries / identifiables via a tree or table
 *
 * Two forms of 'selection' are supported:
 *
 * 1. A basic ability to select/highlight items in the tree or table and right click to perform actions is always
 * active.
 * 2. Additionally, checkboxes may be rendered which allow timeseries to be selected (for charting, for example) into
 * a separate selectionListForCharting - this feature may be enabled or disabled according to the purpose of the selector
 */
public class SeriesSelectionPanel<E extends UIPropertiesTimeSeries> extends JPanel implements SelectionManager<E> {

    public static final String TREE_VIEW_SELECTED_PROPERTY = "treeViewSelected";
    private static final int WIDTH = 250;
    private AbstractUIRootContext context;
    private Class<E> seriesClass;
    private SeriesSelectionList<E> selectionListForCharting;
    private SeriesDescriptionPanel seriesDescriptionPanel = new SeriesDescriptionPanel();
    private JRadioButton useTreeRadio = new JRadioButton("Tree", true);
    private JRadioButton useTableRadio = new JRadioButton("Table");
    private TreeSelector<E> treeSelector;
    private TableSelector<E> tableSelector;
    private JPanel selectorPanel;
    private Box titleBox;
    private CardLayout cardLayout;
    private DescriptionListener descriptionSettingSelectorListener = new DescriptionListener();
    private IdentifiableListActionModel selectionActionModel;
    private IdentifiableTreeListener updateSelectedForChartingContextListener;

    public SeriesSelectionPanel(AbstractUIRootContext context, Class seriesClass) {
        this(context, "Selected", new SelectorTreeNodeFactory<E>(seriesClass), seriesClass);
    }

    public SeriesSelectionPanel(AbstractUIRootContext context, Class seriesClass, SelectorTreeNodeFactory<E> treeNodeFactory) {
        this(context, "Selected", treeNodeFactory, seriesClass);
    }

    public SeriesSelectionPanel(AbstractUIRootContext rootContext, String chart, Class<E> seriesClass) {
        this(rootContext, chart, new SelectorTreeNodeFactory<E>(seriesClass), seriesClass);
    }

    /**
     * Create a time series selector which allows the user to select time series in the supplied context using a tree or table view
     * Selection listeners may be added, or the getSelectedTimeSeries() method invoked to get the current selections
     *
     * @param selectionText, text name for boolean 'selected' column (e.g. if the selected series will be charted, this might be 'Chart')
     */
    public SeriesSelectionPanel(AbstractUIRootContext context, String selectionText, SelectorTreeNodeFactory treeNodeFactory, Class<E> seriesClass) {
        this.context = context;
        this.seriesClass = seriesClass;
        this.selectionListForCharting = new SeriesSelectionList<E>();
        setupTimeseries();
        selectionActionModel = new IdentifiableListActionModel();
        treeSelector = new TreeSelector<E>(selectionActionModel, context, treeNodeFactory);
        tableSelector = new TableSelector<E>(selectionActionModel, context, selectionText, seriesClass);
        createSelectorPanel();
        createTitlePanel();
        addComponents();
        addListeners();
        showTree();
    }


    public void setSeriesSelectionEnabled(boolean selectable) {
        treeSelector.setSeriesSelectionEnabled(selectable);
        tableSelector.setSeriesSelectionEnabled(selectable);
    }

    public void setSelectorActionFactory(SelectorPopupMenuPopulator selectorPopupMenuPopulator) {
        treeSelector.setPopupMenuPopulator(selectorPopupMenuPopulator);
        tableSelector.setPopupMenuPopulator(selectorPopupMenuPopulator);
    }

    public void setTreeComparator(Comparator<Identifiable> treeComparator) {
        treeSelector.setTreeComparator(treeComparator);
    }

    public void setTransferHandler(TransferHandler h) {
        treeSelector.setTransferHandler(h);
        tableSelector.setTransferHandler(h);
    }

    public void showSelections(List<Identifiable> selected) {
        treeSelector.showSelections(selected);
        tableSelector.showSelections(selected);
    }

    public IdentifiableListActionModel getSelectionActionModel() {
        return selectionActionModel;
    }

//    public void addPropertyChangeListener(PropertyChangeListener listener) {
//        propertyChangeSupport.addPropertyChangeListener(listener);
//    }
//
//    public void removePropertyChangeListener(PropertyChangeListener listener) {
//        propertyChangeSupport.removePropertyChangeListener(listener);
//    }

    private void createTitlePanel() {
        titleBox = Box.createHorizontalBox();
        titleBox.add(new JLabel("Series Selector"));
        titleBox.add(Box.createHorizontalGlue());

        ActionListener radioListener = new RadioButtonSelectionListener();
        useTreeRadio.addActionListener(radioListener);
        useTableRadio.addActionListener(radioListener);

        ButtonGroup group = new ButtonGroup();
        group.add(useTreeRadio);
        group.add(useTableRadio);
        titleBox.add(useTreeRadio);
        titleBox.add(useTableRadio);
        titleBox.add(Box.createHorizontalStrut(10));
    }

    private void createSelectorPanel() {
        cardLayout = new CardLayout();
        selectorPanel = new JPanel(cardLayout);
        selectorPanel.add(treeSelector, "tree");
        selectorPanel.add(tableSelector, "table");
    }

    private void addListeners() {

        getSelectionActionModel().addActionModelListener(descriptionSettingSelectorListener);

        updateSelectedForChartingContextListener = AwtSafeListener.getAwtSafeListener(
                new UpdateSelectedForChartingTreeListener(),
                IdentifiableTreeListener.class
        );
        WeakReferenceListener w = new WeakReferenceListener(updateSelectedForChartingContextListener);
        w.addListenerTo(context);
    }

    private void addComponents() {
        seriesDescriptionPanel.setPreferredSize(new Dimension(WIDTH, 110));
        setLayout(new BorderLayout());
        add(titleBox,BorderLayout.NORTH);
        add(selectorPanel, BorderLayout.CENTER);
        add(seriesDescriptionPanel, BorderLayout.SOUTH);
        setBorder(new EmptyBorder(5, 5, 5, 5));
    }

    public SeriesSelectionList getSelectionList() {
        return selectionListForCharting;
    }

    public List<E> getSelectedTimeSeries() {
        return selectionListForCharting.getSelectedTimeSeries();
    }

    public void addSelectionListener(TimeSeriesSelectorListener<E> l) {
        selectionListForCharting.addSelectionListener(l);
    }

    public void removeSelectionListener(TimeSeriesSelectorListener<E> l) {
        selectionListForCharting.removeSelectionListener(l);
    }

    public void addSelection(E s) {
        selectionListForCharting.addSelection(s);
    }

    public void removeSelection(E s) {
        selectionListForCharting.removeSelection(s);
    }

    public void setSelectedTimeSeries(List<E> selections) {
        selectionListForCharting.setSelectedTimeSeries(selections);
    }

    public void setColumnSettings(List<ColumnSettings> columnSettings) {
        tableSelector.setColumns(columnSettings);
    }

    public List<ColumnSettings> getColumnSettings() {
        return tableSelector.getColumnSettings();
    }

    private List<E> getAllTimeSeriesFromContext() {
        return context.findAll(seriesClass).getAllMatches();
    }

    public void addAllDynamicColumns() {
        tableSelector.addAllDynamicColumns();
    }

    private void setupTimeseries() {
        List<E> series = getAllTimeSeriesFromContext();
        updateSelections(series);
    }

    private void updateSelections(List<E> l) {
        List<E> selections = new ArrayList<E>();
        for ( E s : l) {
            if ( s.isSelected() ) {
                selections.add(s);
            }
        }
        selectionListForCharting.setSelectedTimeSeries(selections);
    }

    private class RadioButtonSelectionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if ( useTreeRadio.isSelected()) {
                SeriesSelectionPanel.this.showTree();
            } else {
                SeriesSelectionPanel.this.showTable();
            }
            firePropertyChange(TREE_VIEW_SELECTED_PROPERTY, ! useTreeRadio.isSelected(), useTreeRadio.isSelected());
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

    private class DescriptionListener implements ActionModelListener {

        public void actionStateUpdated() {
            if ( selectionActionModel.isModelValid() ) {
                Identifiable i = selectionActionModel.getSelected().get(0);
                if ( seriesClass.isAssignableFrom(i.getClass())) {
                    seriesDescriptionPanel.setSelectedSeries((UIPropertiesTimeSeries)i);
                }
            }
        }
    }

    /**
     * When items are removed from the identifiable tree, we need to adjust
     * the charting selections if items are removed or added with their selected status set true
     */
    private class UpdateSelectedForChartingTreeListener implements IdentifiableTreeListener {

        public void nodeChanged(Identifiable node, Object changeDescription) {
        }

        public void descendantChanged(IdentifiableTreeEvent contextTreeEvent) {
            List<E> seriesAffected = SelectorComponent.getAffectedSeries(seriesClass, contextTreeEvent, false);
            modifySelectedForCharting(seriesAffected);
        }

        public void descendantAdded(IdentifiableTreeEvent contextTreeEvent) {
            List<E> seriesAffected = SelectorComponent.getAffectedSeries(seriesClass, contextTreeEvent, true);
            modifySelectedForCharting(seriesAffected);
        }

        private void modifySelectedForCharting(List<E> seriesAffected) {
            for (E series : seriesAffected) {
                if (series.isSelected()) {
                    selectionListForCharting.addSelection(series);
                } else {
                    selectionListForCharting.removeSelection(series);
                }
            }
        }

        public void descendantRemoved(IdentifiableTreeEvent contextTreeEvent) {
            //remove those series selected for charting
            List<E> seriesAffected = SelectorComponent.getAffectedSeries(seriesClass, contextTreeEvent, true);
            for (E series : seriesAffected) {
                selectionListForCharting.removeSelection(series);
            }

            //remove those identifiable selected/highlighted in tree/table
//                                List<Identifiable> allIdentifiable = SelectorComponent.getAffectedSeries(Identifiable.class, contextTreeEvent, true);
//                                for ( Identifiable i : allIdentifiable) {
//                                    selectionActionModel.removeSelected(i);
//                                }
        }
    }
}
