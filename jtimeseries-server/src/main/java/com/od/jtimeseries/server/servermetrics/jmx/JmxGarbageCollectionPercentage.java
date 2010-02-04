package com.od.jtimeseries.server.servermetrics.jmx;

import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunction;
import com.od.jtimeseries.util.time.TimePeriod;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 03-Feb-2010
 * Time: 17:55:59
 */
class JmxGarbageCollectionPercentage extends JmxMetric {

    private JmxGarbageCollectionPercentage(TimePeriod timePeriod, String parentContextPath, String id, String description, String serviceUrl, List<JmxValue> listOfJmxValue, AggregateFunction aggregateFunction) {
        super(timePeriod, parentContextPath, id, description, serviceUrl, listOfJmxValue, aggregateFunction);
    }

    static JmxGarbageCollectionPercentage createJmxGarbageCollectionPercentage(TimePeriod timePeriod, String parentContextPath, String id, String description, String serviceUrl) {
        //memory usage is the sum of the heap and non-heap memory
        List<JmxValue> jmxValue = new LinkedList<JmxValue>();
        
        //The collection count attribute on all GarbageCollection type mbeans
        jmxValue.add(new JmxValue("java.lang:type=GarbageCollector,*", "CollectionTime"));

        return new JmxGarbageCollectionPercentage(timePeriod, parentContextPath, id, description, serviceUrl, jmxValue, new JmxPercentageOfTimeFunction(timePeriod.getLengthInMillis()));
    }

}
