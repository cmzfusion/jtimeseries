package com.od.jtimeseries.net.udp.message;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 14/03/12
 * Time: 18:23
 *
 * Announce that a jtimeseries process is listening for UDP packets on a given port
 */
public interface AnnouncementMessage extends UdpMessage {

    int getPort();

    String getDescription();
}
