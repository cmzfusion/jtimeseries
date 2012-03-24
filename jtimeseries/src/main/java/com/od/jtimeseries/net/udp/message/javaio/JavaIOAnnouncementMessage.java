package com.od.jtimeseries.net.udp.message.javaio;

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

    protected JavaIOAnnouncementMessage(int port, String description) {
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

    protected void deserialize(DataInputStream is, char acronymVersion) throws IOException {
        port = is.readInt();
        description = is.readUTF();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        JavaIOAnnouncementMessage that = (JavaIOAnnouncementMessage) o;

        if (port != that.port) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = port;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}
