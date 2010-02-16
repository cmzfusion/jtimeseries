package com.od.jtimeseries.server.servermetrics.jmx.measurement;

import com.od.jtimeseries.timeseries.function.aggregate.AbstractDoubleBasedAggregateFunction;
import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunction;
import com.od.jtimeseries.util.numeric.DoubleNumeric;
import com.od.jtimeseries.util.numeric.Numeric;
import com.od.jtimeseries.server.servermetrics.jmx.JmxValue;
import com.od.jtimeseries.server.servermetrics.jmx.CompositeDataJmxValue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 04-Feb-2010
 * Time: 10:45:21
 */
class JmxMemoryUsagePercentage extends JmxMeasurement {

    private JmxMemoryUsagePercentage(String parentContextPath, String id, String description, List<JmxValue> listOfJmxValue, AggregateFunction aggregateFunction) {
        super(parentContextPath, id, description, listOfJmxValue, aggregateFunction);
    }

    static JmxMemoryUsagePercentage createMemoryUsagePercentage(String parentContextPath, String id, String description) {
        //memory usage is the sum of the heap and non-heap memory
        List<JmxValue> jmxValue = new LinkedList<JmxValue>();
        jmxValue.add(new CompositeDataJmxValue("java.lang:type=Memory", "HeapMemoryUsage", "used"));
        jmxValue.add(new CompositeDataJmxValue("java.lang:type=Memory", "NonHeapMemoryUsage", "used"));
        jmxValue.add(new CompositeDataJmxValue("java.lang:type=Memory", "HeapMemoryUsage", "max"));
        jmxValue.add(new CompositeDataJmxValue("java.lang:type=Memory", "NonHeapMemoryUsage", "max"));

        return new JmxMemoryUsagePercentage(parentContextPath, id, description, jmxValue, new PercentageOfMaxMemoryFunction());
    }

    private static class PercentageOfMaxMemoryFunction extends AbstractDoubleBasedAggregateFunction {

        private List<Double> values = new ArrayList<Double>();

        protected void doAddValue(double d) {
            values.add(d);
        }

        public Numeric calculateAggregateValue() {
            Numeric result = null;
            if ( values.size() == 4) {
                double sumOfUsed = values.get(0) + values.get(1);
                double sumOfMax = values.get(2) + values.get(3);
                double percentage = sumOfUsed * 100 / sumOfMax;
                result = DoubleNumeric.valueOf(percentage);
            }
            return result;
        }

        public String getDescription() {
            return "JmxPercentageOfMaxMemory";
        }

        public void clear() {
            values.clear();
        }

        public AggregateFunction nextInstance() {
            return new PercentageOfMaxMemoryFunction();
        }
    }
}
