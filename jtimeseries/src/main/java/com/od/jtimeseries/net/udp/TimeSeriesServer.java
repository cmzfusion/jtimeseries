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

import com.od.jtimeseries.identifiable.IdentifiablePathUtils;
import com.od.jtimeseries.net.udp.message.AnnouncementMessage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 13-Jan-2009
 * Time: 12:38:49
 *
 * TimeSeriesServer is uniquely identified by a ServerKey (inet address and port)
 * - it also has several other attributes, e.g. description
 */
public class TimeSeriesServer implements Comparable {

    private ServerKey serverKey;
    private String description;
    private long lastAnnounceTimestamp;
    private boolean connectionFailed;

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private String hostName;

    public TimeSeriesServer(String hostName, int port, String description) throws UnknownHostException {
        this(hostName, port, description, 0);
    }

    public TimeSeriesServer(String hostName, int port, String description, long lastAnnounceTimestamp) throws UnknownHostException {
        this.hostName = hostName;
        this.serverKey = new ServerKey(InetAddress.getByName(hostName), port);
        this.description = description;
        this.lastAnnounceTimestamp = lastAnnounceTimestamp;
    }

    public String getHostName() {
        return hostName;
    }

    public InetAddress getInetAddress() {
        return serverKey.getInetAddress();
    }

    public int getPort() {
        return serverKey.getPort();
    }

    public long getLastAnnounceTimestamp() {
        return lastAnnounceTimestamp;
    }

    public void setLastAnnounceTimestamp(long lastAnnounceTimestamp) {
        long oldValue = this.lastAnnounceTimestamp;
        this.lastAnnounceTimestamp = lastAnnounceTimestamp;
        firePropertyChange("lastAnnounceTimestamp", oldValue, this.lastAnnounceTimestamp);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        String oldValue = this.description;
        this.description = description;
        firePropertyChange("description", oldValue, this.description);
    }

    public boolean isConnectionFailed() {
        return connectionFailed;
    }

    public void setConnectionFailed(boolean connectionFailed) {
        boolean oldValue = this.connectionFailed;
        this.connectionFailed = connectionFailed;
        firePropertyChange("connectionFailed", oldValue, this.connectionFailed);
    }

    private String getHostAndPort() {
        return getHostName() + ":" + getPort();
    }

    public String getServerContextIdentifier() {
        return getHostAndPort().replaceAll(IdentifiablePathUtils.NAMESPACE_SEPARATOR_REGEX_TOKEN, "_");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeSeriesServer that = (TimeSeriesServer) o;

        if (serverKey != null ? !serverKey.equals(that.serverKey) : that.serverKey != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return serverKey != null ? serverKey.hashCode() : 0;
    }

    public int compareTo(Object o) {
        TimeSeriesServer r = (TimeSeriesServer)o;
        if ( this.equals(o)) {
            return 0;
        }  else {
            return r.getDescription().compareTo(getDescription());
        }
    }

    @Override
    public String toString() {
        return "TimeSeriesServer{" +
                "serverKey=" + serverKey +
                ", description='" + description + '\'' +
                ", lastAnnounceTimestamp=" + lastAnnounceTimestamp +
                '}';
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    private void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    public static TimeSeriesServer create(AnnouncementMessage p) throws UnknownHostException {
        int port = p.getPort();
        String description = p.getDescription();
        return new TimeSeriesServer(p.getHostname(), port, description, System.currentTimeMillis());
    }

    public ServerKey getServerKey() {
        return serverKey;
    }

    public static class ServerKey {

        private final InetAddress inetAddress;
        private final int port;

        public ServerKey(InetAddress inetAddress, int port) {
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

            ServerKey serverKey = (ServerKey) o;

            if (port != serverKey.port) return false;
            if (inetAddress != null ? !inetAddress.equals(serverKey.inetAddress) : serverKey.inetAddress != null)
                return false;

            return true;
        }

        public int hashCode() {
            int result = inetAddress != null ? inetAddress.hashCode() : 0;
            result = 31 * result + port;
            return result;
        }

        @Override
        public String toString() {
            return "ServerKey{" +
                    "inetAddress=" + inetAddress +
                    ", port=" + port +
                    '}';
        }
    }
}
