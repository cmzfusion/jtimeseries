/**
 * Copyright (C) 2009 (nick @ objectdefinitions.com)
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
import com.od.jtimeseries.server.util.TimeSeriesServerConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 11-Jun-2009
 * Time: 15:05:09
 */
public class ServerConfigJmx implements ServerConfigJmxMBean {

    private TimeSeriesServerConfig timeSeriesServerConfig;
    private UdpClient udpClientForServerAnnounceMessages;

    public ServerConfigJmx(TimeSeriesServerConfig timeSeriesServerConfig, UdpClient udpClientForServerAnnounceMessages) {
        this.timeSeriesServerConfig = timeSeriesServerConfig;
        this.udpClientForServerAnnounceMessages = udpClientForServerAnnounceMessages;
    }

    public String getServerName() {
        return timeSeriesServerConfig.getServerName();
    }

    public int getHttpdDaemonPort() {
        return timeSeriesServerConfig.getHttpdDaemonPort();
    }

    public int getJmxHttpdPort() {
        return timeSeriesServerConfig.getJmxHttpdPort();
    }

    public int getUdpServerPort() {
        return timeSeriesServerConfig.getUdpServerPort();
    }

    public int getSecondsToStartServer() {
        return timeSeriesServerConfig.getSecondsToStartServer();
    }

    public String[] getPingHostnames() {
        List<String> hosts = new ArrayList<String>();
        for (UdpClient.ClientConfig c : udpClientForServerAnnounceMessages.getClientConfigs()) {
            hosts.add(c.toString());
        }
        return hosts.toArray(new String[hosts.size()]);
    }

}
