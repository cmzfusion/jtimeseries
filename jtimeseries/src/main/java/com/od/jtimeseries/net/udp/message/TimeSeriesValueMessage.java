package com.od.jtimeseries.net.udp.message;

import com.od.jtimeseries.timeseries.TimeSeriesItem;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 14/03/12
 * Time: 18:27
 */
public interface TimeSeriesValueMessage extends UdpMessage {

    String getSeriesPath();

    String getDescription();

    TimeSeriesItem getTimeSeriesItem();
}
