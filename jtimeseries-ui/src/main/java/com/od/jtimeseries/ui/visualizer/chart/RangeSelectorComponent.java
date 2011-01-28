package com.od.jtimeseries.ui.visualizer.chart;

import javax.swing.*;
import java.awt.*;
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
 */
public class RangeSelectorComponent extends JPanel {

    public static final String DOMAIN_SELECTION_PROPERTY = "domainSelection";

    private JComboBox rangeModeList = new JComboBox();
    private JFormattedTextField multipleTextField = new JFormattedTextField(NumberFormat.getIntegerInstance());
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private DomainSelection domainSelection = new DomainSelection();

    public RangeSelectorComponent() {
        setLayout(new FlowLayout());
        rangeModeList.setModel(new DefaultComboBoxModel(ChartDomainMode.values()));
        multipleTextField.setColumns(3);
        showCurrentSelection();

        add(multipleTextField);
        add(rangeModeList);
        addListeners();
    }

    private void showCurrentSelection() {
        multipleTextField.setText(String.valueOf(domainSelection.getMultiple()));
        rangeModeList.setSelectedItem(domainSelection.getMode());
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
                    setDomainSelection(new DomainSelection(
                        domainSelection.getMode(),
                        newMultiple
                    ));
                }
            }
        });

        rangeModeList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ChartDomainMode selectedMode = (ChartDomainMode) rangeModeList.getSelectedItem();
                setDomainSelection(new DomainSelection(
                        selectedMode,
                        domainSelection.getMultiple()
                ));
            }
        });
    }

    public DomainSelection getDomainSelection() {
        return domainSelection;
    }

    public void setDomainSelection(DomainSelection domainSelection) {
        DomainSelection oldSelection = this.domainSelection;
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
