package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.context.impl.DefaultTimeSeriesContext;
import com.od.jtimeseries.net.udp.TimeSeriesServer;
import com.od.jtimeseries.ui.download.panel.TimeSeriesServerContext;
import com.od.jtimeseries.ui.timeserious.event.TimeSeriousBusListener;
import com.od.jtimeseries.ui.timeserious.event.TimeSeriousBusListenerAdapter;
import com.od.swing.eventbus.UIEventBus;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 13-Dec-2010
 * Time: 07:19:42
 * To change this template use File | Settings | File Templates.
 */
public class TimeSeriousRootContext extends DefaultTimeSeriesContext {

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

    private class RootContextBusListener extends TimeSeriousBusListenerAdapter {

        //add a time series server context when a new server is created
        public void serverAdded(TimeSeriesServer s) {
            TimeSeriesServerContext context = new TimeSeriesServerContext(
                s, TimeSeriousRootContext.this, s.getDescription(), s.getDescription()
            );
            TimeSeriousRootContext.this.addChild(context);
        }
    }
}
