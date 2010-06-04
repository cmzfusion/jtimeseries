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
package com.od.jtimeseries.agent.jmx;

import com.od.jtimeseries.net.udp.UdpClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 11-Jun-2009
 * Time: 15:05:09
 */
public class AgentConfigJmx implements ServerConfigJmxMBean {

    private String agentName;
    private int httpdDaemonPort;
    private int jmxHttpdPort;

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String serverName) {
        this.agentName = serverName;
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

}