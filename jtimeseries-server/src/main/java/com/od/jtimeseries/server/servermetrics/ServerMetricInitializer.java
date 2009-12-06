package com.od.jtimeseries.server.servermetrics;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

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

    public ServerMetricInitializer(TimeSeriesContext rootContext, List<ServerMetricSource> serverMetricSourceList) {
        this.rootContext = rootContext;
        this.serverMetricSourceList = serverMetricSourceList;
    }

    public void initializeServerMetrics() {
        logMethods.logInfo("Initializing Server Metrics");

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
            TimeSeriesContext c = rootContext.createContextForPath(m.getParentContextPath());
            m.setupSeries(c);
        } catch (Throwable t) {
            logMethods.logError("Failed to set up server metric " + m.getClass() + " " + m, t);
        }
    }


}
