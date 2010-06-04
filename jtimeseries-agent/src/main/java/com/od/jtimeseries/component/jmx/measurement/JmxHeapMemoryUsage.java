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

import com.od.jtimeseries.component.jmx.value.CompositeDataJmxValue;
import com.od.jtimeseries.component.jmx.value.JmxValue;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 02-Feb-2010
 * Time: 22:01:21
 *
 * Create an instance via the JmxMetrics factory method
 */
class JmxHeapMemoryUsage extends JmxMeasurement {

    private JmxHeapMemoryUsage(String parentContextPath, String id, String description, JmxValue value) {
        super(parentContextPath, id, description, value);
    }

    static JmxHeapMemoryUsage createJmxMemoryUsage(String parentContextPath, String id, String description) {
        //memory usage is the sum of the heap and non-heap memory
        JmxValue value = new CompositeDataJmxValue("java.lang:type=Memory", "HeapMemoryUsage", "used");
        JmxHeapMemoryUsage m = new JmxHeapMemoryUsage(parentContextPath, id, description, value);
        m.setDivisor(1000000);
        return m;
    }
}
