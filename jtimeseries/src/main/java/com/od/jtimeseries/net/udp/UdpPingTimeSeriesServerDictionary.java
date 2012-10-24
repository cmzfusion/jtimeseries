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

import com.od.jtimeseries.net.udp.message.HttpServerAnnouncementMessage;
import com.od.jtimeseries.net.udp.message.UdpMessage;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 13-Jan-2009
 * Time: 12:37:59
 */
public class UdpPingTimeSeriesServerDictionary implements TimeSeriesServerDictionary, UdpServer.UdpMessageListener {
    
    private static LogMethods logMethods = LogUtils.getLogMethods(UdpPingTimeSeriesServerDictionary.class);

    private final Map<TimeSeriesServer.ServerKey, TimeSeriesServer> serverKeyToServer = Collections.synchronizedMap(new HashMap<TimeSeriesServer.ServerKey, TimeSeriesServer>());
    private final Map<String, InetAddress> hostnameToInetAddress = Collections.synchronizedMap(new HashMap<String, InetAddress>());

    public List<TimeSeriesServer> getKnownTimeSeriesServer() {
        ArrayList<TimeSeriesServer> servers = new ArrayList<TimeSeriesServer>();
        synchronized (serverKeyToServer) {
            servers.addAll(serverKeyToServer.values());
        }
        return servers;
    }

    /**
     * If there is an existing server for this host and port, return it
     * In this case the returned server may have a description which differs from the description supplied
     *
     * If there is no existing server for this hort and port, create one, with the description provided
     *
     * @param description, description to use when creating a new server
     * @return The existing TimeSeriesServer for this host and port, or a new server with the description provided
     * @throws UnknownHostException
     */
    public TimeSeriesServer getOrCreateServer(String host, int port, String description) throws UnknownHostException {
        InetAddress i = getInetAddress(host);
        TimeSeriesServer.ServerKey s = new TimeSeriesServer.ServerKey(i, port);
        synchronized (serverKeyToServer) {
            TimeSeriesServer server = serverKeyToServer.get(s);
            if (server == null) {
                server = new TimeSeriesServer(host, port, description);
                addServer(server);
            }
            return server;
        }
    }

    public boolean serverExists(String host, int port) throws UnknownHostException {
        InetAddress i = getInetAddress(host);
        return serverKeyToServer.containsKey(new TimeSeriesServer.ServerKey(i, port));
    }

    private InetAddress getInetAddress(String host) throws UnknownHostException {
        InetAddress i = hostnameToInetAddress.get(host);
        if ( i == null ) {
            i = InetAddress.getByName(host);
            hostnameToInetAddress.put(host, i);
        }
        return i;
    }

    public void udpMessageReceived(UdpMessage udpMessage) {
        if ( udpMessage instanceof HttpServerAnnouncementMessage) {
            TimeSeriesServer remoteTimeSeriesServer = null;
            try {
                remoteTimeSeriesServer = TimeSeriesServer.create((HttpServerAnnouncementMessage)udpMessage);
                addServer(remoteTimeSeriesServer);
            } catch (UnknownHostException e) {
                logMethods.error("Failed to add TimeSeriesServer ", e);
            }
        }
    }

    public boolean removeServer(TimeSeriesServer server) {
        boolean removed = false;
        TimeSeriesServer s = serverKeyToServer.remove(server.getServerKey());
        if ( s != null ) {
            serverRemoved(server);
            removed = true;
        }
        return removed;
    }

    //hook for subclasses to take action on server removal
    protected void serverRemoved(TimeSeriesServer server) {
    }

    public boolean addServer(TimeSeriesServer s) {
        boolean added = false;
        synchronized (serverKeyToServer) {
            if (! serverKeyToServer.containsKey(s.getServerKey())) {
                serverKeyToServer.put(s.getServerKey(), s);
                added = true;
            }
        }
        if ( added ) {
            serverAdded(s);
        }
        return added;
    }

    //hook for subclasses to take action on server addition
    protected void serverAdded(TimeSeriesServer server) {
    }
}
