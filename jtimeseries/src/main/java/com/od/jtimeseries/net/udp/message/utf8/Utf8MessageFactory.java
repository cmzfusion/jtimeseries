package com.od.jtimeseries.net.udp.message.utf8;

import com.od.jtimeseries.net.udp.message.*;
import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.util.NetworkUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 16/03/12
 * Time: 16:29
 * To change this template use File | Settings | File Templates.
 */
public class Utf8MessageFactory implements UdpMessageFactory {

    private final int LENGTH_OF_MESSAGE_TYPE_PREFIX = AbstractUtf8Message.MSGTYPE_FIELD_KEY.length() + 1;


    public TimeSeriesValueMessage createTimeSeriesValueMessage(String path, TimeSeriesItem timeSeriesItem) {
        return new Utf8TimeSeriesValueMessage(NetworkUtils.getLocalHostname(), path, timeSeriesItem);
    }

    public SeriesDescriptionMessage createTimeSeriesDescriptionMessage(String path, String description) {
        return new Utf8DescriptionMessage(NetworkUtils.getLocalHostname(), path, description);
    }

    public HttpServerAnnouncementMessage createHttpServerAnnouncementMessage(int httpdPort, String description) {
        return new Utf8HttpServerAnnouncementMessage(httpdPort, NetworkUtils.getLocalHostname(), description);
    }

    public ClientAnnouncementMessage createClientAnnouncementMessage(int udpPort, String description) {
        return new Utf8ClientAnnouncementMessage(udpPort, NetworkUtils.getLocalHostname(), description);
    }

    public List<UdpMessage> deserializeFromDatagram(byte[] buffer, int length) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buffer, 0, length)));
        readEncoding(reader);
        MessageType t = readMessageType(reader);
        AbstractUtf8Message message = createMessage(t);
        message.deserialize(reader);
        return Collections.singletonList((UdpMessage)message);
    }

    private void readEncoding(BufferedReader reader) throws IOException {
        //first line should be encoding UTF-8
        String encoding = reader.readLine();
        if ( ! AbstractUtf8Message.UTF8_ENCODING_HEADER_STRING.equals(encoding)) {
            throw new IOException("Expected UTF-8 header when decoding UTF-8 message");
        }
    }

    private MessageType readMessageType(BufferedReader reader) throws IOException {
        String l = reader.readLine();
        MessageType t;
        try {
            if ( l.length() <= LENGTH_OF_MESSAGE_TYPE_PREFIX) {
                throw new IOException("MSGTYPE field too short");
            }
            String messageType = l.substring(LENGTH_OF_MESSAGE_TYPE_PREFIX);
            t = MessageType.valueOf(messageType);
        } catch (Exception i) {
            throw new IOException("Unrecognised message type " + l + " when decoding UTF-8 message", i);
        }
        return t;
    }

    private AbstractUtf8Message createMessage(MessageType t) throws IOException {
        AbstractUtf8Message message;
        switch(t) {
            case CLIENT_ANNOUNCE :
                message = new Utf8ClientAnnouncementMessage();
                break;
            case SERVER_ANNOUNCE:
                message = new Utf8HttpServerAnnouncementMessage();
                break;
            case TS_VALUE:
                message = new Utf8TimeSeriesValueMessage();
                break;
            case SERIES_DESCRIPTION:
                message = new Utf8DescriptionMessage();
                break;
            default :
                throw new IOException("Unsupported message type for UTF-8 message decoding");
        }
        return message;
    }

    public String toString() {
        return getClass().getSimpleName() + System.identityHashCode(this);
    }

}
