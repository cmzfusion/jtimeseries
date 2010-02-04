package com.od.jtimeseries.server.servermetrics.jmx;

import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunction;
import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunctions;
import com.od.jtimeseries.util.time.TimePeriod;

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
class JmxMemoryUsage extends JmxMetric {

    private JmxMemoryUsage(TimePeriod timePeriod, String parentContextPath, String id, String description, String serviceUrl, List<JmxValue> listOfJmxValue, AggregateFunction aggregateFunction) {
        super(timePeriod, parentContextPath, id, description, serviceUrl, listOfJmxValue, aggregateFunction);
    }

    static JmxMemoryUsage createJmxMemoryUsage(TimePeriod timePeriod, String parentContextPath, String id, String description, String serviceUrl) {
        //memory usage is the sum of the heap and non-heap memory
        List<JmxValue> jmxValue = new LinkedList<JmxValue>();
        jmxValue.add(new CompositeDataJmxValue("java.lang:type=Memory", "HeapMemoryUsage", "used"));
        jmxValue.add(new CompositeDataJmxValue("java.lang:type=Memory", "NonHeapMemoryUsage", "used"));

        JmxMemoryUsage m = new JmxMemoryUsage(timePeriod, parentContextPath, id, description, serviceUrl, jmxValue, AggregateFunctions.SUM());
        m.setDivisor(1000000);
        return m;
    }
}
