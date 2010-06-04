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
package com.od.jtimeseries.component.managedmetric.jmx.measurement;

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
