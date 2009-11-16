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

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.net.udp.TimeSeriesValueMessage;
import com.od.jtimeseries.net.udp.UdpMessage;
import com.od.jtimeseries.net.udp.UdpServer;
import com.od.jtimeseries.timeseries.TimeSeries;
import com.od.jtimeseries.util.logging.LogDefaults;
import com.od.jtimeseries.util.logging.LogMethods;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
* User: nick
* Date: 20-May-2009
* Time: 22:11:48
* To change this template use File | Settings | File Templates.
*/
class TimeSeriesCreatingMessageListener implements UdpServer.UdpMessageListener {

    private Set<String> contextPath = new HashSet<String>();

    private LogMethods logMethod = LogDefaults.getDefaultLogMethods(TimeSeriesCreatingMessageListener.class);
    private TimeSeriesContext rootContext;

    public TimeSeriesCreatingMessageListener(TimeSeriesContext rootContext) {
        this.rootContext = rootContext;
    }

    public void udpMessageReceived(UdpMessage m) {
        if ( m instanceof TimeSeriesValueMessage) {
            TimeSeriesValueMessage v = (TimeSeriesValueMessage)m;
            processNewTimeSeriesValue(v);
        }
    }

    private void processNewTimeSeriesValue(TimeSeriesValueMessage v) {
        try {
            TimeSeries s = rootContext.getOrCreateTimeSeriesForPath(v.getContextPath(), v.getDescription());
            s.append(v.getTimeSeriesItem());

            if ( contextPath.add(v.getContextPath())) {
                logMethod.logInfo("Started to receive UDP updates for series " + v.getContextPath() + " from host " + v.getHostname() + " with address " + v.getInetAddress());
            }
        } catch ( Exception e) {
            logMethod.logError("Error when trying to create timeseries for UDP series "  + v.getContextPath() + " from host " + v.getInetAddress() + " with address " + v.getInetAddress());
            logMethod.logDebug("Error when trying to create timeseries", e);
        }
    }
}
