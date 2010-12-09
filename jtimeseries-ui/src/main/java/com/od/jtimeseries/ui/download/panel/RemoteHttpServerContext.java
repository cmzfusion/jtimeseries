package com.od.jtimeseries.ui.download.panel;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.context.impl.DefaultTimeSeriesContext;
import com.od.jtimeseries.net.udp.RemoteHttpServer;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 09-Dec-2010
 * Time: 14:13:37
 * To change this template use File | Settings | File Templates.
 */
public class RemoteHttpServerContext extends DefaultTimeSeriesContext {

    private RemoteHttpServer server;

    public RemoteHttpServerContext(RemoteHttpServer server, TimeSeriesContext parentContext, String id, String description) {
        super(parentContext, id, description);
        this.server = server;
    }

    public RemoteHttpServer getServer() {
        return server;
    }

    public void setServer(RemoteHttpServer server) {
        this.server = server;
    }
}
