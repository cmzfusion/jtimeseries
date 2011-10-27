/**
 * Copyright (C) 2011 (nick @ objectdefinitions.com)
 *
 * This file is part of JTimeseries.
 *
 * JTimeseries is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JTimeseries is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JTimeseries.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.od.jtimeseries.net.udp;

import java.net.InetAddress;
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
