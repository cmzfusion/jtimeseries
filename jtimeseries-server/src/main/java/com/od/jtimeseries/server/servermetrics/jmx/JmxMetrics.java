package com.od.jtimeseries.server.servermetrics.jmx;

import com.od.jtimeseries.util.time.TimePeriod;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 02-Feb-2010
 * Time: 22:06:20
 * To change this template use File | Settings | File Templates.
 */
public class JmxMetrics {

    public static JmxMetric memoryUsage(TimePeriod timePeriod, String parentContextPath, String id, String description, String serviceUrl) {
        return JmxMemoryUsage.createJmxMemoryUsage(timePeriod, parentContextPath, id, description, serviceUrl);
    }

    public static JmxMetric garbageCollectionPercentage(TimePeriod timePeriod, String parentContextPath, String id, String description, String serviceUrl) {
        return JmxGarbageCollectionPercentage.createJmxGarbageCollectionPercentage(timePeriod, parentContextPath, id, description, serviceUrl);
    }

    public static JmxMetric cpuTimePercentage(TimePeriod timePeriod, String parentContextPath, String id, String description, String serviceUrl) {
        return JmxCpuTimePercentage.createJmxCpuTimePercentage(timePeriod, parentContextPath, id, description, serviceUrl);
    }

    public static JmxMetric memoryUsagePercentage(TimePeriod timePeriod, String parentContextPath, String id, String description, String serviceUrl) {
        return JmxMemoryUsagePercentage.createMemoryUsagePercentage(timePeriod, parentContextPath, id, description, serviceUrl);
    }

}
