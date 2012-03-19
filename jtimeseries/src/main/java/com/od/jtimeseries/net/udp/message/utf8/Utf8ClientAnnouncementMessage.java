package com.od.jtimeseries.net.udp.message.utf8;

import com.od.jtimeseries.net.udp.message.MessageType;

import java.io.IOException;
import java.io.Writer;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 19/03/12
 * Time: 06:25
 *
 * Announce to a timeseries server that a client exists
 */
public class Utf8ClientAnnouncementMessage extends Utf8AnnouncementMessage {

    public MessageType getMessageType() {
        return MessageType.CLIENT_ANNOUNCE;
    }
}
