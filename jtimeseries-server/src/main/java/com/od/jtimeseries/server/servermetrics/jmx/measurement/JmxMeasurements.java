package com.od.jtimeseries.server.servermetrics.jmx.measurement;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 02-Feb-2010
 * Time: 22:06:20
 * To change this template use File | Settings | File Templates.
 */
public class JmxMeasurements {

    public static JmxMeasurement heapMemoryUsage(String parentContextPath) {
        return JmxHeapMemoryUsage.createJmxMemoryUsage(parentContextPath, "Heap Memory Usage", "Heap Memory Usage in MB");
    }

    public static JmxMeasurement heapMemoryUsage(String parentContextPath, String id, String description) {
        return JmxHeapMemoryUsage.createJmxMemoryUsage(parentContextPath, id, description);
    }

    public static JmxMeasurement gcTimePercentage(String parentContextPath) {
        return JmxGarbageCollectionPercentage.createJmxGarbageCollectionPercentage(parentContextPath, "GC Time Percentage", "Percentage of time spent in Garbage Collection");
    }

    public static JmxMeasurement gcTimePercentage(String parentContextPath, String id, String description) {
        return JmxGarbageCollectionPercentage.createJmxGarbageCollectionPercentage(parentContextPath, id, description);
    }

    public static JmxMeasurement cpuTimePercentage(String parentContextPath) {
        return JmxCpuTimePercentage.createJmxCpuTimePercentage(parentContextPath, "CPU Usage Percentage", "CPU usage percentage, may exceed 100 if more than one processor");
    }

    public static JmxMeasurement cpuTimePercentage(String parentContextPath, String id, String description) {
        return JmxCpuTimePercentage.createJmxCpuTimePercentage(parentContextPath, id, description);
    }

     public static JmxMeasurement heapMemoryPercentageOfMax(String parentContextPath) {
        return JmxHeapMemoryPercentageOfMax.createMemoryUsagePercentage(parentContextPath, "Heap Memory Percentage Of Max", "Percentage of Max Heap Memory currently used");
    }

    public static JmxMeasurement heapMemoryPercentageOfMax(String parentContextPath, String id, String description) {
        return JmxHeapMemoryPercentageOfMax.createMemoryUsagePercentage(parentContextPath, id, description);
    }

}
