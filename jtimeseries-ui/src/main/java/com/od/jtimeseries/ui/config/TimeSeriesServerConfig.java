package com.od.jtimeseries.ui.config;

import com.od.jtimeseries.net.udp.TimeSeriesServer;

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

    public TimeSeriesServerConfig(String hostName, int port, String description) {
        this.hostName = hostName;
        this.port = port;
        this.description = description;
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
