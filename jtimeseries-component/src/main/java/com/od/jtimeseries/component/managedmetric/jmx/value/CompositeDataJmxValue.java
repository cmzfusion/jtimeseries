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
