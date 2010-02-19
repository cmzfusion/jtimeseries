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

import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesFactory;
import com.od.jtimeseries.util.identifiable.IdentifiableBase;
import com.od.jtimeseries.util.identifiable.Identifiable;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 17-May-2009
 * Time: 14:08:47
 * To change this template use File | Settings | File Templates.
 *
 * A factory to create UdpRemoteTimeSeries -
 * These time series are just a proxy for series data stored on a remote time series server.
 * This is appropriate in the case where we want to maintain no local timeseries history.
 */
public class UdpRemoteTimeSeriesFactory extends IdentifiableBase implements TimeSeriesFactory {

    private UdpClient udpClient;

    public UdpRemoteTimeSeriesFactory(UdpClient udpClient) {
        this("UdpRemoteTimeSeriesFactory", "UdpRemoteTimeSeriesFactory", udpClient);
    }
    
    public UdpRemoteTimeSeriesFactory(String id, String description, UdpClient udpClient) {
        super(id, description);
        this.udpClient = udpClient;
    }

    public IdentifiableTimeSeries createTimeSeries(Identifiable parent, String path, String id, String description) {
        return new UdpRemoteTimeSeries(id, description, udpClient);
    }
}
