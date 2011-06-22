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
package com.od.jtimeseries.component.managedmetric.jmx.measurement;

import com.od.jtimeseries.component.managedmetric.jmx.value.CompositeDataJmxValue;
import com.od.jtimeseries.component.managedmetric.jmx.value.JmxValue;
import com.od.jtimeseries.timeseries.function.aggregate.AbstractDoubleBasedAggregateFunction;
import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunction;
import com.od.jtimeseries.util.numeric.DoubleNumeric;
import com.od.jtimeseries.util.numeric.Numeric;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 04-Feb-2010
 * Time: 10:45:21
 */
class JmxHeapMemoryPercentageOfMax extends JmxMeasurement {

    private JmxHeapMemoryPercentageOfMax(String parentContextPath, String id, String description, List<JmxValue> listOfJmxValue, AggregateFunction aggregateFunction) {
        super(parentContextPath, id, description, listOfJmxValue, aggregateFunction);
    }

    static JmxHeapMemoryPercentageOfMax createMemoryUsagePercentage(String parentContextPath, String id, String description) {
        List<JmxValue> jmxValue = new LinkedList<JmxValue>();
        jmxValue.add(new CompositeDataJmxValue("java.lang:type=Memory", "HeapMemoryUsage", "used"));
        jmxValue.add(new CompositeDataJmxValue("java.lang:type=Memory", "HeapMemoryUsage", "max"));

        return new JmxHeapMemoryPercentageOfMax(parentContextPath, id, description, jmxValue, new PercentageOfMaxMemoryFunction());
    }

    private static class PercentageOfMaxMemoryFunction extends AbstractDoubleBasedAggregateFunction {

        private List<Double> values = new ArrayList<Double>();

        protected void doAddValue(double d) {
            values.add(d);
        }

        public Numeric calculateAggregateValue() {
            Numeric result = DoubleNumeric.NaN;
            if ( values.size() == 2) {
                double sumOfUsed = values.get(0);
                double sumOfMax = values.get(1);
                double percentage = sumOfUsed * 100 / sumOfMax;
                result = DoubleNumeric.valueOf(percentage);
            }
            return result;
        }

        public String getDescription() {
            return "JmxPercentageOfMaxHeapMemory";
        }

        public void clear() {
            values.clear();
        }

        public AggregateFunction next() {
            return new PercentageOfMaxMemoryFunction();
        }
    }
}
