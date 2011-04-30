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
package com.od.jtimeseries.ui.download.panel;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.ui.config.ColumnSettings;
import com.od.jtimeseries.ui.selector.SeriesSelectionPanel;
import com.od.jtimeseries.ui.selector.table.FixedColumn;
import com.od.jtimeseries.ui.timeseries.ServerTimeSeries;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.jtimeseries.ui.visualizer.AbstractUIRootContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 24-May-2009
 * Time: 22:33:56
 * To change this template use File | Settings | File Templates.
 */
public class ChooseSeriesPanel extends AbstractDownloadWizardPanel {

    private SeriesSelectionPanel<ServerTimeSeries> seriesSelectionPanel;

    public ChooseSeriesPanel(WizardPanelListener panelListener, AbstractUIRootContext timeSeriesContext) {
        super(panelListener);
        Box titlePanel = createTitlePanel("Select series to import (" + timeSeriesContext.findAllTimeSeries().getNumberOfMatches() + " series found)");
        JComponent seriesSelector = createSeriesSelector(timeSeriesContext);
        Box buttonPanel = createButtonPanel();

        setLayout(new BorderLayout());
        add(titlePanel, BorderLayout.NORTH);
        add(seriesSelector, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private Box createButtonPanel() {
        Box buttonBox = Box.createHorizontalBox();
        buttonBox.add(Box.createHorizontalGlue());

        JButton addButton = new JButton("Add Selected Series");
        addButton.setIcon(ImageUtils.DOWNLOAD_16x16);
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getPanelListener().seriesSelected(seriesSelectionPanel.getSelectedTimeSeries());
            }
        });
        buttonBox.add(addButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setIcon(ImageUtils.CANCEL_16x16);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getPanelListener().downloadCancelled();
            }
        });
        buttonBox.add(cancelButton);
        return buttonBox;
    }

    private JComponent createSeriesSelector(AbstractUIRootContext timeSeriesContext) {
        seriesSelectionPanel = new SeriesSelectionPanel(timeSeriesContext, ServerTimeSeries.class);
        java.util.List<ColumnSettings> defaultColumnSettings = getDefaultColumnSettings();
        seriesSelectionPanel.setColumns(defaultColumnSettings);
        seriesSelectionPanel.addAllDynamicColumns();
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(seriesSelectionPanel);
        splitPane.setRightComponent(seriesSelectionPanel.getSelectionList());
        splitPane.setDividerLocation((PANEL_WIDTH * 2) / 3);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0.7d);
        return splitPane;
    }

    private List<ColumnSettings> getDefaultColumnSettings() {
        List<ColumnSettings> columns = new ArrayList<ColumnSettings>();
        FixedColumn.addFixedColumn(columns, FixedColumn.Selected);
        FixedColumn.addFixedColumn(columns, FixedColumn.DisplayName);
        return columns;
    }

}
