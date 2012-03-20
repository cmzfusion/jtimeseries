package com.od.jtimeseries.net.udp.message;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 19/03/12
 * Time: 17:45
 */
public interface SeriesDescriptionMessage extends UdpMessage {

    String getSeriesDescription();

    String getSeriesPath();
}
