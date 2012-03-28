package com.od.jtimeseries.net.udp.message.javaio;

import com.od.jtimeseries.net.udp.message.HttpServerAnnouncementMessage;
import com.od.jtimeseries.net.udp.message.MessageType;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 23/03/12
 * Time: 18:00
 */
public class JavaIOHttpServerAnnouncementMessage extends JavaIOAnnouncementMessage implements HttpServerAnnouncementMessage {

    private static byte[] HEADER_ACRONYM = new byte[] { 'S', '0' };

    public JavaIOHttpServerAnnouncementMessage(String sourceHostname, int port, String description) {
        super(sourceHostname, port, description);
    }

    public JavaIOHttpServerAnnouncementMessage() {
    }

    public MessageType getMessageType() {
        return MessageType.SERVER_ANNOUNCE;
    }

    protected byte[] getHeaderAcronym() {
        return HEADER_ACRONYM;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || ! ( o instanceof HttpServerAnnouncementMessage)) return false;
        if (!super.equals(o)) return false;
        return true;
    }

    public String toString() {
        return getClass().getSimpleName() + "{" +
                super.toString() +
                "} ";
    }

}
