package com.od.jtimeseries.net.udp.message.utf8;

import com.od.jtimeseries.net.udp.message.AnnouncementMessage;

import java.io.IOException;
import java.io.Writer;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 19/03/12
 * Time: 06:28
 *
 * Announce that a jtimeseries process is listening for UDP packets on a given port
 */
public abstract class Utf8AnnouncementMessage extends AbstractUtf8Message  implements AnnouncementMessage {

    private static final String HOST_FIELD_KEY = "HOST";
    private static final String PORT_FIELD_KEY = "PORT";
    private static final String DESCRIPTION_FIELD_KEY = "DESCRIPTION";

    private String description;
    private String host;
    private int port;

    public Utf8AnnouncementMessage(int httpdPort, String host) {
        this.port = httpdPort;
        this.host = host;
    }

    protected void writeBodyFieldsToOutputStream(Writer writer, StringBuilder sb) throws IOException {
        appendValue(writer, HOST_FIELD_KEY, host, sb);
        appendValue(writer, PORT_FIELD_KEY, String.valueOf(port), sb);
        appendValue(writer, DESCRIPTION_FIELD_KEY, description, sb);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getDescription() {
        return description;
    }
}
