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
package com.od.jtimeseries.server.jmx;

import com.od.jtimeseries.net.udp.UdpClient;
import com.od.jtimeseries.net.udp.UdpClientConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 11-Jun-2009
 * Time: 15:05:09
 */
public class ServerConfigJmx implements ServerConfigJmxMBean {

    private UdpClient udpClientForServerAnnounceMessages;

    private String serverName;
    private int httpdDaemonPort;
    private int jmxHttpdPort;
    private int udpServerPort;
    private int secondsToStartServer;

    public ServerConfigJmx(UdpClient udpClientForServerAnnounceMessages) {
        this.udpClientForServerAnnounceMessages = udpClientForServerAnnounceMessages;
    }

    public UdpClient getUdpClientForServerAnnounceMessages() {
        return udpClientForServerAnnounceMessages;
    }

    public void setUdpClientForServerAnnounceMessages(UdpClient udpClientForServerAnnounceMessages) {
        this.udpClientForServerAnnounceMessages = udpClientForServerAnnounceMessages;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public int getHttpdDaemonPort() {
        return httpdDaemonPort;
    }

    public void setHttpdDaemonPort(int httpdDaemonPort) {
        this.httpdDaemonPort = httpdDaemonPort;
    }

    public int getJmxHttpdPort() {
        return jmxHttpdPort;
    }

    public void setJmxHttpdPort(int jmxHttpdPort) {
        this.jmxHttpdPort = jmxHttpdPort;
    }

    public int getUdpServerPort() {
        return udpServerPort;
    }

    public void setUdpServerPort(int udpServerPort) {
        this.udpServerPort = udpServerPort;
    }

    public int getSecondsToStartServer() {
        return secondsToStartServer;
    }

    public void setSecondsToStartServer(int secondsToStartServer) {
        this.secondsToStartServer = secondsToStartServer;
    }

    public String[] getPingHostnames() {
        List<String> hosts = new ArrayList<String>();
        for (UdpClientConfig c : udpClientForServerAnnounceMessages.getClientConfigs()) {
            hosts.add(c.toString());
        }
        return hosts.toArray(new String[hosts.size()]);
    }

}
