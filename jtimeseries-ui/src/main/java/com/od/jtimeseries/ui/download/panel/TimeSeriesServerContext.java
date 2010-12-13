package com.od.jtimeseries.ui.download.panel;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.context.impl.DefaultTimeSeriesContext;
import com.od.jtimeseries.net.udp.TimeSeriesServer;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 09-Dec-2010
 * Time: 14:13:37
 * To change this template use File | Settings | File Templates.
 */
public class TimeSeriesServerContext extends DefaultTimeSeriesContext {

    private TimeSeriesServer server;

    public TimeSeriesServerContext(TimeSeriesServer server, TimeSeriesContext parentContext, String id, String description) {
        super(parentContext, id, description);
        this.server = server;
    }

    public TimeSeriesServer getServer() {
        return server;
    }

    public void setServer(TimeSeriesServer server) {
        this.server = server;
    }
}
