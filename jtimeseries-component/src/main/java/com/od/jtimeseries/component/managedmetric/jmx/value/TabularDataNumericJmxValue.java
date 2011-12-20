package com.od.jtimeseries.component.managedmetric.jmx.value;

import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunction;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.TabularDataSupport;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ga2adaz
 * Date: 19/12/11
 * Time: 15:25
 * To change this template use File | Settings | File Templates.
 */
public class TabularDataNumericJmxValue extends JmxValue {

    private String tabularDataKey;

    /**
     * @param objectName, String representation of a JMX ObjectName, identifying one or more mbeans from which we want to read an attribute value
     * @param attribute,  of mbeans to read
     * @param tabularDataKey, key within tabular data structure idenfitying the value to read
     */
    public TabularDataNumericJmxValue(String objectName, String attribute, String tabularDataKey) {
        super(objectName, attribute);
        this.tabularDataKey = tabularDataKey;
    }

    /**
     * Reads in the numeric value of mapped to the tabularDataKey value specified when this object was created
     * @param jmxConnection
     * @param f
     * @param beanName
     * @throws Exception
     */
    protected void readValueFromBean(MBeanServerConnection jmxConnection, AggregateFunction f, ObjectName beanName) throws Exception {
        TabularDataSupport tabularDataSupport = ((TabularDataSupport)jmxConnection.getAttribute(new ObjectName(getObjectName()), getAttribute()));
        Object[] key = {tabularDataKey};
        CompositeData compositeData = tabularDataSupport.get(key);
        String value = (String) compositeData.get("value");
        f.addValue(Double.valueOf(value));
    }



    
}
