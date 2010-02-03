package com.od.jtimeseries.server.servermetrics.jmx;

import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunction;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 03-Feb-2010
 * Time: 17:26:38
 *
 * A value from a CompositeData typed attribute of a named JMX bean, where the
 * compositeDataKey specifies the attribute we require
 */
public class CompositeDataJmxValue extends JmxValue {

    private String compositeDataKey;

    public CompositeDataJmxValue(String objectName, String attribute, String compositeDataKey) {
        super(objectName, attribute);
        this.compositeDataKey = compositeDataKey;
    }

    public void readValues(MBeanServerConnection jmxConnection, AggregateFunction f) throws Exception {
        Object value = ((CompositeDataSupport)jmxConnection.getAttribute(
                new ObjectName(getObjectName()), getAttribute())).get(compositeDataKey);
        f.addValue(Double.valueOf(value.toString()));
    }


}
