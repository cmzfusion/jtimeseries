package com.od.jtimeseries.ui.timeserious.config;

import com.od.jtimeseries.net.udp.TimeSeriesServer;
import com.od.jtimeseries.ui.download.panel.TimeSeriesServerContext;

import java.net.InetAddress;

/**
* Created by IntelliJ IDEA.
* User: nick
* Date: 05/01/11
* Time: 18:22
* To change this template use File | Settings | File Templates.
*/
public class TimeSeriesServerConfig {

    private InetAddress serverAddress;
    private String description;
    private int port;

    public TimeSeriesServerConfig(TimeSeriesServerContext c) {
        this.serverAddress = c.getServer().getInetAddress();
        this.description = c.getServer().getDescription();
        this.port = c.getServer().getPort();
    }

    public TimeSeriesServer createServer() {
        return new TimeSeriesServer(
                serverAddress,
                port,
                description,
                0
        );
    }

}
