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
package com.od.jtimeseries.server;

import com.od.jtimeseries.net.udp.ClientAnnouncementMessage;
import com.od.jtimeseries.net.udp.UdpClient;
import com.od.jtimeseries.net.udp.UdpMessage;
import com.od.jtimeseries.net.udp.UdpServer;
import com.od.jtimeseries.util.logging.LogDefaults;
import com.od.jtimeseries.util.logging.LogMethods;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 24-Jun-2009
 * Time: 13:02:54
 * To change this template use File | Settings | File Templates.
 */
public class ClientAnnouncementMessageListener implements UdpServer.UdpMessageListener {

    private UdpClient clientToSendUdpServerAnnouncePings;
    private LogMethods logMethod = LogDefaults.getDefaultLogMethods(ClientAnnouncementMessageListener.class);

    public ClientAnnouncementMessageListener(UdpClient clientToSendUdpServerAnnouncePings) {
        this.clientToSendUdpServerAnnouncePings = clientToSendUdpServerAnnouncePings;
    }

    public void udpMessageReceived(UdpMessage m) {
        if ( m instanceof ClientAnnouncementMessage) {
            ClientAnnouncementMessage c = (ClientAnnouncementMessage)m;
            try {
                boolean added = clientToSendUdpServerAnnouncePings.addClientConfig(
                    new UdpClient.ClientConfig(
                        InetAddress.getByName(c.getInetAddress()),
                        c.getPort()
                    )
                );
                if ( added ) {
                    logMethod.logInfo("New client ping received for " + c.getHostname() + " port " + c.getPort());
                }
            } catch (UnknownHostException e) {
                logMethod.logError("Failed to find host " + c.getInetAddress() + " for client ping", e);
            }
        }
    }
}
