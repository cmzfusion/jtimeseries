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
}
