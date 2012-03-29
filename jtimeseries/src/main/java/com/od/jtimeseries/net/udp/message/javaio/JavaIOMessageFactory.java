package com.od.jtimeseries.net.udp.message.javaio;

import com.od.jtimeseries.net.udp.message.*;
import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.util.NetworkUtils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 23/03/12
 * Time: 18:11
 */
public class JavaIOMessageFactory implements UdpMessageFactory {

    public TimeSeriesValueMessage createTimeSeriesValueMessage(String path, TimeSeriesItem timeSeriesItem) {
        return new JavaIOTimeSeriesValueMessage(NetworkUtils.getLocalHostname(),path, timeSeriesItem);
    }

    public SeriesDescriptionMessage createTimeSeriesDescriptionMessage(String path, String description) {
        return new JavaIODescriptionMessage(NetworkUtils.getLocalHostname(), path, description);
    }

    public HttpServerAnnouncementMessage createHttpServerAnnouncementMessage(int httpdPort, String serverName) {
        return new JavaIOHttpServerAnnouncementMessage(NetworkUtils.getLocalHostname(), httpdPort, serverName);
    }

    public List<UdpMessage> deserializeFromDatagram(byte[] buffer, int length) throws IOException {
        List<UdpMessage> messages = new LinkedList<UdpMessage>();
        DataInputStream is = new DataInputStream(new ByteArrayInputStream(buffer, 0, length));
        while(is.available() > 0) {
            byte[] header = new byte[3];
            is.readFully(header);
            if ( ! Arrays.equals(header, AbstractJavaIOMessage.JAVA_IO_MESSAGE_HEADER)) {
                throw new IOException("Message does not start with JAVA_IO_MESSAGE_HEADER, instead " + header);
            }

            byte[] messageAcronym = new byte[2];
            is.readFully(messageAcronym);
            //the first byte is a ASCII character which indicates message type
            //the second is a ASCII number which may in the future be used to indicate version

            AbstractJavaIOMessage message;
            switch(messageAcronym[0]) {
                case 'V' :
                    message = new JavaIOTimeSeriesValueMessage();
                    break;
                case 'S' :
                    message = new JavaIOHttpServerAnnouncementMessage();
                    break;
                case 'C' :
                    message = new JavaIOClientAnnouncementMessage();
                    break;
                case 'D' :
                    message  = new JavaIODescriptionMessage();
                    break;
                default :
                    throw new IOException("Unidentified Java IO Message type with acronym " + messageAcronym);
            }
            message.deserialize(is, (char) messageAcronym[1]);
            messages.add(message);
        }
        return messages;
    }

    public ClientAnnouncementMessage createClientAnnouncementMessage(int port, String description) {
        return new JavaIOClientAnnouncementMessage(NetworkUtils.getLocalHostname(), port, description);
    }

    public String toString() {
        return getClass().getSimpleName() + System.identityHashCode(this);
    }
}
