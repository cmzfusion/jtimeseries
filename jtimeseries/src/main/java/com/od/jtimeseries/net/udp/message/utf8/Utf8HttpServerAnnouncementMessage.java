package com.od.jtimeseries.net.udp.message.utf8;

import com.od.jtimeseries.net.udp.message.HttpServerAnnouncementMessage;
import com.od.jtimeseries.net.udp.message.MessageType;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 19/03/12
 * Time: 06:35
 *
 * Announce to a client that a timeseries http server is active
 */
public class Utf8HttpServerAnnouncementMessage extends Utf8AnnouncementMessage implements HttpServerAnnouncementMessage {

    Utf8HttpServerAnnouncementMessage() {}

    Utf8HttpServerAnnouncementMessage(int httpdPort, String hostname, String description) {
        super(httpdPort, hostname, description);
    }

    public MessageType getMessageType() {
        return MessageType.SERVER_ANNOUNCE;
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
