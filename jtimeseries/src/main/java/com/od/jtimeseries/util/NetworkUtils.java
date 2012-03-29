package com.od.jtimeseries.util;

import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 28/03/12
 * Time: 11:31
 *
 * Find the local hostname once, share the value
 */
public class NetworkUtils {

    private static final LogMethods logMethods = LogUtils.getLogMethods(NetworkUtils.class);

    private static String hostname = "";
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
    public static final int MAX_ALLOWABLE_PACKET_SIZE_BYTES = 8192;

    public static final String MAX_BYTES_PER_DATAGRAM_PROPERTY="maxBytesPerDatagram";
    private static volatile int maxBytesPerDatagramPacket;
    static {
        try {
            maxBytesPerDatagramPacket = Integer.parseInt(System.getProperty(MAX_BYTES_PER_DATAGRAM_PROPERTY, "512"));
        } catch (NumberFormatException e) {
            logMethods.error("System property " + MAX_BYTES_PER_DATAGRAM_PROPERTY + " must be an integer value");
        }
        checkMaxSize();
    }

    private static void checkMaxSize() {
        if ( maxBytesPerDatagramPacket > MAX_ALLOWABLE_PACKET_SIZE_BYTES) {
            maxBytesPerDatagramPacket = MAX_ALLOWABLE_PACKET_SIZE_BYTES;
            logMethods.warn("Set the " + MAX_BYTES_PER_DATAGRAM_PROPERTY + " to the max theoretical size which is " + MAX_ALLOWABLE_PACKET_SIZE_BYTES);
        }
    }

    static {
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            logMethods.error("Could not find inet address for Utf8MessageFactory", e);
        }
    }

    public static String getLocalHostname() {
        return hostname;
    }

    public static int getMaxBytesPerDatagramPacket() {
        return maxBytesPerDatagramPacket;
    }

    public static void setMaxBytesPerDatagramPacket(int maxBytesPerDatagramPacket) {
        NetworkUtils.maxBytesPerDatagramPacket = maxBytesPerDatagramPacket;
        checkMaxSize();
    }
}
