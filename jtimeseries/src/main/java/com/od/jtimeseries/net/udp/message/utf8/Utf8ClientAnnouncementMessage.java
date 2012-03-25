package com.od.jtimeseries.net.udp.message.utf8;

import com.od.jtimeseries.net.udp.message.ClientAnnouncementMessage;
import com.od.jtimeseries.net.udp.message.MessageType;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 19/03/12
 * Time: 06:25
 *
 * Announce to a timeseries server that a client exists
 */
public class Utf8ClientAnnouncementMessage extends Utf8AnnouncementMessage implements ClientAnnouncementMessage {

    Utf8ClientAnnouncementMessage() {}

    Utf8ClientAnnouncementMessage(int udpPort, String hostname, String description) {
        super(udpPort, hostname, description);
    }

    public MessageType getMessageType() {
        return MessageType.CLIENT_ANNOUNCE;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || ! (o instanceof ClientAnnouncementMessage)) return false;
        if (!super.equals(o)) return false;
        return true;
    }

    public String toString() {
        return getClass().getSimpleName() + "{" +
                super.toString() +
                "} ";
    }
}
