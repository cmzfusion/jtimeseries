package com.od.jtimeseries.server.servermetrics.jmx;

import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunction;
import com.od.jtimeseries.util.time.TimePeriod;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 04-Feb-2010
 * Time: 10:00:24
 */
class JmxCpuTimePercentage extends JmxMetric {

    private JmxCpuTimePercentage(TimePeriod timePeriod, String parentContextPath, String id, String description, String serviceUrl, List<JmxValue> listOfJmxValue, AggregateFunction aggregateFunction) {
        super(timePeriod, parentContextPath, id, description, serviceUrl, listOfJmxValue, aggregateFunction);
    }

    static JmxCpuTimePercentage createJmxCpuTimePercentage(TimePeriod timePeriod, String parentContextPath, String id, String description, String serviceUrl) {
        //memory usage is the sum of the heap and non-heap memory
        List<JmxValue> jmxValue = new LinkedList<JmxValue>();

        //The collection count attribute on all GarbageCollection type mbeans
        jmxValue.add(new JmxValue("java.lang:type=OperatingSystem", "ProcessCpuTime"));

        JmxCpuTimePercentage jmxCpuTimePercentage = new JmxCpuTimePercentage(timePeriod, parentContextPath, id, description, serviceUrl, jmxValue, new JmxPercentageOfTimeFunction(timePeriod.getLengthInMillis()));
        jmxCpuTimePercentage.setDivisor(1000000); //cpu times appear to be nanoseconds, we need milliseconds
        return jmxCpuTimePercentage;
    }

}
