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

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.identifiable.Identifiable;
import com.od.jtimeseries.net.udp.TimeSeriesServerDictionary;
import com.od.jtimeseries.ui.config.ExportableConfig;
import com.od.jtimeseries.ui.config.UiTimeSeriesConfig;
import com.od.jtimeseries.ui.selector.shared.AbstractUIContextTimeSeriesFactory;
import com.od.jtimeseries.ui.selector.shared.ServerContextCreatingContextFactory;
import com.od.jtimeseries.ui.timeseries.ChartingTimeSeries;
import com.od.jtimeseries.ui.timeseries.RemoteHttpTimeSeries;
import com.od.jtimeseries.ui.timeseries.RemoteHttpTimeSeriesCollection;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.uicontext.ContextImportExportHandler;
import com.od.jtimeseries.ui.uicontext.IdentifiableListActionModel;
import com.od.jtimeseries.ui.uicontext.ImportItem;

import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.net.MalformedURLException;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 30/04/11
 * Time: 16:15
 * To change this template use File | Settings | File Templates.
 */
public class VisualizerImportExportHandler extends ContextImportExportHandler {

    public VisualizerImportExportHandler(TimeSeriesContext rootContext, TimeSeriesServerDictionary serverDictionary) {
        super(rootContext);
        setTimeSeriesFactory(new VisualizerTimeSeriesFactory());
        setContextFactory(new ServerContextCreatingContextFactory(rootContext, serverDictionary));
    }

    protected boolean canImport(Component component, IdentifiableListActionModel i, Identifiable target) {
        return i.isSelectionLimitedToTypes(TimeSeriesContext.class, UIPropertiesTimeSeries.class) && ! i.containsItemsFromRootContext(getRootContext());
    }

    protected boolean shouldImport(Identifiable i, Identifiable target) {
        return ! TimeSeriesContext.class.isAssignableFrom(i.getClass());  //ignore contexts when importing, import only timeseries leaf nodes
    }

    protected ImportItem getImportItem(Component component, Identifiable identifiable, Identifiable target) {
        UIPropertiesTimeSeries s = (UIPropertiesTimeSeries)identifiable;
        return new ImportItem(
            s.getPath(),
            s.getDescription(),
            UIPropertiesTimeSeries.class,
            s.getConfig()
        );
    }

    /**
     * Subclass should override to create ImportItem if this ExportableConfig can be imported
     * @return an ImportDetails, which contains everything necessary to import the target exportable config, or null
     */
    protected ImportItem getImportItem(Component component, ExportableConfig s, Identifiable target) {
        UiTimeSeriesConfig c = (UiTimeSeriesConfig)s;
        return new ImportItem(
            c.getPath(),
            c.getDescription(),
            UIPropertiesTimeSeries.class,
            c
        );
    }

    public int getSourceActions(IdentifiableListActionModel selected) {
        return DnDConstants.ACTION_COPY_OR_MOVE;
    }

    private class VisualizerTimeSeriesFactory extends AbstractUIContextTimeSeriesFactory {

        protected UIPropertiesTimeSeries createTimeSeriesForConfig(UiTimeSeriesConfig config) throws MalformedURLException {
            //http series are unique by URL, to minimise unnecessary queries.
            //Get or create the instance for this URL
            RemoteHttpTimeSeries httpSeries = RemoteHttpTimeSeriesCollection.getOrCreateHttpSeries(config);

            //the http series is wrapped with a ChartingTimeSeries instance which is unique to
            //this visualizier, and so can have local settings for display name, colour etc.
            return new ChartingTimeSeries(httpSeries, config);
        }
    }
}
