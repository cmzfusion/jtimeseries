package com.od.jtimeseries.component.managedmetric.jmx.value;

import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunction;

import javax.management.MBeanServerConnection;

/**
 * Created by IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 13/01/12
 * Time: 16:59
 *
 * Provides logic to read one or more values from jmx beans
 */
public interface JmxValue {

    String getObjectName();

    String getAttribute();

    void readValues(MBeanServerConnection jmxConnection, AggregateFunction f) throws Exception;
}
