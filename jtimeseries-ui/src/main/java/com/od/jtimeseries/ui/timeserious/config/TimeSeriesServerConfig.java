package com.od.jtimeseries.ui.timeserious.config;

import com.od.jtimeseries.net.udp.TimeSeriesServer;
import com.od.jtimeseries.ui.download.panel.TimeSeriesServerContext;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
* Created by IntelliJ IDEA.
* User: nick
* Date: 05/01/11
* Time: 18:22
* To change this template use File | Settings | File Templates.
*/
public class TimeSeriesServerConfig {

    private String hostName;
    private int port;
    private String description;

    public TimeSeriesServerConfig(TimeSeriesServerContext c) {
        this.hostName = c.getServer().getHostName();
        this.description = c.getServer().getDescription();
        this.port = c.getServer().getPort();
    }

    public String getHostName() {
        return hostName;
    }

    public int getPort() {
        return port;
    }

    public String getDescription() {
        return description;
    }

    public TimeSeriesServer createServer() throws UnknownHostException {
        return new TimeSeriesServer(
                hostName,
                port,
                description
        );
    }

}
