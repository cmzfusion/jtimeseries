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

    public static final String PORT_FIELD_KEY = "PORT";
    public static final String DESCRIPTION_FIELD_KEY = "DESCRIPTION";

    private String description;
    private int port = -1;

    Utf8AnnouncementMessage() {}

    Utf8AnnouncementMessage(int httpdPort, String hostname, String description) {
        super(hostname);
        this.port = httpdPort;
        this.description = description;
    }

    protected void writeBodyFieldsToOutputStream(Writer writer, StringBuilder sb) throws IOException {
        appendValue(writer, PORT_FIELD_KEY, String.valueOf(port), sb);
        appendValue(writer, DESCRIPTION_FIELD_KEY, description, sb);
    }

    public int getPort() {
        return port;
    }

    public String getDescription() {
        return description;
    }

    protected void doSetProperties(String key, String value) throws IOException {
        if ( PORT_FIELD_KEY.equals(key)) {
            port = parseInteger(PORT_FIELD_KEY, value);
        } else if ( DESCRIPTION_FIELD_KEY.equals(key)) {
            description = value;
        }
    }

    protected void doPostDeserialize() throws IOException {
        if ( port == -1 ) {
            throw new IOException(PORT_FIELD_KEY + " field not set in message " + getMessageType());
        } else if ( description == null) {
            throw new IOException(DESCRIPTION_FIELD_KEY + " field not set in message " + getMessageType());
        }
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || ! (o instanceof AnnouncementMessage)) return false;
        if (!super.equals(o)) return false;

        AnnouncementMessage that = (AnnouncementMessage) o;

        if (getPort() != that.getPort()) return false;
        if (getDescription() != null ? !getDescription().equals(that.getDescription()) : that.getDescription() != null) return false;

        return true;
    }

    public int hashCode() {
        int result = getPort();
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        return result;
    }
}
