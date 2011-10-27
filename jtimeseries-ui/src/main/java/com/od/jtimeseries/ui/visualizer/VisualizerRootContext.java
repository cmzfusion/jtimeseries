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
package com.od.jtimeseries.ui.visualizer;

import com.od.jtimeseries.net.udp.TimeSeriesServer;
import com.od.jtimeseries.net.udp.TimeSeriesServerDictionary;
import com.od.jtimeseries.ui.config.UiTimeSeriesConfig;
import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.ui.uicontext.AbstractUIRootContext;
import com.od.jtimeseries.ui.uicontext.ContextImportExportHandler;
import com.od.jtimeseries.ui.uicontext.ContextUpdatingBusListener;
import com.od.jtimeseries.ui.selector.shared.ServerContextCreatingContextFactory;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.identifiable.PathParser;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 09/01/11
 * Time: 09:26
 * To change this template use File | Settings | File Templates.
 */
public class VisualizerRootContext extends AbstractUIRootContext {

    private TimeSeriesServerDictionary serverDictionary;

    public VisualizerRootContext(TimeSeriesServerDictionary serverDictionary, DisplayNameCalculator displayNameCalculator) {
        super(displayNameCalculator);
        this.serverDictionary = serverDictionary;
        ContextImportExportHandler h = new VisualizerImportExportHandler(this, serverDictionary);
        setImportExportHandler(h);
    }

    protected ContextUpdatingBusListener createContextBusListener() {
        return new ContextUpdatingBusListener(this);
    }

    /**
     * Add chart configs to this visualizer, under the local server node with
     * matching URL to the server specified in each config
     */
    public void addChartConfigs(List<UiTimeSeriesConfig> chartConfigs) {
        for ( UiTimeSeriesConfig c : chartConfigs ) {
            try {

                //the first node in the path was the server description
                //when the series was saved.
                //the same server may have a different description
                //locally
                PathParser p = new PathParser(c.getPath());
                String serverDescription = p.removeFirstNode();
                TimeSeriesServer s = ServerContextCreatingContextFactory.getTimeSeriesServer(c, serverDescription, serverDictionary);

                //TODO - handle case where we already have a series with this path?
                String newLocalPath = s.getServerContextIdentifier() + NAMESPACE_SEPARATOR + p.getRemainingPath();
                if ( ! contains(newLocalPath)) {
                    create(newLocalPath, c.getDescription(), UIPropertiesTimeSeries.class, c);
                }
            } catch (Exception e) {
                logMethods.logError("Failed to create series for config " + c, e);
            }
        }
    }
}
