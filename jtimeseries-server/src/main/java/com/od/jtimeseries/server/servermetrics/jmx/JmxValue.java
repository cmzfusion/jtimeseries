package com.od.jtimeseries.server.servermetrics.jmx;

import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunction;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
* User: nick
* Date: 06-Dec-2009
* Time: 12:30:35
*
* A simple data type attribute on a named JMX bean
*/
public class JmxValue {

    private final String objectName;
    private final String attribute;

    public JmxValue(String objectName, String attribute) {
        this.objectName = objectName;
        this.attribute = attribute;
    }

    public String getObjectName() {
        return objectName;
    }

    public String getAttribute() {
        return attribute;
    }

    /**
     * Read value(s) from the mbeans via the jmx connection into the aggregate function supplied
     */
    public void readValues(MBeanServerConnection jmxConnection, AggregateFunction f) throws Exception {

        //one or more MBeans may match the object name specified, we add the values from each matching bean
        Set<ObjectName> matchingBeans = jmxConnection.queryNames(new ObjectName(objectName), null);

        for ( ObjectName beanName : matchingBeans ) {
            readValueFromBean(jmxConnection, f, beanName);
        }
    }

    protected void readValueFromBean(MBeanServerConnection jmxConnection, AggregateFunction f, ObjectName beanName) throws Exception {
        Object value = jmxConnection.getAttribute(beanName, attribute);
        f.addValue(Double.valueOf(value.toString()));
    }
}
