package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.net.udp.TimeSeriesServer;
import com.od.jtimeseries.ui.download.panel.LoadSeriesFromServerCommand;
import com.od.jtimeseries.ui.identifiable.TimeSeriesServerContext;
import com.od.jtimeseries.ui.selector.shared.ContextUpdatingBusListener;
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
        ScheduledFuture f = loadSeriesFromServerExecutor.scheduleWithFixedDelay(loadTask, 0, SERVER_REFRESH_RATE_MINS, TimeUnit.MINUTES);
        loadTasksByServer.put(s, f);
    }

}
