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

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.net.udp.RemoteServerDictionary;
import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.ui.selector.SeriesSelectionPanel;
import com.od.jtimeseries.ui.util.ImageUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
* Created by IntelliJ IDEA.
* User: nick
* Date: 24-May-2009
* Time: 22:29:22
*
* Create and show a dialog to download series
*/
public class ShowDownloadSeriesDialogAction extends AbstractAction {

    private SeriesSelectionPanel seriesSelectionPanel;
    private TimeSeriesContext contextToReceiveSeries;
    private RemoteServerDictionary serverDictionary;
    private DisplayNameCalculator displayNameCalculator;
    private JComponent componentForDialogPositioning;

    public ShowDownloadSeriesDialogAction(SeriesSelectionPanel seriesSelectionPanel, TimeSeriesContext contextToReceiveSeries, RemoteServerDictionary serverDictionary, DisplayNameCalculator displayNameCalculator, JComponent componentForDialogPositioning) {
        super("Download Series", ImageUtils.DOWNLOAD_16x16);
        this.seriesSelectionPanel = seriesSelectionPanel;
        this.contextToReceiveSeries = contextToReceiveSeries;
        this.serverDictionary = serverDictionary;
        this.displayNameCalculator = displayNameCalculator;
        this.componentForDialogPositioning = componentForDialogPositioning;
    }

    public void actionPerformed(ActionEvent e) {
        DownloadRemoteSeriesDialog d = new DownloadRemoteSeriesDialog(
                contextToReceiveSeries,
                serverDictionary,
                displayNameCalculator,
                componentForDialogPositioning
        );

        d.setVisible(true);
        d.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                seriesSelectionPanel.refresh();
            }
        });
    }
}
