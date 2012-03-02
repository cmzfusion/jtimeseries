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
package com.od.jtimeseries.ui.visualizer.chart;

import com.od.jtimeseries.ui.config.ChartDomainMode;
import com.od.jtimeseries.ui.config.DomainTimeSelection;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.NumberFormat;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 28/01/11
 * Time: 06:47
 *
 * Select how much history to show in time series charts
 */
public class DomainModeComponent extends JPanel {

    public static final String DOMAIN_SELECTION_PROPERTY = "domainSelection";

    private JComboBox rangeModeCombo = new JComboBox(ChartDomainMode.values());
    private JFormattedTextField multipleTextField = new JFormattedTextField(NumberFormat.getIntegerInstance());
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private DomainTimeSelection domainSelection = new DomainTimeSelection();

    public DomainModeComponent() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        multipleTextField.setColumns(3);
        showCurrentSelection();
        add(multipleTextField);
        add(rangeModeCombo);
        addListeners();
    }

    private void showCurrentSelection() {
        multipleTextField.setText(String.valueOf(domainSelection.getMultiple()));
        rangeModeCombo.setSelectedItem(domainSelection.getMode());
        multipleTextField.setEnabled(domainSelection.getMode() != ChartDomainMode.ALL);
    }

    private void addListeners() {
        multipleTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Integer newMultiple = null;
                try {
                    newMultiple = Integer.valueOf(multipleTextField.getValue().toString());
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }
                if ( newMultiple != null ) {
                    setDomainSelection(new DomainTimeSelection(
                        domainSelection.getMode(),
                        newMultiple
                    ));
                }
            }
        });

        rangeModeCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ChartDomainMode selectedMode = (ChartDomainMode) rangeModeCombo.getSelectedItem();
                setDomainSelection(new DomainTimeSelection(
                        selectedMode,
                        domainSelection.getMultiple()
                ));
            }
        });
    }

    public DomainTimeSelection getDomainSelection() {
        return domainSelection;
    }

    public void setDomainSelection(DomainTimeSelection domainSelection) {
        DomainTimeSelection oldSelection = this.domainSelection;
        this.domainSelection = domainSelection;
        if (!oldSelection.equals(domainSelection)) {
            showCurrentSelection();
            propertyChangeSupport.firePropertyChange(DOMAIN_SELECTION_PROPERTY, oldSelection, domainSelection);
        }
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
    }
}
