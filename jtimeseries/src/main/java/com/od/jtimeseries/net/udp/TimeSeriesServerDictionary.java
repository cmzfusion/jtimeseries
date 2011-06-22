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

import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 13-Jan-2009
 * Time: 12:40:21
 */
public interface TimeSeriesServerDictionary {

    List<TimeSeriesServer> getKnownTimeSeriesServer();

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
    TimeSeriesServer getOrCreateServer(String host, int port, String description) throws UnknownHostException;

    boolean serverExists(String host, int port) throws UnknownHostException;

    boolean removeServer(TimeSeriesServer server);
}
