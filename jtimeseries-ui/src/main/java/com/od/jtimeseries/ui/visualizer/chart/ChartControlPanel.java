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
        showLegendCheckbox = new JCheckBox("Legend");
        showLegendCheckbox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                timeSeriesChart.setShowLegend(showLegendCheckbox.isSelected());
            }
        });
    }

    private void createColorCombo() {
        colorComboBox.setPreferredSize(new Dimension(65, colorComboBox.getPreferredSize().height));
        colorComboBox.setMaximumSize(colorComboBox.getPreferredSize());
        colorComboBox.setColorValueVisible(false);
        colorComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                timeSeriesChart.setChartBackgroundColor(colorComboBox.getSelectedColor());
            }
        });
    }

    private void layoutPanel() {
        Box b = Box.createHorizontalBox();
        b.add(Box.createHorizontalGlue());

        JComponent displayControls = createDisplayControls();
        b.add(createWidgetBox(displayControls, "Display", true));
        addSpacing(b);
        b.add(createWidgetBox(rangeModeCombo, "Range", true));
        addSpacing(b);
        b.add(createWidgetBox(timeSelector, "Time", true));
        addSpacing(b);
        b.add(createWidgetBox(dataFilterCombo, "Filters", true));
        //b.add(Box.createHorizontalGlue());

        setPreferredSize(new Dimension(getPreferredSize().width, getPreferredSize().height + 30));
        setLayout(new BorderLayout());
        add(b, BorderLayout.CENTER);
    }

    private JComponent createDisplayControls() {
        Box b = Box.createHorizontalBox();
        b.add(colorComboBox);
        addSpacing(b);
        b.add(showLegendCheckbox);
        addSpacing(b);
        b.add(chartTypeCombo);
        return b;
    }

    private JComponent createWidgetBox(JComponent widget, String title, boolean addLowerBorder) {
        Box b = Box.createVerticalBox();
        b.add(Box.createVerticalGlue());
        b.add(widget);
        if ( addLowerBorder) {
            b.add(Box.createVerticalStrut(5));
        }

        TitledBorder border = new TitledBorder(title);
        border.setTitleFont(border.getTitleFont().deriveFont(9f));
        b.setBorder(border);
        b.setMaximumSize(b.getPreferredSize());
        return b;
    }

    private void addSpacing(Box b) {
        b.add(Box.createRigidArea(new Dimension(20, 0)));
    }

}
