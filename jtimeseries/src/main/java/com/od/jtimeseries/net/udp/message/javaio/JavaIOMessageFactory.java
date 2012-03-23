package com.od.jtimeseries.net.udp.message.javaio;

import com.od.jtimeseries.net.udp.message.*;
import com.od.jtimeseries.timeseries.TimeSeriesItem;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 23/03/12
 * Time: 18:11
 */
public class JavaIOMessageFactory implements UdpMessageFactory {

    public TimeSeriesValueMessage createTimeSeriesValueMessage(String path, TimeSeriesItem timeSeriesItem) {
        return new JavaIOTimeSeriesValueMessage(path, timeSeriesItem);
    }

    public SeriesDescriptionMessage createTimeSeriesDescriptionMessage(String path, String description) {
        return new JavaIODescriptionMessage(path, description);
    }

    public HttpServerAnnouncementMessage createHttpServerAnnouncementMessage(int httpdPort, String serverName) {
        return new JavaIOHttpServerAnnouncementMessage(httpdPort, serverName);
    }

    public UdpMessage deserializeFromDatagram(byte[] buffer, int offset, int length) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ClientAnnouncementMessage createClientAnnouncementMessage(int port, String description) {
        return new JavaIOClientAnnouncementMessage(port, description);
    }
}
