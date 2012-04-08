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
package com.od.jtimeseries.demo;

import com.od.jtimeseries.JTimeSeries;
import com.od.jtimeseries.capture.function.CaptureFunctions;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.identifiable.Identifiable;
import com.od.jtimeseries.net.udp.UdpClient;
import com.od.jtimeseries.net.udp.UdpClientConfig;
import com.od.jtimeseries.net.udp.UdpPublishingTreeListener;
import com.od.jtimeseries.source.ValueRecorder;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.impl.DefaultIdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.impl.DefaultTimeSeriesFactory;
import com.od.jtimeseries.timeseries.impl.RoundRobinTimeSeries;
import com.od.jtimeseries.util.NamedExecutors;
import com.od.jtimeseries.util.time.Time;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 17-May-2009
 * Time: 14:33:35
 * To change this template use File | Settings | File Templates.
 *
 * send some random values to a local time series server
 */
public class TimeSeriesUdpClient {

    public static void main(String[] args) throws UnknownHostException {
        UdpClient udpClient = new UdpClient(new UdpClientConfig(
                InetAddress.getByName("localhost"),
                18081
        ));

        int timeSeriesCount = 1000;

        TimeSeriesContext rootContext = JTimeSeries.createRootContext();
//        rootContext.setTimeSeriesFactory(new UdpRemoteTimeSeriesFactory(udpClient));

        rootContext.addTreeListener(new UdpPublishingTreeListener(udpClient, timeSeriesCount, 20000));

        rootContext.setTimeSeriesFactory(new DefaultTimeSeriesFactory() {
            public IdentifiableTimeSeries createTimeSeries(Identifiable parent, String path, String id, String description, Class classType, Object... parameters) {
                return new DefaultIdentifiableTimeSeries(id, description, new RoundRobinTimeSeries(10));
            }
        });

        final List<ValueRecorder> l = new ArrayList<ValueRecorder>();
        for ( int loop=0; loop < timeSeriesCount; loop++) {
            l.add(rootContext.createValueRecorderSeries(TimeSeriesUdpClient.class.getName() + loop, TimeSeriesUdpClient.class.getName()));
        }

        ScheduledExecutorService s = NamedExecutors.newSingleThreadScheduledExecutor("TimeSeriesUdpClient");
        s.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                long value = System.currentTimeMillis();
                for (ValueRecorder r : l) {
                    r.newValue(value);
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }
}
