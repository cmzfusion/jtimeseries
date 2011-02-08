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
import com.od.jtimeseries.ui.selector.shared.IdentifiableListActionModel;
import com.od.jtimeseries.ui.selector.shared.SelectorActionFactory;
import com.od.jtimeseries.ui.selector.shared.SelectorComponent;
import com.od.jtimeseries.ui.selector.table.ColumnSettings;
import com.od.jtimeseries.ui.selector.table.TableSelector;
import com.od.jtimeseries.ui.selector.tree.TreeSelector;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.identifiable.IdentifiableTreeEvent;
import com.od.jtimeseries.util.identifiable.IdentifiableTreeListener;
import com.od.swing.action.ActionModelListener;
import com.od.swing.util.AwtSafeListener;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 06-Jan-2009
 * Time: 17:25:36
 */
public class SeriesSelectionPanel<E extends UIPropertiesTimeSeries> extends JPanel implements SelectionManager<E> {

    public static final String TREE_VIEW_SELECTED_PROPERTY = "treeViewSelected";
    private static final int WIDTH = 250;
    private TimeSeriesContext context;
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
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

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
        this.selectionListForCharting = new SeriesSelectionList<E>();
        setupTimeseries();
        selectionActionModel = new IdentifiableListActionModel();
        treeSelector = new TreeSelector<E>(selectionActionModel, context, seriesClass);
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

    public void setSelectorActionFactory(SelectorActionFactory selectorActionFactory) {
        treeSelector.setSelectorActionFactory(selectorActionFactory);
        tableSelector.setSelectorActionFactory(selectorActionFactory);
    }

    public void setTransferHandler(TransferHandler h) {
        treeSelector.setTransferHandler(h);
        tableSelector.setTransferHandler(h);
    }

    public IdentifiableListActionModel getSelectionActionModel() {
        return selectionActionModel;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

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

        context.addTreeListener(
                AwtSafeListener.getAwtSafeListener(
                        new IdentifiableTreeListener() {

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
                        },
                        IdentifiableTreeListener.class
                )
        );
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

    public void setColumns(List<ColumnSettings> columnSettings) {
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

}
