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

        public AggregateFunction nextInstance() {
            return new PercentageOfMaxMemoryFunction();
        }
    }
}
