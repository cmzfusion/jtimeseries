/**
 * Copyright (C) 2011 (nick @ objectdefinitions.com)
 *
 * This file is part of JTimeseries.
 *
 * JTimeseries is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JTimeseries is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JTimeseries.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.od.jtimeseries.component.managedmetric.jmx.value;

import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunction;

import javax.management.AttributeNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import java.io.IOException;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
* User: nick
* Date: 06-Dec-2009
* Time: 12:30:35
*
* Read simple data type attributes on JMX bean(s)
*/
public class SimpleJmxValue implements JmxValue {

    private final String objectName;
    private final String attribute;

    /**
     * @param objectName, String representation of a JMX ObjectName, identifying one or more mbeans from which we want to read an attribute value
     * @param attribute, of mbeans to read
     */
    public SimpleJmxValue(String objectName, String attribute) {
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
        Set<ObjectName> matchingBeans = jmxConnection.queryNames(getJmxObjectName(), null);

        for ( ObjectName beanName : matchingBeans ) {
            readValueFromBean(jmxConnection, f, beanName);
        }
    }

    protected void readValueFromBean(MBeanServerConnection jmxConnection, AggregateFunction f, ObjectName beanName) throws Exception {
        checkBeanExists(jmxConnection, beanName);
        Object attribute = safelyGetAttribute(jmxConnection, beanName);
        Double v = convertToDouble(attribute);
        f.addValue(v);
    }

    protected ObjectName getJmxObjectName() throws JmxValueException {
        ObjectName result = null;
        try {
            result = new ObjectName(getObjectName());
        } catch (MalformedObjectNameException e) {
            raiseValueException("Malformed Object Name " + getObjectName());
        }
        return result;
    }

    protected Double convertToDouble(Object value) throws JmxValueException {
        Double v = Double.NaN;
        try {
            v = Double.valueOf(value.toString());
        } catch ( NumberFormatException nfe) {
            raiseValueException("Could not convert value " + value + " to a Double");
        }
        return v;
    }

    protected Object safelyGetAttribute(MBeanServerConnection jmxConnection, ObjectName beanName) throws JmxValueException {
        Object attribute = null;
        try {
            attribute = jmxConnection.getAttribute(beanName, getAttribute());
        } catch (Throwable t) {
            if ( t instanceof AttributeNotFoundException) {
                raiseValueException("The attribute was not found");
            } else {
                raiseValueException("Exception calling getAttribute", t);
            }
        }

        if ( attribute == null ) {
           raiseValueException("The attribute value returned was null");
        }
        return attribute;
    }

    protected void checkBeanExists(MBeanServerConnection jmxConnection, ObjectName beanName) throws IOException, JmxValueException {
        boolean registered = jmxConnection.isRegistered(beanName);
        if ( ! registered ) {
            raiseValueException("There is no registered MBean named " + getObjectName());
        }
    }

    protected void raiseValueException(String message, Throwable t) throws JmxValueException {
        throw new JmxValueException("Failed to read the attribute " + getAttribute() + " from bean " + getObjectName() + ". " + message, t);
    }

    protected void raiseValueException(String message) throws JmxValueException {
        throw new JmxValueException("Failed to read the attribute " + getAttribute() + " from bean " + getObjectName() + ". " + message);
    }

    protected Object getValueFromCompositeData(CompositeData compositeDataSupport, String key) throws JmxValueException {
        Object result = null;
        try {
            result = compositeDataSupport.get(key);
        } catch (Exception e) {
            raiseValueException("Failed while trying to read CompositeDataSupport value for key " + key, e);
        }
        return result;
    }
}
