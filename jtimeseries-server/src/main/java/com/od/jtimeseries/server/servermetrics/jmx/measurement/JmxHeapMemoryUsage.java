package com.od.jtimeseries.server.servermetrics.jmx.measurement;

import com.od.jtimeseries.server.servermetrics.jmx.JmxValue;
import com.od.jtimeseries.server.servermetrics.jmx.CompositeDataJmxValue;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 02-Feb-2010
 * Time: 22:01:21
 *
 * Create an instance via the JmxMetrics factory method
 */
class JmxHeapMemoryUsage extends JmxMeasurement {

    private JmxHeapMemoryUsage(String parentContextPath, String id, String description, JmxValue value) {
        super(parentContextPath, id, description, value);
    }

    static JmxHeapMemoryUsage createJmxMemoryUsage(String parentContextPath, String id, String description) {
        //memory usage is the sum of the heap and non-heap memory
        JmxValue value = new CompositeDataJmxValue("java.lang:type=Memory", "HeapMemoryUsage", "used");
        JmxHeapMemoryUsage m = new JmxHeapMemoryUsage(parentContextPath, id, description, value);
        m.setDivisor(1000000);
        return m;
    }
}
