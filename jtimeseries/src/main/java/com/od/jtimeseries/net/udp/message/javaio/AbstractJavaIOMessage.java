package com.od.jtimeseries.net.udp.message.javaio;

import com.od.jtimeseries.net.udp.message.Encoding;
import com.od.jtimeseries.net.udp.message.UdpMessage;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 21/03/12
 * Time: 21:03
 *
 * A binary encoded message which can be streamed
 */
public abstract class AbstractJavaIOMessage implements UdpMessage {

    private String sourceInetAddress;
    private String sourceHostname;

    private static final byte[] JAVA_IO_MESSAGE_HEADER = new byte[] { 'J', 'I', 'O' };

    protected AbstractJavaIOMessage() {
    }

    public String getSourceInetAddress() {
        return sourceInetAddress;
    }

    public void setSourceInetAddress(String sourceInetAddress) {
        this.sourceInetAddress = sourceInetAddress;
    }

    public String getSourceHostname() {
        return this.sourceHostname == null ? sourceInetAddress : sourceHostname;
    }

    public void setSourceHostname(String sourceHostname) {
        this.sourceHostname = sourceHostname;
    }

    public void serialize(OutputStream outputStream) throws IOException {
        writeHeaderFields(outputStream);
        writeBody(outputStream);
    }

    private void writeBody(OutputStream outputStream) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(32);
        DataOutputStream d = new DataOutputStream(bos);
        doSerializeMessageBody(d);
        outputStream.write(bos.toByteArray());
    }

    protected abstract void doSerializeMessageBody(DataOutputStream bos) throws IOException;

    protected abstract void deserialize(DataInputStream is) throws IOException;

    protected void writeHeaderFields(OutputStream outputStream) throws IOException {
        outputStream.write(AbstractJavaIOMessage.JAVA_IO_MESSAGE_HEADER);
        outputStream.write(getMessageType().getAcronym());
    }

    public int getMaxExpectedSize() {
        return 32; //TODO
    }

    public Encoding getEncoding() {
        return Encoding.JAVA_IO;
    }

    public boolean isMessageStreamingSupported() {
        return true;
    }
}
