package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.net.udp.TimeSeriesServer;
import com.od.jtimeseries.ui.download.panel.TimeSeriesServerContext;
import com.od.jtimeseries.ui.event.TimeSeriousBusListenerAdapter;
import com.od.jtimeseries.util.identifiable.Identifiable;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 16/02/11
 * Time: 17:45
 */
public class ContextUpdatingBusListener extends TimeSeriousBusListenerAdapter {

    private TimeSeriesContext rootContext;

    public ContextUpdatingBusListener(TimeSeriesContext rootContext) {
        this.rootContext = rootContext;
    }

    public void serverRemoved(TimeSeriesServer s) {
        for (Identifiable i : rootContext.getChildren()) {
            if ( i instanceof TimeSeriesServerContext) {
                if (((TimeSeriesServerContext)i).getServer().equals(s)) {
                    rootContext.removeChild(i);
                    break;
                }
            }
        }
    }
}
