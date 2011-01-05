package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.context.impl.DefaultTimeSeriesContext;
import com.od.jtimeseries.net.udp.TimeSeriesServer;
import com.od.jtimeseries.ui.download.panel.AddSeriesFromServerTask;
import com.od.jtimeseries.ui.download.panel.LoadSeriesFromServerCommand;
import com.od.jtimeseries.ui.download.panel.TimeSeriesServerContext;
import com.od.jtimeseries.ui.event.TimeSeriousBusListener;
import com.od.jtimeseries.ui.event.TimeSeriousBusListenerAdapter;
import com.od.jtimeseries.ui.timeserious.config.TimeSeriesServerConfig;
import com.od.jtimeseries.ui.timeserious.config.TimeSeriousConfig;
import com.od.jtimeseries.ui.util.ConfigAware;
import com.od.swing.eventbus.UIEventBus;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 13-Dec-2010
 * Time: 07:19:42
 * To change this template use File | Settings | File Templates.
 */
public class TimeSeriousRootContext extends DefaultTimeSeriesContext implements ConfigAware {

    public TimeSeriousRootContext() {
        super("TmeSeriousRootContext", "Root context for TimeSerious application");
        addBusListener();
    }

    private void addBusListener() {
        UIEventBus.getInstance().addEventListener(
            TimeSeriousBusListener.class,
                new RootContextBusListener()
        );
    }

    public void prepareConfigForSave(TimeSeriousConfig config) {
        List<TimeSeriesServerContext> serverContexts = findAll(TimeSeriesServerContext.class).getAllMatches();

        List<TimeSeriesServerConfig> serverConfigs = new LinkedList<TimeSeriesServerConfig>();
        for ( TimeSeriesServerContext c : serverContexts) {
            serverConfigs.add(new TimeSeriesServerConfig(c));
        }
        config.setTimeSeriesServerConfigs(serverConfigs);
    }

    public void restoreConfig(TimeSeriousConfig config) {
        for (TimeSeriesServerConfig c : config.getServerConfigs()) {
            TimeSeriesServer s = c.createServer();
            TimeSeriesServerContext context = new TimeSeriesServerContext(s);
            addChild(context);
            new LoadSeriesFromServerCommand(
                this
            ).execute(s);
        }
    }

    private class RootContextBusListener extends TimeSeriousBusListenerAdapter {

        //add a time series server context when a new server is created
        public void serverAdded(TimeSeriesServer s) {
            TimeSeriesServerContext context = new TimeSeriesServerContext(s);
            TimeSeriousRootContext.this.addChild(context);
        }
    }
}
