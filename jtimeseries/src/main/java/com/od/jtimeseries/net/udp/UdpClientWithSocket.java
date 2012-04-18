package com.od.jtimeseries.net.udp;

import com.od.jtimeseries.util.logging.LimitedErrorLogger;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * A wrapper around UdpClientConfig which also maintains a DatagramSocket to send messages
 */
class UdpClientWithSocket {

    private static final LogMethods logMethods = LogUtils.getLogMethods(UdpClientWithSocket.class);

    private final LimitedErrorLogger limitedLogger = new LimitedErrorLogger(logMethods, 5, 100);
    private UdpClientConfig c;
    private DatagramSocket datagramSocket;

    UdpClientWithSocket(UdpClientConfig c) {
        this.c = c;
    }

    public int getPort() {
        return c.getPort();
    }

    public InetAddress getInetAddress() {
        return c.getInetAddress();
    }

    public DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }

    public void setDatagramSocket(DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
    }

    public UdpClientConfig getUdpClientConfig() {
        return c;
    }

    public void sendDatagram(byte[] data) {
        sendDatagram(data, data.length);
    }

    public void sendDatagram(byte[] data, int length) {
        DatagramPacket datagramPacket = new DatagramPacket(data, length, getInetAddress(), getPort());
        DatagramSocket socket = getDatagramSocket();

        try {
            if ( socket == null || socket.isClosed()) {
                socket = new DatagramSocket();
                setDatagramSocket(socket);
            }
            socket.send(datagramPacket);

        } catch (Throwable t) {
            limitedLogger.logError("Failed to send datagram", t);
            //close socket if there was an error
            closeSocket();
        }
    }

    public void closeSocket() {
        if ( datagramSocket != null) {
            try {
                datagramSocket.close();
            } catch (Throwable t) {
                limitedLogger.logError("Failed to close datagramSocket", t);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UdpClientWithSocket that = (UdpClientWithSocket) o;

        if (c != null ? !c.equals(that.c) : that.c != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = c != null ? c.hashCode() : 0;
        return result;
    }

}
