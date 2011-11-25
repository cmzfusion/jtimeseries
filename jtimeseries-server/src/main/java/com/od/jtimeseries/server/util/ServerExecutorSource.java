package com.od.jtimeseries.server.util;

import com.od.jtimeseries.util.NamedExecutors;
import com.od.jtimeseries.util.TimeSeriesExecutorFactory;

import java.util.concurrent.ExecutorService;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 07/11/11
 * Time: 08:27
 */
public class ServerExecutorSource extends TimeSeriesExecutorFactory.DefaultExecutorSource {

    //a httpd daemon with more threads
    private ExecutorService httpExecutor;
    private ExecutorService jmxExecutor;

    public ServerExecutorSource(int maxThreads, int maxJmxThreads) {
        httpExecutor = NamedExecutors.newFixedThreadPool("HttpRequestProcessor", maxThreads, NamedExecutors.DAEMON_THREAD_CONFIGURER);
        jmxExecutor = NamedExecutors.newFixedThreadPool("JmxMetricProcessor", maxJmxThreads, NamedExecutors.DAEMON_THREAD_CONFIGURER);
    }

    public ExecutorService getHttpdQueryExecutor(Object httpdInstance) {
        return httpExecutor;
    }
}
