package com.od.jtimeseries.net.udp.message.javaio;

import com.od.jtimeseries.net.udp.message.ClientAnnouncementMessage;
import com.od.jtimeseries.net.udp.message.HttpServerAnnouncementMessage;
import com.od.jtimeseries.net.udp.message.MessageType;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 23/03/12
 * Time: 18:00
 */
public class JavaIOClientAnnouncementMessage extends JavaIOAnnouncementMessage implements ClientAnnouncementMessage {

    private static byte[] HEADER_ACRONYM = new byte[] { 'C', '0' };

    public JavaIOClientAnnouncementMessage(int port, String description) {
        super(port, description);
    }

    public JavaIOClientAnnouncementMessage() {
    }

    public MessageType getMessageType() {
        return MessageType.CLIENT_ANNOUNCE;
    }

    protected byte[] getHeaderAcronym() {
        return HEADER_ACRONYM;
    }
}
