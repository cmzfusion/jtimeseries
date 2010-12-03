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

import com.od.jtimeseries.ui.timeseries.ChartingTimeSeries;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 25-May-2009
 * Time: 00:08:07
 * To change this template use File | Settings | File Templates.
 */
public class AbstractDownloadWizardPanel extends JPanel {

    private WizardPanelListener panelListener;
    public static final int PANEL_WIDTH = 800;
    public static final int PANEL_HEIGHT = 800;

    public AbstractDownloadWizardPanel(WizardPanelListener panelListener) {
        this.panelListener = panelListener;
    }

    protected Box createTitlePanel(String title) {
        Box labelPanel = Box.createHorizontalBox();
        JLabel label = new JLabel(title);
        label.setFont(label.getFont().deriveFont(14f).deriveFont(Font.BOLD));
        label.setBorder(new EmptyBorder(5,5,5,5));
        labelPanel.add(label);
        labelPanel.add(Box.createHorizontalGlue());
        return labelPanel;
    }

    public WizardPanelListener getPanelListener() {
        return panelListener;
    }

    public static interface WizardPanelListener {
        void seriesLoaded();

        void seriesSelected(List<? extends UIPropertiesTimeSeries> selectedTimeSeries);

        void downloadCancelled();
    }
}
