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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 13-Jan-2009
 * Time: 12:38:49
 */
public class RemoteHttpServer implements Comparable {

    private final InetAddress serverAddress;
    private final int port;

    private String description;
    private long lastAnnounceTimestamp;

    public RemoteHttpServer(InetAddress serverAddress, int port, String description, long lastAnnounceTimestamp) {
        this.serverAddress = serverAddress;
        this.port = port;
        this.description = description;
        this.lastAnnounceTimestamp = lastAnnounceTimestamp;
    }

    public void setLastAnnounceTimestamp(long lastAnnounceTimestamp) {
        this.lastAnnounceTimestamp = lastAnnounceTimestamp;
    }

    public InetAddress getServerAddress() {
        return serverAddress;
    }

    public int getPort() {
        return port;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddressAndPort() {
        return serverAddress + ":" + port;
    }

    public long getLastAnnounceTimestamp() {
        return lastAnnounceTimestamp;
    }

    public int hashCode() {
        int result = 17;
        result = 31 * result + serverAddress.hashCode();
        result = 31 * result + port;
        return result;
    }

    public boolean equals(Object o) {
        boolean result = false;
        if ( o == this ) {
            result = true;
        } else if ( o instanceof RemoteHttpServer) {
            RemoteHttpServer r = (RemoteHttpServer)o;
            result = serverAddress.equals(r.getServerAddress()) && port == r.getPort();
        }
        return result;
    }

    public int compareTo(Object o) {
        RemoteHttpServer r = (RemoteHttpServer)o;
        if ( this.equals(o)) {
            return 0;
        }  else {
            return r.getDescription().compareTo(getDescription());
        }
    }

    @Override
    public String toString() {
        return "RemoteHttpServer{" +
                "serverAddress=" + serverAddress +
                ", port=" + port +
                ", description='" + description + '\'' +
                ", lastAnnounceTimestamp=" + new Date(lastAnnounceTimestamp) +
                '}';
    }

    public static RemoteHttpServer createRemoteTimeSeriesServer(AnnouncementMessage p) throws UnknownHostException {
        InetAddress i = InetAddress.getByName(p.getInetAddress());
        int port = p.getPort();
        String description = p.getDescription();
        return new RemoteHttpServer(i, port, description, System.currentTimeMillis());
    }
}
