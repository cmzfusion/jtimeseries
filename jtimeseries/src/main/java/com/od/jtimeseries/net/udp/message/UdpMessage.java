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

    public static final String ENCODING_FIELD_KEY = "ENCODING";
    public static final String MSGTYPE_FIELD_KEY = "MSGTYPE";

    /**
     * Note on choice of MAX_PACKET_SIZE,
     * http://book.javanb.com/java-network-programming-3rd/javanp3-CHP-13-SECT-2.html
     * The theoretical limit for an IPv4 datagram is 65,507 bytes of data, and a DatagramPacket with
     * a 65,507-byte buffer can receive any possible IPv4 datagram without losing data. IPv6 datagrams
     * raise the theoretical limit to 65,536 bytes. In practice, however, many UDP-based protocols such
     * as DNS and TFTP use packets with 512 bytes of data per datagram or fewer. The largest data size
     * in common usage is 8,192 bytes for NFS. Almost all UDP datagrams you're likely to encounter will
     * have 8K of data or fewer. In fact, many operating systems don't support UDP datagrams with more
     * than 8K of data and either truncate, split, or discard larger datagrams. If a large datagram is
     * too big and as a result the network truncates or drops it, your Java program won't be notified
     * of the problem. (UDP is an unreliable protocol, after all.)
     * Consequently, you shouldn't create DatagramPacket objects with more than 8,192 bytes of data.
     */
    int MAX_PACKET_SIZE_BYTES = 8192;

    /**
     * @return the ip address of the host which sent the UDP message
     */
    String getSourceInetAddress();

    /**
     * Set the source inet address when a message is received
     */
    void setSourceInetAddress(String sourceInetAddress);

    /**
     * @return Description of agent sending message, or null if a source description was not provided with the message
     */
    String getSourceDescription();

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


    MessageType getMessageType();

}
