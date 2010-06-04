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
package com.od.jtimeseries.component.jmx.measurement;

import com.od.jtimeseries.component.jmx.value.JmxValue;
import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunction;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 04-Feb-2010
 * Time: 10:00:24
 */
class JmxCpuTimePercentage extends JmxMeasurement {

    private JmxCpuTimePercentage(String parentContextPath, String id, String description, List<JmxValue> listOfJmxValue, AggregateFunction aggregateFunction) {
        super(parentContextPath, id, description, listOfJmxValue, aggregateFunction);
    }

    static JmxCpuTimePercentage createJmxCpuTimePercentage(String parentContextPath, String id, String description) {
        //memory usage is the sum of the heap and non-heap memory
        List<JmxValue> jmxValue = new LinkedList<JmxValue>();

        //The collection count attribute on all GarbageCollection type mbeans
        jmxValue.add(new JmxValue("java.lang:type=OperatingSystem", "ProcessCpuTime"));

        JmxCpuTimePercentage jmxCpuTimePercentage = new JmxCpuTimePercentage(parentContextPath, id, description, jmxValue, new JmxPercentageOfTimeFunction());
        jmxCpuTimePercentage.setDivisor(1000000); //cpu times appear to be nanoseconds, we need milliseconds
        return jmxCpuTimePercentage;
    }

}
