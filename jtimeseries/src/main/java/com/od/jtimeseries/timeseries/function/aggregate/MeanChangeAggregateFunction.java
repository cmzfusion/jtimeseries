package com.od.jtimeseries.timeseries.function.aggregate;

import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunction;
import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunctions;
import com.od.jtimeseries.util.numeric.Numeric;
import com.od.jtimeseries.util.numeric.DoubleNumeric;

/**
* Created by IntelliJ IDEA.
* User: nick
* Date: 10-Dec-2009
* Time: 23:20:52
*
* A wrapper around ChangeFunction, to implement division of the net change by a specified value
*/
class MeanChangeAggregateFunction implements AggregateFunction {

    private static final String NAME = "MeanChange";
    private double divisor;
    private AggregateFunction c;

    /**
     * Change function where initial value is specified up front
     */
    MeanChangeAggregateFunction(double divisor, Numeric initialValue) {
        this.divisor = divisor;
        c = AggregateFunctions.CHANGE(initialValue);
    }

    /**
     * Change function where initial value is defined by the first value received from the value source
     */
    MeanChangeAggregateFunction(double divisor) {
        this.divisor = divisor;
        c = AggregateFunctions.CHANGE();
    }

    public void addValue(Numeric value) {
        c.addValue(value);
    }

    public void addValue(double value) {
        c.addValue(value);
    }

    public void addValue(long value) {
        c.addValue(value);
    }

    public Numeric getLastAddedValue() {
        return c.getLastAddedValue();
    }

    public Numeric calculateAggregateValue() {
        double result = c.calculateAggregateValue().doubleValue();
        return new DoubleNumeric(result / divisor);
    }

    public String getDescription() {
        return NAME;
    }

    public void clear() {
        c.clear();
    }

    public AggregateFunction nextInstance() {
        return new MeanChangeAggregateFunction(divisor, c.getLastAddedValue());
    }
}
