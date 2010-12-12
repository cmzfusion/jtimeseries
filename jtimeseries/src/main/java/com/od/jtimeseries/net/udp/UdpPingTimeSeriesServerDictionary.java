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
public class UdpPingTimeSeriesServerDictionary implements TimeSeriesServerDictionary, UdpServer.UdpMessageListener {
    
    private static LogMethods logMethods = LogUtils.getLogMethods(UdpPingTimeSeriesServerDictionary.class);

    private final Set<TimeSeriesServer> serverSet = Collections.synchronizedSet(new TreeSet<TimeSeriesServer>());

    public List<TimeSeriesServer> getKnownTimeSeriesServer() {
        ArrayList<TimeSeriesServer> servers = new ArrayList<TimeSeriesServer>();
        synchronized (serverSet) {
            servers.addAll(serverSet);
        }
        return servers;
    }

    public void udpMessageReceived(UdpMessage udpMessage) {
        if ( udpMessage instanceof HttpServerAnnouncementMessage) {
            TimeSeriesServer remoteTimeSeriesServer = null;
            try {
                remoteTimeSeriesServer = TimeSeriesServer.create((HttpServerAnnouncementMessage)udpMessage);
                addServer(remoteTimeSeriesServer);
            } catch (UnknownHostException e) {
                logMethods.logError("Failed to add TimeSeriesServer ", e);
            }
        }
    }

    public boolean removeServer(TimeSeriesServer server) {
        boolean removed = serverSet.remove(server);
        if ( removed ) {
            serverRemoved(server);
        }
        return removed;
    }

    //hook for subclasses to take action on server removal
    protected void serverRemoved(TimeSeriesServer server) {
    }

    public boolean addServer(TimeSeriesServer remoteTimeSeriesServer) {
        if ( ! serverSet.remove(remoteTimeSeriesServer) ) {
            logMethods.logDebug("New TimeSeriesServer " + remoteTimeSeriesServer);
        }
        boolean added = serverSet.add(remoteTimeSeriesServer);
        if ( added ) {
            serverAdded(remoteTimeSeriesServer);
        }
        return added;
    }

    //hook for subclasses to take action on server addition
    protected void serverAdded(TimeSeriesServer server) {
    }
}
