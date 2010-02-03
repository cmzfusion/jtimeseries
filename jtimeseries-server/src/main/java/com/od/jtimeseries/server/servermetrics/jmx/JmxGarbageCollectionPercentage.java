package com.od.jtimeseries.server.servermetrics.jmx;

import com.od.jtimeseries.timeseries.function.aggregate.AbstractDelegatingAggregateFunction;
import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunction;
import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunctions;
import com.od.jtimeseries.util.numeric.DoubleNumeric;
import com.od.jtimeseries.util.numeric.Numeric;
import com.od.jtimeseries.util.time.TimePeriod;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 03-Feb-2010
 * Time: 17:55:59
 */
public class JmxGarbageCollectionPercentage extends JmxMetric {

    private JmxGarbageCollectionPercentage(TimePeriod timePeriod, String parentContextPath, String id, String description, String serviceUrl, List<JmxValue> listOfJmxValue, AggregateFunction aggregateFunction) {
        super(timePeriod, parentContextPath, id, description, serviceUrl, listOfJmxValue, aggregateFunction);
    }

    public static JmxGarbageCollectionPercentage createJmxGarbageCollectionPercentage(TimePeriod timePeriod, String parentContextPath, String id, String description, String serviceUrl) {
        //memory usage is the sum of the heap and non-heap memory
        List<JmxValue> jmxValue = new LinkedList<JmxValue>();
        
        //The collection count attribute on all GarbageCollection type mbeans
        jmxValue.add(new JmxValue("java.lang:type=GarbageCollector,*", "CollectionTime"));

        return new JmxGarbageCollectionPercentage(timePeriod, parentContextPath, id, description, serviceUrl, jmxValue, new GcPercentageAggregateFunction(timePeriod.getLengthInMillis()));
    }

    //Calculates the sum of the time spent by all garbage collectors divided by period length * 100
    private static class GcPercentageAggregateFunction extends AbstractDelegatingAggregateFunction {

        private long lastCollectionTime;
        private long millisInPeriod;

        public GcPercentageAggregateFunction(long millisInPeriod) {
            this(0, millisInPeriod);
        }

        private GcPercentageAggregateFunction(long lastCollectionTime, long millisInPeriod) {
            super(AggregateFunctions.SUM());
            this.lastCollectionTime = lastCollectionTime;
            this.millisInPeriod = millisInPeriod;
        }

        public Numeric calculateAggregateValue() {
            double result = Double.NaN;
            Numeric newCollectionTime = super.calculateAggregateValue();
            if ( ! Double.isNaN(newCollectionTime.doubleValue())) {
                if ( lastCollectionTime != 0) {
                    long difference = newCollectionTime.longValue() - lastCollectionTime;
                    result = (100 * difference) / (double)millisInPeriod;
                    //System.out.println("Calculated percentage " + result);
                }
                this.lastCollectionTime = newCollectionTime.longValue();                
            }
            return new DoubleNumeric(result);
        }

        public AggregateFunction nextInstance() {
            return new GcPercentageAggregateFunction(lastCollectionTime, millisInPeriod);
        }
    }
}
