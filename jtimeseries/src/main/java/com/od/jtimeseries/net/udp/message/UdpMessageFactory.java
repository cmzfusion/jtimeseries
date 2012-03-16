package com.od.jtimeseries.net.udp.message;

import com.od.jtimeseries.timeseries.TimeSeriesItem;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 14/03/12
 * Time: 18:29
 */
public interface UdpMessageFactory {


    TimeSeriesValueMessage createTimeSeriesValueMessage(String path, String description, TimeSeriesItem timeSeriesItem);

    HttpServerAnnouncementMessage createHttpServerAnnouncementMessage(int httpdPort, String serverName);
}
