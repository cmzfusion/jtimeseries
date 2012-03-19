package com.od.jtimeseries.net.udp.message;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 14/03/12
 * Time: 18:23
 *
 * Announce that a jtimeseries component is running and listening on a port
 */
public interface AnnouncementMessage extends UdpMessage {

    /**
     * @return port number of the service
     */
    int getPort();

    /**
     * @return A description of the agent whose presence is being announced (e.g. the name of the timeseries server, or client)
     */
    String getDescription();
}
