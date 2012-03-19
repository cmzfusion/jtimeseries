package com.od.jtimeseries.net.udp.message;

import com.od.jtimeseries.timeseries.TimeSeriesItem;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 14/03/12
 * Time: 18:29
 */
public interface UdpMessageFactory {

    TimeSeriesValueMessage createTimeSeriesValueMessage(String path, TimeSeriesItem timeSeriesItem);

    HttpServerAnnouncementMessage createHttpServerAnnouncementMessage(int httpdPort, String serverName);

    /**
     * Deserialize a message from a received udp datagram by reading bytes from buffer, starting at offset
     * Do not read more than 'length' bytes
     *
     * @param buffer, buffer from which to read message
     * @param offset, location at which to start reading
     * @param length, number of valid bytes which can be read from offset
     * @return
     * @throws IOException if message could not be decoded
     */
    UdpMessage deserializeFromDatagram(byte[] buffer, int offset, int length) throws IOException;

    ClientAnnouncementMessage createClientAnnouncementMessage(int port, String description);
}
