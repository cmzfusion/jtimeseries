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
package com.od.jtimeseries.ui.visualizer.download;

import com.od.jtimeseries.JTimeSeries;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.net.udp.TimeSeriesServerDictionary;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.ui.timeseries.ChartingTimeSeries;
import com.od.jtimeseries.ui.visualizer.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.ui.visualizer.download.panel.AbstractDownloadWizardPanel;
import com.od.jtimeseries.ui.visualizer.download.panel.SelectRemoteSeriesPanel;
import com.od.jtimeseries.ui.visualizer.download.panel.SelectServerPanel;
import com.od.jtimeseries.util.time.Time;
import com.od.swing.progress.ProgressLayeredPane;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 24-May-2009
 * Time: 22:30:18
 * To change this template use File | Settings | File Templates.
 */
public class DownloadRemoteSeriesDialog extends JFrame {

    private ProgressLayeredPane progressLayeredPane;
    private TimeSeriesContext contextToReceiveSeries;
    private AbstractDownloadWizardPanel.WizardPanelListener panelListener;
    private SelectServerPanel selectServerPanel;
    private TimeSeriesContext contextToStoreRemoteSeries;
    private SelectRemoteSeriesPanel selectRemoteSeriesPanel;
    private DisplayNameCalculator displayNameCalculator;

    public DownloadRemoteSeriesDialog(TimeSeriesContext contextToReceiveSeries, TimeSeriesServerDictionary serverDictionary, DisplayNameCalculator displayNameCalculator, JComponent dialogPositionComponent) {
        this.contextToReceiveSeries = contextToReceiveSeries;
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

            public void seriesSelected(java.util.List<ChartingTimeSeries> remoteUrlTimeSeries) {
                addLocalTimeSeries(remoteUrlTimeSeries);
                dispose();
            }

            public void downloadCancelled() {
                dispose();
            }
        };
    }

    private void addLocalTimeSeries(java.util.List<ChartingTimeSeries> selectedTimeSeries) {
        for ( IdentifiableTimeSeries s : selectedTimeSeries) {
            TimeSeriesContext c = contextToReceiveSeries.createContext(s.getParentPath());

            //TODO we may want to flag the conflict up to the user
            if ( ! c.containsChildWithId(s.getId())) {
                c.addChild(s);
            }
        }
    }
}
