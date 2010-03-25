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
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.server.servermetrics.jmx.JmxExecutorService;
import com.od.jtimeseries.server.servermetrics.jmx.JmxMetric;
import com.od.jtimeseries.server.servermetrics.ServerMetricSource;
import com.od.jtimeseries.server.servermetrics.ServerMetric;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 22-Nov-2009
 * Time: 00:38:44
 * To change this template use File | Settings | File Templates.
 */
public class ServerMetricInitializer {

    private static LogMethods logMethods = LogUtils.getLogMethods(ServerMetricInitializer.class);

    private TimeSeriesContext rootContext;
    private List<ServerMetricSource> serverMetricSourceList;
    private JmxExecutorService jmxExecutorService;

    public ServerMetricInitializer(TimeSeriesContext rootContext, List<ServerMetricSource> serverMetricSourceList, JmxExecutorService jmxExecutorService) {
        this.rootContext = rootContext;
        this.serverMetricSourceList = serverMetricSourceList;
        this.jmxExecutorService = jmxExecutorService;
    }

    public void initializeServerMetrics() {
        logMethods.logInfo("Initializing Server Metrics");

        logMethods.logInfo("Creating JMX Executor Service " + jmxExecutorService);
        JmxMetric.setJmxExecutorService(jmxExecutorService);

        for ( ServerMetricSource s : serverMetricSourceList ) {
            for ( ServerMetric m : s.getServerMetrics()) {
                logMethods.logInfo("Setting up metric " + m);
                setupMetric(m);
            }
        }
        logMethods.logInfo("Finished initializing Server Metrics");
    }

    private void setupMetric(ServerMetric m) {
        try {
            m.initializeMetrics(rootContext);
        } catch (Throwable t) {
            logMethods.logError("Failed to set up server metric " + m.getClass() + " " + m, t);
        }
    }


}
