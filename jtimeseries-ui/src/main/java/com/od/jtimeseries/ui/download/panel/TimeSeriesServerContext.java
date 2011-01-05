package com.od.jtimeseries.ui.download.panel;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.context.impl.DefaultTimeSeriesContext;
import com.od.jtimeseries.net.udp.TimeSeriesServer;
import com.od.jtimeseries.ui.util.Displayable;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 09-Dec-2010
 * Time: 14:13:37
 * To change this template use File | Settings | File Templates.
 */
public class TimeSeriesServerContext extends DefaultTimeSeriesContext implements Displayable {

    private TimeSeriesServer server;
    private boolean loading;

    public TimeSeriesServerContext(TimeSeriesServer server) {
        super(server.getDescription(), server.getDescription());
        this.server = server;
    }

    public TimeSeriesServer getServer() {
        return server;
    }

    public void setServer(TimeSeriesServer server) {
        this.server = server;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
        fireNodeChanged("loadingState");
    }

    public String getDisplayName() {
        return server.getDescription();
    }
}
