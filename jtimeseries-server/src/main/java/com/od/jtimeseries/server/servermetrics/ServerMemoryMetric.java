package com.od.jtimeseries.server.servermetrics;

import com.od.jtimeseries.util.time.TimePeriod;
import com.od.jtimeseries.util.time.Time;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunction;
import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunctions;
import com.od.jtimeseries.source.ValueRecorder;
import com.od.jtimeseries.source.impl.DefaultValueRecorder;
import com.od.jtimeseries.capture.impl.DefaultCapture;

import javax.management.remote.JMXServiceURL;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.*;
import javax.management.openmbean.CompositeDataSupport;
//import javax.management.ObjectName;
//import javax.management.JMX;
import java.lang.management.MemoryMXBean;
import java.io.IOException;
import java.util.List;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 25-Nov-2009
 * Time: 22:11:24
 * To change this template use File | Settings | File Templates.
 *
 * Use JMX to monitor server's own memory usage
 */
public class ServerMemoryMetric extends JmxMetric {

    private ServerMemoryMetric(TimePeriod timePeriod, String id, String description, String serviceUrl, List<NameAttributeAndKey> listOfNameAttributeAndKey, AggregateFunction aggregateFunction) {
        super(timePeriod, id, description, serviceUrl, listOfNameAttributeAndKey, aggregateFunction);
    }

    public static ServerMemoryMetric createServerMemoryMetric(int jmxManagementPort) {
        JmxMetric.NameAttributeAndKey heapUsed = new JmxMetric.NameAttributeAndKey("java.lang:type=Memory", "HeapMemoryUsage","used");
        JmxMetric.NameAttributeAndKey nonHeapUsed = new JmxMetric.NameAttributeAndKey("java.lang:type=Memory", "NonHeapMemoryUsage","used");

        return new ServerMemoryMetric(
            Time.seconds(15),
            "ServerMemory",
            "Heap and NonHeap Memory usage by server in MB",
            "service:jmx:rmi:///jndi/rmi://localhost:" + jmxManagementPort + "/jmxrmi",
            Arrays.asList(heapUsed, nonHeapUsed),
            AggregateFunctions.SUM()
        );
    }
}
