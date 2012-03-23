package com.od.jtimeseries.net.udp.message;

import com.od.jtimeseries.timeseries.TimeSeriesItem;

import java.io.IOException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 14/03/12
 * Time: 18:29
 */
public interface UdpMessageFactory {

    TimeSeriesValueMessage createTimeSeriesValueMessage(String path, TimeSeriesItem timeSeriesItem);

    SeriesDescriptionMessage createTimeSeriesDescriptionMessage(String path, String description);

    HttpServerAnnouncementMessage createHttpServerAnnouncementMessage(int httpdPort, String serverName);

    /**
     * Deserialize one or more messages from a received udp datagram by reading bytes from buffer
     * Do not read more than 'length' bytes
     *
     * @param buffer, buffer from which to read message
     * @return
     * @throws IOException if message could not be decoded
     */
    List<UdpMessage> deserializeFromDatagram(byte[] buffer, int length) throws IOException;

    ClientAnnouncementMessage createClientAnnouncementMessage(int port, String description);
}
