package com.od.jtimeseries.server.servermetrics.jmx.measurement;

import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunction;
import com.od.jtimeseries.server.servermetrics.jmx.JmxValue;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 04-Feb-2010
 * Time: 10:00:24
 */
class JmxCpuTimePercentage extends JmxMeasurement {

    private JmxCpuTimePercentage(String parentContextPath, String id, String description, List<JmxValue> listOfJmxValue, AggregateFunction aggregateFunction) {
        super(parentContextPath, id, description, listOfJmxValue, aggregateFunction);
    }

    static JmxCpuTimePercentage createJmxCpuTimePercentage(String parentContextPath, String id, String description) {
        //memory usage is the sum of the heap and non-heap memory
        List<JmxValue> jmxValue = new LinkedList<JmxValue>();

        //The collection count attribute on all GarbageCollection type mbeans
        jmxValue.add(new JmxValue("java.lang:type=OperatingSystem", "ProcessCpuTime"));

        JmxCpuTimePercentage jmxCpuTimePercentage = new JmxCpuTimePercentage(parentContextPath, id, description, jmxValue, new JmxPercentageOfTimeFunction());
        jmxCpuTimePercentage.setDivisor(1000000); //cpu times appear to be nanoseconds, we need milliseconds
        return jmxCpuTimePercentage;
    }

}
