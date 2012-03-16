package com.od.jtimeseries.net.udp.message.utf8;

import com.od.jtimeseries.net.udp.message.*;
import com.od.jtimeseries.timeseries.TimeSeriesItem;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 16/03/12
 * Time: 16:29
 * To change this template use File | Settings | File Templates.
 */
public class Utf8MessageFactory implements UdpMessageFactory {

    public TimeSeriesValueMessage createTimeSeriesValueMessage(String path, String description, TimeSeriesItem timeSeriesItem) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public HttpServerAnnouncementMessage createHttpServerAnnouncementMessage(int httpdPort, String serverName) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ClientAnnouncementMessage createClientAnnouncementMessage(int port, String description) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public UdpMessage deserializeFromDatagram(byte[] buffer, int offset, int length) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
