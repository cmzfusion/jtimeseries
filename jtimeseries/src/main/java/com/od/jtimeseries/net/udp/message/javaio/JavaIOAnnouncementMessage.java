package com.od.jtimeseries.net.udp.message.javaio;

import com.od.jtimeseries.net.udp.message.AnnouncementMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 23/03/12
 * Time: 17:57
 */
public abstract class JavaIOAnnouncementMessage extends AbstractJavaIOMessage {

    private int port;
    private String description;

    protected JavaIOAnnouncementMessage(String sourceHostname, int port, String description) {
        super(sourceHostname);
        this.port = port;
        this.description = description;
    }

    protected JavaIOAnnouncementMessage() {}

    public int getPort() {
        return port;
    }

    public String getDescription() {
        return description;
    }

    protected void doSerializeMessageBody(DataOutputStream bos) throws IOException {
        bos.writeInt(port);
        bos.writeUTF(description);
    }

    protected void doDeserialize(DataInputStream is, char acronymVersion) throws IOException {
        port = is.readInt();
        description = is.readUTF();
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
