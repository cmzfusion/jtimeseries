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
package com.od.jtimeseries.ui.visualizer.selector.shared;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.ui.timeseries.RemoteChartingTimeSeries;
import com.od.jtimeseries.ui.visualizer.selector.shared.TitleLabelPanel;
import com.od.swing.action.ListSelectionActionModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 25-May-2009
 * Time: 11:43:35
 * To change this template use File | Settings | File Templates.
 */
public abstract class SelectorPanel extends TitleLabelPanel {

    protected java.util.List<SelectorPanelListener> seriesSelectionListeners = new ArrayList<SelectorPanelListener>();
    private ListSelectionActionModel<RemoteChartingTimeSeries> seriesActionModel;

    public SelectorPanel(ListSelectionActionModel<RemoteChartingTimeSeries> seriesActionModel) {
        this.seriesActionModel = seriesActionModel;
    }

    public void addSelectorListener(SelectorPanelListener seriesSelectionListener) {
        seriesSelectionListeners.add(seriesSelectionListener);
    }

    protected void fireSelectedForDescription(IdentifiableTimeSeries m) {
        java.util.List<SelectorPanelListener> snapshot = new ArrayList<SelectorPanelListener>(seriesSelectionListeners);
        for ( SelectorPanelListener l : snapshot) {
            l.seriesSelectedForDescription(m);
        }
    }

    protected void fireSelectedForDescription(TimeSeriesContext m) {
        java.util.List<SelectorPanelListener> snapshot = new ArrayList<SelectorPanelListener>(seriesSelectionListeners);
        for ( SelectorPanelListener l : snapshot) {
            l.contextSelectedForDescription(m);
        }
    }

    protected ListSelectionActionModel<RemoteChartingTimeSeries> getSeriesActionModel() {
        return seriesActionModel;
    }

    public abstract void refreshSeries();

    public abstract void removeSeries(List<RemoteChartingTimeSeries> series);

    public static interface SelectorPanelListener {
        void seriesSelectedForDescription(IdentifiableTimeSeries s);

        void contextSelectedForDescription(TimeSeriesContext m);
    }

    public static class SelectorPanelListenerAdapter implements SelectorPanelListener {

        public void seriesSelectedForDescription(IdentifiableTimeSeries s) {}

        public void contextSelectedForDescription(TimeSeriesContext m) {}
    }
}
