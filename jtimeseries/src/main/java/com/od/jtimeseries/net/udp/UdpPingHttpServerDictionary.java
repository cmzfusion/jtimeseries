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
package com.od.jtimeseries.net.udp;

import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.util.logging.LogMethods;

import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 13-Jan-2009
 * Time: 12:37:59
 */
public class UdpPingHttpServerDictionary implements RemoteServerDictionary, UdpServer.UdpMessageListener {

    private final Set<RemoteHttpServer> serverSet = Collections.synchronizedSet(new TreeSet<RemoteHttpServer>());
    private LogMethods logMethods = LogUtils.getLogMethods(UdpPingHttpServerDictionary.class);

    public List<RemoteHttpServer> getKnownTimeSeriesServer() {
        ArrayList<RemoteHttpServer> servers = new ArrayList<RemoteHttpServer>();
        synchronized (serverSet) {
            servers.addAll(serverSet);
        }
        return servers;
    }

    public void udpMessageReceived(UdpMessage udpMessage) {
        if ( udpMessage instanceof AnnouncementMessage) {
            RemoteHttpServer remoteTimeSeriesServer = null;
            try {
                remoteTimeSeriesServer = RemoteHttpServer.createRemoteTimeSeriesServer((AnnouncementMessage)udpMessage);
                if ( ! serverSet.remove(remoteTimeSeriesServer) ) {
                    logMethods.logDebug("New RemoteHttpServer " + remoteTimeSeriesServer);
                }
                serverSet.add(remoteTimeSeriesServer);
            } catch (UnknownHostException e) {
                logMethods.logError("Failed to add RemoteHttpServer ", e);
            }
        }
    }
}
