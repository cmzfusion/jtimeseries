package com.od.jtimeseries.server.servermetrics.jmx;

import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunction;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 03-Feb-2010
 * Time: 17:26:38
 *
 * A value from a CompositeData typed attribute of JMX beans(s), where the
 * compositeDataKey specifies the attribute we require
 */
public class CompositeDataJmxValue extends JmxValue {

    private String compositeDataKey;

     /**
     * @param objectName, String representation of a JMX ObjectName, identifying one or more mbeans from which we want to read an attribute value
     * @param attribute, of mbeans to read which should be of type CompositeData
     * @param compositeDataKey, key within composite data structure idenfitying the value to read
     */
    public CompositeDataJmxValue(String objectName, String attribute, String compositeDataKey) {
        super(objectName, attribute);
        this.compositeDataKey = compositeDataKey;
    }

    public void readValueFromBean(MBeanServerConnection jmxConnection, AggregateFunction f, ObjectName beanName) throws Exception {
        Object value = ((CompositeDataSupport)jmxConnection.getAttribute(
                new ObjectName(getObjectName()), getAttribute())).get(compositeDataKey);
        f.addValue(Double.valueOf(value.toString()));
    }


}
