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
        FormLayout l = new FormLayout(
            "10px, pref:grow, 10px",
            "10px, pref, 5px, pref, 5px, pref, 5px, pref, 10px:grow"
        );
        setLayout(l);
        CellConstraints cc = new CellConstraints();
        add(createWidgetBox(createDisplayControls(), "Display"), cc.xy(2, 2));
        add(createWidgetBox(rangeModeCombo, "Range"), cc.xy(2, 4));
        add(createWidgetBox(timeSelector, "Time"), cc.xy(2, 6));
        add(createWidgetBox(dataFilterCombo, "Filters"), cc.xy(2, 8));
    }

    private JComponent createDisplayControls() {
        FormLayout f = new FormLayout("pref:grow", "pref, 10px, pref, 10px, pref, 10px");
        JPanel p = new JPanel();
        p.setLayout(f);
        CellConstraints cc = new CellConstraints();
        p.add(colorComboBox, cc.xy(1, 1));
        p.add(chartTypeCombo, cc.xy(1, 3));
        p.add(showLegendCheckbox, cc.xy(1, 5));
        return p;
    }

    private JComponent createWidgetBox(JComponent widget, String title) {

        FormLayout f = new FormLayout("10px, max(175px;pref), 10px:grow", "5px,pref,5px");
        JPanel p = new JPanel();
        p.setLayout(f);
        CellConstraints cc = new CellConstraints();
        p.add(widget, cc.xy(2, 2));

        TitledBorder border = new TitledBorder(title);
        border.setTitleFont(border.getTitleFont());
        p.setBorder(border);
        return p;
    }

}
