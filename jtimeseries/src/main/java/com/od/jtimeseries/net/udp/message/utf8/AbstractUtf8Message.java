package com.od.jtimeseries.net.udp.message.utf8;

import com.od.jtimeseries.net.udp.message.UdpMessage;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 16/03/12
 * Time: 17:59
 *
 * A message sent as a UTF-8 encoded String
 *
 * This will be read line by line, and the expected form for each line is KEY=VALUE
 * The first two lines must be ENCODING and MSGTYPE
 *
 * Subsequent lines may be any supported keys/values for the message type
 *
 * ENCODING=UTF-8
 * MSGTYPE=${messageType}
 * SRC_DESC=${Description of sending agent}
 * KEY=VALUE
 * KEY=VALUE
 * KEY=VALUE
 * ...
 *
 */
public abstract class AbstractUtf8Message implements UdpMessage {

    static final String SOURCE_DESCRPITION_FIELD_KEY = "SRC_DESC";

    private String newLine = System.getProperty("line.separator");
    protected String sourceInetAddress;
    private String sourceDescription;

    public void serialize(OutputStream outputStream) throws IOException {
        Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

        //pass around a stringbuilder for reuse/efficiency when writing fields
        StringBuilder sb = new StringBuilder();
        writeHeaderFieldsToOutputStream(writer, sb);
        writeBodyFieldsToOutputStream(writer, sb);
        writer.flush();
    }

    private void writeHeaderFieldsToOutputStream(Writer writer, StringBuilder sb) throws IOException {
        appendValue(writer, ENCODING_FIELD_KEY, "UTF-8", sb);
        appendValue(writer, MSGTYPE_FIELD_KEY, getMessageType().name(), sb);
        appendValue(writer, SOURCE_DESCRPITION_FIELD_KEY, getSourceDescription(), sb);
    }

    protected void appendValue(Writer writer, String key, String value, StringBuilder sb) throws IOException {
        if ( value != null) {
            sb.setLength(0);
            sb.append(key).append("=").append(value).append(newLine);
            writer.append(sb);
        }
    }

    protected abstract void writeBodyFieldsToOutputStream(Writer writer, StringBuilder sb) throws IOException;

    public int getMaxExpectedSize() {
        return 256;
    }

    public String getSourceInetAddress() {
        return sourceInetAddress;
    }

    public void setSourceInetAddress(String sourceInetAddress) {
        this.sourceInetAddress = sourceInetAddress;
    }

    public String getSourceDescription() {
        return sourceDescription;
    }

    public void setSourceDescription(String sourceDescription) {
        this.sourceDescription = sourceDescription;
    }
}
