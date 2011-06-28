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
package com.od.jtimeseries.ui.timeserious.rootcontext;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.net.udp.TimeSeriesServer;
import com.od.jtimeseries.ui.download.panel.LoadSeriesFromServerCommand;
import com.od.jtimeseries.ui.identifiable.TimeSeriesServerContext;
import com.od.jtimeseries.ui.uicontext.ContextUpdatingBusListener;
import com.od.jtimeseries.util.NamedExecutors;

import java.util.concurrent.*;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 15/03/11
* Time: 19:33
*
* Periodically load the series and refresh the stats for servers in the main selector
*/
class ServerSeriesLoadingBusListener extends ContextUpdatingBusListener {

    private TimeSeriesContext rootContext;

    private static final ScheduledExecutorService loadSeriesFromServerExecutor = NamedExecutors.newSingleThreadScheduledExecutor("LoadSeriesFromServer");
    private static final int SERVER_REFRESH_RATE_MINS = 20;

    private final ConcurrentMap<TimeSeriesServer, ScheduledFuture> loadTasksByServer = new ConcurrentHashMap<TimeSeriesServer, ScheduledFuture>();

    public ServerSeriesLoadingBusListener(TimeSeriesContext rootContext) {
        super(rootContext);
        this.rootContext = rootContext;
    }

    //add a time series server context when a new server is created, and load its series
    //visualizer contexts don't do this automatically otherwise the visualizers would end up with all the
    //known servers, even if they don't have any series
    public void serverAdded(TimeSeriesServer s) {
        TimeSeriesServerContext context = new TimeSeriesServerContext(s);
        rootContext.addChild(context);
        addServerLoadTask(s);
    }

    public void serverRemoved(TimeSeriesServer s) {
        synchronized (loadTasksByServer) {
            ScheduledFuture f = loadTasksByServer.remove(s);
            f.cancel(false);
        }
        super.serverRemoved(s);
    }

    private void addServerLoadTask(final TimeSeriesServer s) {
        Runnable loadTask = new Runnable() {
            public void run() {
                synchronized (loadTasksByServer) {
                    //if the server has been removed from the map, this means the user has removed the server
                    //we don't want to run the refresh
                    if ( loadTasksByServer.containsKey(s)) {
                        new LoadSeriesFromServerCommand(
                            rootContext
                        ).execute(s);
                    }
                }
            }
        };
        ScheduledFuture f = loadSeriesFromServerExecutor.scheduleWithFixedDelay(loadTask, 1, SERVER_REFRESH_RATE_MINS * 60, TimeUnit.SECONDS);
        loadTasksByServer.put(s, f);
    }

}
