package com.od.jtimeseries.server.servermetrics.jmx.measurement;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 02-Feb-2010
 * Time: 22:06:20
 * To change this template use File | Settings | File Templates.
 */
public class JmxMeasurements {

    public static JmxMeasurement memoryUsage(String parentContextPath, String id, String description) {
        return JmxMemoryUsage.createJmxMemoryUsage(parentContextPath, id, description);
    }

    public static JmxMeasurement garbageCollectionPercentage(String parentContextPath, String id, String description) {
        return JmxGarbageCollectionPercentage.createJmxGarbageCollectionPercentage(parentContextPath, id, description);
    }

    public static JmxMeasurement cpuTimePercentage(String parentContextPath, String id, String description) {
        return JmxCpuTimePercentage.createJmxCpuTimePercentage(parentContextPath, id, description);
    }

    public static JmxMeasurement memoryUsagePercentage(String parentContextPath, String id, String description) {
        return JmxMemoryUsagePercentage.createMemoryUsagePercentage(parentContextPath, id, description);
    }

}
