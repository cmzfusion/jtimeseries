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

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.combobox.AbstractComboBox;
import com.jidesoft.combobox.ColorComboBox;
import com.od.jtimeseries.ui.config.ChartRangeMode;
import com.od.jtimeseries.ui.config.DomainTimeSelection;
import com.od.jtimeseries.ui.visualizer.chart.creator.ChartDataFilter;
import com.od.jtimeseries.ui.visualizer.chart.creator.ChartType;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by IntelliJ IDEA.
* User: nick
* Date: 26-Feb-2010
* Time: 23:25:15
* To change this template use File | Settings | File Templates.
*/
public class ChartControlPanel extends JPanel {

    private TimeSeriesChart timeSeriesChart;
    private ColorComboBox colorComboBox = new ColorComboBox();
    private JCheckBox showLegendCheckbox = new JCheckBox("Show Legend");
    private DomainModeComponent timeSelector = new DomainModeComponent();
    private JComboBox chartTypeCombo = new JComboBox(ChartType.values());
    private JComboBox dataFilterCombo = new JComboBox(ChartDataFilter.values());
    private JComboBox rangeModeCombo = new JComboBox(ChartRangeMode.values());

    public ChartControlPanel(TimeSeriesChart timeSeriesChart) {
        this.timeSeriesChart = timeSeriesChart;
        createColorCombo();
        createShowLegendCheckbox();
        createChartRangeModeCombo();
        createChartTypeCombo();
        createChartDataFilterCombo();
        layoutPanel();
        setComponentSizesRecursive(this);
        refreshStateFromChart();
        addRangeSelectorListener();
    }

    private void createChartTypeCombo() {
        chartTypeCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                timeSeriesChart.setChartType((ChartType) chartTypeCombo.getSelectedItem());
            }
        });
    }

    private void createChartDataFilterCombo() {
        dataFilterCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                timeSeriesChart.setChartDataFilter((ChartDataFilter) dataFilterCombo.getSelectedItem());
            }
        });
    }

    private void createChartRangeModeCombo() {
        rangeModeCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ChartControlPanel.this.timeSeriesChart.setChartRangeMode((ChartRangeMode) rangeModeCombo.getSelectedItem());
            }
        });
    }

    private void setComponentSizesRecursive(JComponent parent) {
        for ( Component c : parent.getComponents()) {
            if ( c instanceof AbstractComboBox || c instanceof JTextField) {
                c.setMaximumSize(c.getPreferredSize());
                c.setMinimumSize(c.getPreferredSize());
            } else if ( c instanceof JComponent) {
                setComponentSizesRecursive((JComponent) c);
            }
        }
    }

    public void refreshStateFromChart() {
        updateDomainSelection();
        showLegendCheckbox.setSelected(timeSeriesChart.isShowLegend());
        colorComboBox.setSelectedColor(timeSeriesChart.getChartBackgroundColor());
        chartTypeCombo.setSelectedItem(timeSeriesChart.getChartType());
        dataFilterCombo.setSelectedItem(timeSeriesChart.getChartDataFilter());
        rangeModeCombo.setSelectedItem(timeSeriesChart.getChartRangeMode());
    }

    private void updateDomainSelection() {
        DomainTimeSelection d = timeSeriesChart.getDomainStartTimeSelection();
        timeSelector.setDomainSelection(d);
    }

    private void addRangeSelectorListener() {
        timeSelector.addPropertyChangeListener(
            DomainModeComponent.DOMAIN_SELECTION_PROPERTY,
            new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    timeSeriesChart.setDomainStartTimeSelection((DomainTimeSelection) evt.getNewValue());
                }
            }
        );
    }

    private void createShowLegendCheckbox() {
        showLegendCheckbox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                timeSeriesChart.setShowLegend(showLegendCheckbox.isSelected());
            }
        });
    }

    private void createColorCombo() {
        colorComboBox.setColorValueVisible(false);
        colorComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                timeSeriesChart.setChartBackgroundColor(colorComboBox.getSelectedColor());
            }
        });
    }

    private void layoutPanel() {

        FormLayout l = new FormLayout("10px, pref:grow, 10px", "10px, pref, 5px, pref, 5px, pref, 5px, pref, 10px:grow");
        CellConstraints cc = new CellConstraints();

        JComponent d = createDisplayControls();
        JComponent rangeControls = createWidgetBox(rangeModeCombo, "Range");
        JComponent displayControls = createWidgetBox(d, "Display");
        JComponent timeControls = createWidgetBox(timeSelector, "Time");
        JComponent filterControls = createWidgetBox(dataFilterCombo, "Filters");

        setLayout(l);
        add(rangeControls, cc.xy(2, 2));
        add(displayControls, cc.xy(2, 4));
        add(timeControls, cc.xy(2, 6));
        add(filterControls, cc.xy(2, 8));
    }

    private JComponent createDisplayControls() {
        Box b = Box.createVerticalBox();
        addLeftAligned(b, colorComboBox);
        addVerticalSpacing(b);
        addLeftAligned(b, showLegendCheckbox);
        addVerticalSpacing(b);
        addLeftAligned(b, chartTypeCombo);
        return b;
    }

    private void addLeftAligned(Box b, JComponent component) {
        Box box = Box.createHorizontalBox();
        box.add(component);
        box.add(Box.createHorizontalGlue());
        b.add(box);
    }

    private JComponent createWidgetBox(JComponent widget, String title) {
        TitledBorder border = new TitledBorder(title);
        border.setTitleFont(border.getTitleFont().deriveFont(9f));
        Box b = Box.createHorizontalBox();
        b.add(widget);
        b.add(Box.createHorizontalGlue());
        b.setBorder(border);
        return b;
    }

    private void addSpacing(Box b) {
        b.add(Box.createRigidArea(new Dimension(20, 0)));
    }

    private void addVerticalSpacing(Box b) {
        b.add(Box.createRigidArea(new Dimension(0, 20)));
    }

}
