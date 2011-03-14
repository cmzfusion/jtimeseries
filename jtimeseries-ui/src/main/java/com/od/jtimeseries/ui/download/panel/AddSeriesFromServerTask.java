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
import com.od.jtimeseries.net.httpd.TimeSeriesIndexHandler;
import com.od.jtimeseries.net.udp.TimeSeriesServer;
import com.od.jtimeseries.ui.timeseries.RemoteHttpTimeSeries;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.timeseries.UiTimeSeriesConfig;
import com.od.jtimeseries.util.identifiable.IdentifiablePathUtils;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 12-Jan-2009
 * Time: 16:18:37
 */
public class AddSeriesFromServerTask implements Callable<List<ReadTimeSeriesIndexQuery.RemoteTimeSeries>> {

    private static final LogMethods logMethods = LogUtils.getLogMethods(AddSeriesFromServerTask.class);

    private URL remoteContextUrl;
    private TimeSeriesContext destinationRootContext;
    private TimeSeriesServerContext serverContext;
    private TimeSeriesServer server;

    public AddSeriesFromServerTask(TimeSeriesContext destinationRootContext, TimeSeriesServer server) throws MalformedURLException {
        this.destinationRootContext = destinationRootContext;
        this.server = server;
        this.serverContext = findOrCreateServerContext();
        this.remoteContextUrl = createUrl();
    }

    private URL createUrl() throws MalformedURLException {
        return new URL("http", server.getInetAddress().getHostName(), server.getPort(), "/" + TimeSeriesIndexHandler.INDEX_POSTFIX);
    }

    private TimeSeriesServerContext findOrCreateServerContext() {
        TimeSeriesServerContext serverContext = (TimeSeriesServerContext) destinationRootContext.get(server.getServerContextIdentifier());
        if (serverContext == null) {
            serverContext = new TimeSeriesServerContext(destinationRootContext, server);
            destinationRootContext.addChild(serverContext);
        }
        return serverContext;
    }

    public List<ReadTimeSeriesIndexQuery.RemoteTimeSeries> call() throws Exception {
        try {
            serverContext.setLoading(true); //this will cause loading animations to be shown for the node representing this server in the context tree
            ReadTimeSeriesIndexQuery readIndexQuery = new ReadTimeSeriesIndexQuery(remoteContextUrl);
            readIndexQuery.runQuery();
            for (ReadTimeSeriesIndexQuery.RemoteTimeSeries timeSeriesResult : readIndexQuery.getResult()) {
                createAndAddToContext(serverContext, timeSeriesResult);
            }
            return readIndexQuery.getResult();

        } finally {
            serverContext.setLoading(false);
        }
    }

    private void createAndAddToContext(TimeSeriesServerContext serverContext, ReadTimeSeriesIndexQuery.RemoteTimeSeries series) {

        String path =
            serverContext.getId() +
            IdentifiablePathUtils.NAMESPACE_SEPARATOR +
            series.getParentPath() +
            IdentifiablePathUtils.NAMESPACE_SEPARATOR +
            series.getId();

        try {
            UiTimeSeriesConfig config = new UiTimeSeriesConfig();
            config.setId(series.getId());
            config.setParentPath(series.getParentPath());
            config.setDescription(series.getDescription());
            config.setTimeSeriesUrl(series.getSeriesURL().toString());

            //TODO - should we add extra handling if series already exists in target?
            if (!destinationRootContext.contains(path)) {
                createNewSeries(series, path, config);
            }

            //always refresh summary stats
            Identifiable i = destinationRootContext.get(path);
            if ( i instanceof UIPropertiesTimeSeries) {
                i.putAllProperties(series.getSummaryStatsProperties());
                ((UIPropertiesTimeSeries)i).setStatsRefreshTime(new Date());
            }

            //update the summary stats on the shared remote http series, if it exists
            RemoteHttpTimeSeries.updateSummaryStats(config, series.getSummaryStatsProperties());
        } catch (Throwable t) {
            logMethods.logError("Error adding series from server " + path + " in context " + destinationRootContext, t);
        }
    }

    private void createNewSeries(ReadTimeSeriesIndexQuery.RemoteTimeSeries series, String path, UiTimeSeriesConfig config) {
        //we don't know what type of UIPropertiesTimeSeries the destination context should contain
        //defer construction to the context's factories by using the generic create method on context
        destinationRootContext.create(
            path,
            series.getDescription(),
            UIPropertiesTimeSeries.class,
            config
        );
    }

}
