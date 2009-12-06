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

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.JTimeSeries;
import com.od.jtimeseries.source.ValueRecorder;
import com.od.jtimeseries.net.udp.*;
import com.od.jtimeseries.capture.function.CaptureFunctions;
import com.od.jtimeseries.util.time.Time;

import java.net.InetAddress;
import java.net.UnknownHostException;

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
        UdpClient udpClient = new UdpClient(new UdpClient.ClientConfig(
                InetAddress.getByName("localhost"),
                18081
        ));

        TimeSeriesContext rootContext = JTimeSeries.createRootContext();
        rootContext.setTimeSeriesFactory(new UdpRemoteTimeSeriesFactory(udpClient));

        TimeSeriesContext fooContext = rootContext.createChildContext("Udp Context", "A context for foo");
        TimeSeriesContext testContext = rootContext.createContextForPath("Udp Context.test");

        ValueRecorder v = fooContext.createValueRecorder("Foo Benchmark", "Foo Benchmark Description", CaptureFunctions.MAX(Time.seconds(1)));
        ValueRecorder f = testContext.createValueRecorder("Wibble Benchmark", "Wibble Benchmark Description", CaptureFunctions.MEAN(Time.seconds(2)));
        rootContext.startScheduling().startDataCapture();

        while(true) {
            v.newValue(Math.random() * 100);
            f.newValue(Math.random() * 50);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
