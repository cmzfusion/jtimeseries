package com.od.jtimeseries.server.servermetrics.jmx.measurement;

import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunction;
import com.od.jtimeseries.server.servermetrics.jmx.JmxValue;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 03-Feb-2010
 * Time: 17:55:59
 */
class JmxGarbageCollectionPercentage extends JmxMeasurement {

    private JmxGarbageCollectionPercentage(String parentContextPath, String id, String description, List<JmxValue> listOfJmxValue, AggregateFunction aggregateFunction) {
        super(parentContextPath, id, description, listOfJmxValue, aggregateFunction);
    }

    static JmxGarbageCollectionPercentage createJmxGarbageCollectionPercentage(String parentContextPath, String id, String description) {
        //memory usage is the sum of the heap and non-heap memory
        List<JmxValue> jmxValue = new LinkedList<JmxValue>();
        
        //The collection count attribute on all GarbageCollection type mbeans
        jmxValue.add(new JmxValue("java.lang:type=GarbageCollector,*", "CollectionTime"));

        return new JmxGarbageCollectionPercentage(parentContextPath, id, description, jmxValue, new JmxPercentageOfTimeFunction());
    }

}
