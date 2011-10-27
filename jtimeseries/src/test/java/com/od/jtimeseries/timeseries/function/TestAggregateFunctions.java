package com.od.jtimeseries.timeseries.function;

import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunction;
import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunctions;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 22-Feb-2010
 * Time: 11:35:19
 */
public class TestAggregateFunctions extends TestCase {

    public void testMedian() {
        AggregateFunction f = AggregateFunctions.MEDIAN();
        assertEquals("Median", f.getDescription());

        addValues(f, 1, 5, 2, 8, 7);
        assertEquals(5d, f.calculateAggregateValue().doubleValue());

        f.clear();
        addValues(f, 1, 5, 2, 8, 7, 2);
        assertEquals(3.5d, f.calculateAggregateValue().doubleValue());
    }

    public void testPercentile() {
        //tested against EXCEL percentile function, which apparently shares
        //the calculation logic / interpretation of percentile

        AggregateFunction f = AggregateFunctions.PERCENTILE(90);
        assertEquals("90 Percentile", f.getDescription());

        addValues(f, 6, 47, 49, 15, 42, 41, 7, 39, 43, 40, 36);
        assertEquals(47d, f.calculateAggregateValue().doubleValue());

        f = AggregateFunctions.PERCENTILE(95);
        assertEquals("95 Percentile", f.getDescription());

        addValues(f, 6, 47, 49, 15, 42, 41, 7, 39, 43, 40, 36);
        assertEquals(48d, f.calculateAggregateValue().doubleValue());

        f = AggregateFunctions.PERCENTILE(75);
        assertEquals("75 Percentile", f.getDescription());

        addValues(f, 6, 47, 49, 15, 42, 41, 7, 39, 43, 40, 36);
        assertEquals(42.5d, f.calculateAggregateValue().doubleValue());

        f = AggregateFunctions.PERCENTILE(15);
        assertEquals("15 Percentile", f.getDescription());

        addValues(f, 6, 47, 49, 15, 42, 41, 7, 39, 43, 40, 36);
        assertEquals(11d, f.calculateAggregateValue().doubleValue());
    }

    private void addValues(AggregateFunction f, int... value) {
        for ( int v : value) {
            f.addValue(v);
        }
    }


}
