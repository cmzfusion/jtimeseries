package com.od.jtimeseries.net.udp.message.javaio;

import com.od.jtimeseries.net.udp.message.MessageType;
import com.od.jtimeseries.net.udp.message.SeriesDescriptionMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 23/03/12
 * Time: 18:00
 */
public class JavaIODescriptionMessage extends AbstractJavaIOMessage implements SeriesDescriptionMessage {

    private static byte[] HEADER_ACRONYM = new byte[] { 'D', '0' };

    private String description;
    private String path;

    public JavaIODescriptionMessage(String path, String description) {
        this.path = path;
        this.description = description;
    }

    public JavaIODescriptionMessage() {
    }

    public MessageType getMessageType() {
        return MessageType.CLIENT_ANNOUNCE;
    }

    public String getSeriesDescription() {
        return description;
    }

    public String getSeriesPath() {
        return path;
    }

    protected void doSerializeMessageBody(DataOutputStream bos) throws IOException {
        bos.writeUTF(path);
        bos.writeUTF(description);
    }

    protected void deserialize(DataInputStream is, char acronymVersion) throws IOException {
        path = is.readUTF();
        description = is.readUTF();
    }

    protected byte[] getHeaderAcronym() {
        return HEADER_ACRONYM;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        JavaIODescriptionMessage that = (JavaIODescriptionMessage) o;

        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "JavaIODescriptionMessage{" +
                "description='" + description + '\'' +
                ", path='" + path + '\'' +
                super.toString() +
                "} " + super.toString();
    }
}
