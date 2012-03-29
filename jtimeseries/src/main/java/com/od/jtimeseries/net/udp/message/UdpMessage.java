package com.od.jtimeseries.net.udp.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 14/03/12
 * Time: 18:22
 *
 * Mandatory fields:
 *
 * Optional fields:
 * SourceDescription        -  a description of the agent sending the update
 */
public interface UdpMessage {

    /**
     * @return the ip address of the host which sent the UDP message
     */
    String getSourceInetAddress();

    /**
     * Set the source inet address when a message is received
     */
    void setSourceInetAddress(String sourceInetAddress);

    /**
     * @return Host sending the message, or sourceInetAddress if sourceHost was not set
     */
    String getSourceHostname();

    /**
     * Serialize the message to outputStream
     */
    void serialize(OutputStream outputStream) throws IOException;

    /**
     * @return maximum expected size for the serialized message in bytes
     * this is used as an initial buffer size when buffering the message for output,
     * a value slightly over the actual size will be most efficient
     */
    int getMaxExpectedSize();

    /**
     * @return type of this message
     */
    MessageType getMessageType();

    /**
     * @return encoding of this message
     */
    Encoding getEncoding();

    /**
     * Not all messages can be chained, this depends whether a message length is encoded with the message content,
     * and if the messageFactory has support to decode multiple messages. Including the message length is not so easy
     * when for example a text based protocol is used - it is nice, for example, to be able to send a message via
     * netcat without computing a length in bytes
     *
     * In some message types sent as UDP packet, there is an assumption that just one message will be sent per datagram
     * so streaming is not supported
     *
     * @return true, if the message factory can support decoding multiple messages from a single stream
     */
    boolean isMessageStreamingSupported();
}
