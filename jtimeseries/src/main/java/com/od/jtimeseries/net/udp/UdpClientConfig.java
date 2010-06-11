package com.od.jtimeseries.net.udp;

import java.net.InetAddress;
import java.net.DatagramSocket;
import java.net.UnknownHostException;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 11-Jun-2010
* Time: 12:03:41
*
* Config to connect to create a message to ca  UdpServer
*/
public class UdpClientConfig {

    private InetAddress inetAddress;
    private int port;

    public UdpClientConfig(String address, int port) throws UnknownHostException {
        this(InetAddress.getByName(address), port);
    }

    public UdpClientConfig(InetAddress inetAddress, int port) {
        this.inetAddress = inetAddress;
        this.port = port;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public int getPort() {
        return port;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UdpClientConfig that = (UdpClientConfig) o;

        if (port != that.port) return false;
        if (inetAddress != null ? !inetAddress.equals(that.inetAddress) : that.inetAddress != null) return false;

        return true;
    }

    public int hashCode() {
        int result = inetAddress != null ? inetAddress.hashCode() : 0;
        result = 31 * result + port;
        return result;
    }

    public String toString() {
        return inetAddress.getHostName() + ":" + port;
    }
}
