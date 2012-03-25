package com.od.jtimeseries.net.udp.message.utf8;

import com.od.jtimeseries.net.udp.message.Encoding;
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

    public static final String UTF8_ENCODING = "UTF-8";
    public static final String HOSTNAME_FIELD_KEY = "HOSTNAME";

    public static final String ENCODING_FIELD_KEY = "ENCODING";
    public static final String UTF8_ENCODING_HEADER_STRING = ENCODING_FIELD_KEY + "=UTF-8";
    public static final byte[] UTF8_ENCODING_HEADER_CHARS = UTF8_ENCODING_HEADER_STRING.getBytes();

    public static final String MSGTYPE_FIELD_KEY = "MSGTYPE";

    private String newLine = System.getProperty("line.separator");
    private String sourceInetAddress;
    private String hostname;

    AbstractUtf8Message() {
    }

    public AbstractUtf8Message(String hostname) {
        this.hostname = hostname;
    }

    public void serialize(OutputStream outputStream) throws IOException {
        Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

        //pass around a stringbuilder for reuse/efficiency when writing fields
        StringBuilder sb = new StringBuilder();
        writeHeaderFieldsToOutputStream(writer, sb);
        writeBodyFieldsToOutputStream(writer, sb);
        writer.flush();
    }

    private void writeHeaderFieldsToOutputStream(Writer writer, StringBuilder sb) throws IOException {
        //although written by the message class these are parsed/read by the Utf8MessageFactory
        //rather than local parsing logic
        appendValue(writer, ENCODING_FIELD_KEY, UTF8_ENCODING, sb);
        appendValue(writer, MSGTYPE_FIELD_KEY, getMessageType().name(), sb);

        //actually optional, but better to include it
        appendValue(writer, HOSTNAME_FIELD_KEY, getSourceHostname(), sb);
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

    public Encoding getEncoding() {
        return Encoding.UTF8;
    }

    public String getSourceInetAddress() {
        return sourceInetAddress;
    }

    public void setSourceInetAddress(String sourceInetAddress) {
        this.sourceInetAddress = sourceInetAddress;
    }

    public String getSourceHostname() {
        return hostname == null ? sourceInetAddress : hostname;
    }

    void deserialize(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        while(line != null) {
            parseLine(line);
            line = reader.readLine();
        }
        postDeserialize();
    }

    /**
     * Check required field values are set and do any post-initialization
     */
    private void postDeserialize() throws IOException {
        //add local checks here, no mandatory fields at this level so far
        doPostDeserialize();
    }

    /**
     * Check required field values are set and do any post-initialization
     */
    protected abstract void doPostDeserialize() throws IOException;

    private void parseLine(String line) throws IOException {
        int indexOfEquals = line.indexOf('=');
        String key, value;
        if ( indexOfEquals != -1) {
            key = line.substring(0, indexOfEquals);
            value = line.substring(indexOfEquals + 1, line.length());
            setProperties(key, value);
        }
    }

    private void setProperties(String key, String value) throws IOException {
        if ( HOSTNAME_FIELD_KEY.equals(key)) {
            hostname = value;
        }
        doSetProperties(key, value);
    }

    protected abstract void doSetProperties(String key, String value) throws IOException;

    protected int parseInteger(String key, String value) throws IOException {
        int result;
        try {
            result = Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            throw new IOException("Could not parse integer value for field " + key + " from value " + value);
        }
        return result;
    }

    protected long parseLong(String key, String value) throws IOException {
        long result;
        try {
            result = Long.parseLong(value);
        } catch (NumberFormatException nfe) {
            throw new IOException("Could not parse long value for field " + key + " from value " + value);
        }
        return result;
    }

    protected double parseDouble(String key, String value) throws IOException {
        double result;
        try {
            result = Double.parseDouble(value);
        } catch (NumberFormatException nfe) {
            throw new IOException("Could not parse double value for field " + key + " from value " + value);
        }
        return result;
    }

    public boolean isMessageStreamingSupported() {
        return false;
    }

    //source hostname and ip are not considered in equals comparison
    //this would be unhelpful for testing, since only set on server side on receipt
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || ! (o instanceof UdpMessage)) return false;
        return true;
    }


    public String toString() {
        return "sourceInetAddress='" + getSourceInetAddress() + '\'' +
                ", sourceHostname='" + getSourceHostname() + '\'';
    }
}
