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
import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.ui.timeseries.ServerTimeSeries;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 12-Jan-2009
 * Time: 16:18:37
 */
public class AddSeriesFromServerTask implements Callable<List<ReadTimeSeriesIndexQuery.RemoteTimeSeries>> {

    private TimeSeriesServerContext serverContext;
    private URL remoteContextUrl;
    private TimeSeriesContext rootContext;
    private TimeSeriesServer server;
    private DisplayNameCalculator displayNameCalculator;

    public AddSeriesFromServerTask(TimeSeriesContext rootContext, TimeSeriesServer server, DisplayNameCalculator displayNameCalculator) throws MalformedURLException {
        this.rootContext = rootContext;
        this.server = server;
        this.displayNameCalculator = displayNameCalculator;
        this.serverContext = findOrCreateServerContext();
        this.remoteContextUrl = createUrl();
    }

    private URL createUrl() throws MalformedURLException {
        return new URL("http", server.getServerAddress().getHostName(), server.getPort(), "/" + TimeSeriesIndexHandler.INDEX_POSTFIX);
    }

    private TimeSeriesServerContext findOrCreateServerContext() {
        String serverId = server.getDescription();
        TimeSeriesServerContext serverContext = (TimeSeriesServerContext)rootContext.get(serverId);
        if ( serverContext == null) {
            serverContext = new TimeSeriesServerContext(server);
            rootContext.addChild(serverContext);
        }
        return serverContext;
    }

    public List<ReadTimeSeriesIndexQuery.RemoteTimeSeries> call() throws Exception {
        try {
            serverContext.setLoading(true); //this will cause loading animations to be shown for the node representing this server in the context tree
            ReadTimeSeriesIndexQuery readIndexQuery = new ReadTimeSeriesIndexQuery(remoteContextUrl);
            readIndexQuery.runQuery();
            for ( ReadTimeSeriesIndexQuery.RemoteTimeSeries timeSeriesResult : readIndexQuery.getResult()) {
                createAndAddToContext(timeSeriesResult);
            }
            return readIndexQuery.getResult();

        } finally {
            serverContext.setLoading(false);
        }
    }

    private void createAndAddToContext(ReadTimeSeriesIndexQuery.RemoteTimeSeries result) {
        TimeSeriesContext c = serverContext.createContext(result.getParentPath());

        ServerTimeSeries series = new ServerTimeSeries(result.getId(), result.getDescription(), result.getSeriesURL());
        series.putAllProperties(result.getSummaryStatsProperties());

        //TODO - should we add extra handling if series already exists in target?
        if ( ! c.containsChildWithId(series.getId())) {
            c.addChild(series);

            //must do this after adding the series to the context because the contextPath will not
            //be complete until this is done, and the name calculation is based on the path
            if ( displayNameCalculator != null) {
                displayNameCalculator.setDisplayName(series);
            }
        }
    }

}
