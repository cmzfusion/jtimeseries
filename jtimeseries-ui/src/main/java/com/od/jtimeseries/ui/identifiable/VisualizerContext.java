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
package com.od.jtimeseries.ui.identifiable;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.ui.config.ExportableConfig;
import com.od.jtimeseries.ui.config.ExportableConfigHolder;
import com.od.jtimeseries.ui.config.VisualizerConfiguration;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 10/03/11
 * Time: 08:34
 */
public class VisualizerContext extends HidablePeerContext<VisualizerConfiguration, PeerVisualizerFrame> implements ExportableConfigHolder {

    public VisualizerContext(VisualizerConfiguration visualizerConfiguration) {
        super(visualizerConfiguration.getTitle(), visualizerConfiguration.getTitle(), visualizerConfiguration, visualizerConfiguration.isShown());
    }

    protected VisualizerConfiguration createPeerConfig(boolean isShown) {
        PeerVisualizerFrame peerFrame = getPeerResource();
        VisualizerConfiguration c = peerFrame.getVisualizerConfiguration();
        c.setFrameLocation(peerFrame.getBounds());
        c.setZPosition(peerFrame.getZPosition());
        c.setIsIcon(peerFrame.isIcon());
        c.setShown(isShown);
        return c;
    }

    public int getZPosition() {
        return isPeerCreatedAndShown() ?
            getPeerResource().getZPosition() :
            getConfiguration().getZPosition();
    }

    public ExportableConfig getExportableConfig() {
        return getConfiguration();
    }

    public String getDefaultFileName() {
        return "timeSeriousVisualizer_" + getId();
    }

    public void addTimeSeries(List<UIPropertiesTimeSeries> selectedSeries) {
        getPeerResource().addTimeSeries(selectedSeries);
    }

    public HidablePeerContext<VisualizerConfiguration, PeerVisualizerFrame> newInstance(TimeSeriesContext parent, VisualizerConfiguration config) {
        return new VisualizerContext(config);
    }
}
