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
package com.od.jtimeseries.ui.download;

import com.od.jtimeseries.JTimeSeries;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.net.udp.TimeSeriesServerDictionary;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.ui.timeseries.ChartingTimeSeries;
import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.ui.download.panel.AbstractDownloadWizardPanel;
import com.od.jtimeseries.ui.download.panel.SelectRemoteSeriesPanel;
import com.od.jtimeseries.ui.download.panel.SelectServerPanel;
import com.od.swing.progress.ProgressLayeredPane;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 24-May-2009
 * Time: 22:30:18
 * To change this template use File | Settings | File Templates.
 */
public class DownloadRemoteSeriesDialog extends JFrame {

    private ProgressLayeredPane progressLayeredPane;
    private AbstractDownloadWizardPanel.WizardPanelListener panelListener;
    private SelectServerPanel selectServerPanel;
    private TimeSeriesContext contextToStoreRemoteSeries;
    private SelectRemoteSeriesPanel selectRemoteSeriesPanel;
    private DisplayNameCalculator displayNameCalculator;
    private List<ChartingTimeSeries> selectedSeries = new LinkedList<ChartingTimeSeries>();

    public DownloadRemoteSeriesDialog(TimeSeriesServerDictionary serverDictionary, DisplayNameCalculator displayNameCalculator, JComponent dialogPositionComponent) {
        this.displayNameCalculator = displayNameCalculator;
        setTitle("Download Time Series");
        setAlwaysOnTop(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        createPanelListener();
        createPanels(serverDictionary);

        progressLayeredPane = new ProgressLayeredPane(selectServerPanel);
        getContentPane().add(progressLayeredPane);

        setSize(AbstractDownloadWizardPanel.PANEL_WIDTH, AbstractDownloadWizardPanel.PANEL_HEIGHT);
        setLocationRelativeTo(dialogPositionComponent);
    }

    public List<ChartingTimeSeries> getSelectedSeries() {
        return selectedSeries;
    }

    private void createPanels(TimeSeriesServerDictionary serverDictionary) {
        contextToStoreRemoteSeries = JTimeSeries.createRootContext();
        selectServerPanel = new SelectServerPanel(panelListener, serverDictionary, contextToStoreRemoteSeries, displayNameCalculator);
    }

    private void createPanelListener() {
        panelListener = new AbstractDownloadWizardPanel.WizardPanelListener() {

            public void seriesLoaded() {
                selectRemoteSeriesPanel = new SelectRemoteSeriesPanel(panelListener, contextToStoreRemoteSeries);
                progressLayeredPane.setViewComponent(
                        selectRemoteSeriesPanel
                );
            }

            public void seriesSelected(java.util.List<ChartingTimeSeries> series) {
                DownloadRemoteSeriesDialog.this.selectedSeries = series;
                dispose();
            }

            public void downloadCancelled() {
                dispose();
            }
        };
    }
}
