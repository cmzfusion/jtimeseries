/**
 * Copyright (C) 2009 (nick @ objectdefinitions.com)
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

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
* User: nick
* Date: 06-Dec-2009
* Time: 12:30:35
*
* Read simple data type attribute on JMX bean(s)
*/
public class JmxValue {

    private final String objectName;
    private final String attribute;

    /**
     * @param objectName, String representation of a JMX ObjectName, identifying one or more mbeans from which we want to read an attribute value
     * @param attribute, of mbeans to read
     */
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
