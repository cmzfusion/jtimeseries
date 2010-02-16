package com.od.jtimeseries.server.servermetrics.jmx.measurement;

import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunction;
import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunctions;
import com.od.jtimeseries.server.servermetrics.jmx.JmxValue;
import com.od.jtimeseries.server.servermetrics.jmx.CompositeDataJmxValue;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 02-Feb-2010
 * Time: 22:01:21
 *
 * Create an instance via the JmxMetrics factory method
 */
class JmxMemoryUsage extends JmxMeasurement {

    private JmxMemoryUsage(String parentContextPath, String id, String description, List<JmxValue> listOfJmxValue, AggregateFunction aggregateFunction) {
        super(parentContextPath, id, description, listOfJmxValue, aggregateFunction);
    }

    static JmxMemoryUsage createJmxMemoryUsage(String parentContextPath, String id, String description) {
        //memory usage is the sum of the heap and non-heap memory
        List<JmxValue> jmxValue = new LinkedList<JmxValue>();
        jmxValue.add(new CompositeDataJmxValue("java.lang:type=Memory", "HeapMemoryUsage", "used"));
        jmxValue.add(new CompositeDataJmxValue("java.lang:type=Memory", "NonHeapMemoryUsage", "used"));

        JmxMemoryUsage m = new JmxMemoryUsage(parentContextPath, id, description, jmxValue, AggregateFunctions.SUM());
        m.setDivisor(1000000);
        return m;
    }
}
