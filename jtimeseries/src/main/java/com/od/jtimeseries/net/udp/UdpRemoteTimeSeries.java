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

import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.timeseries.impl.DefaultIdentifiableTimeSeries;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 17-May-2009
 * Time: 13:49:51
 * To change this template use File | Settings | File Templates.
 *
 * A time series which acts as a local proxy for a remote time series - sends UDP messages when new values
 * are appended to the series.
 *
 * Only appending values is supported, and there is no local history kept (appending will have no effect on the size)
 */
public class UdpRemoteTimeSeries extends DefaultIdentifiableTimeSeries {

    private long lastTimestamp;
    private UdpClient udpClient;

    public UdpRemoteTimeSeries(String id, String description, UdpClient udpClient) {
        super(id, description);
        this.udpClient = udpClient;
    }

    public synchronized boolean append(TimeSeriesItem value) {
        return doAppend(value);
    }

    public synchronized boolean add(TimeSeriesItem value) {
        return doAppend(value);
    }

    private boolean doAppend(TimeSeriesItem value) {
        boolean result = false;
        if ( value.getTimestamp() >= lastTimestamp ) {
            lastTimestamp = value.getTimestamp();
            result = true;
            udpClient.sendMessage(
                    new TimeSeriesValueMessage(getPath(), getDescription(), value)
            );
        }
        return result;
    }

    public boolean prepend(TimeSeriesItem value) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(Collection<? extends TimeSeriesItem> c) {
        throw new UnsupportedOperationException();
    }
}
