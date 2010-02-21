package com.od.jtimeseries.timeseries.function.aggregate;

import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunction;
import com.od.jtimeseries.util.numeric.Numeric;

/**
 * Created by IntelliJ IDEA.
* User: nick
* Date: 30-Jan-2010
* Time: 19:42:47
* To change this template use File | Settings | File Templates.
*
* Base for aggregate function classes which wrap and delegate to another function internally
*/
public abstract class AbstractDelegatingAggregateFunction implements AggregateFunction {

    private AggregateFunction wrappedFunction;

    public AbstractDelegatingAggregateFunction(AggregateFunction aggregateFunction) {
        this.wrappedFunction = aggregateFunction;
    }

    public void addValue(Numeric value) {
        wrappedFunction.addValue(value);
    }

    public void addValue(double value) {
        wrappedFunction.addValue(value);
    }

    public void addValue(long value) {
        wrappedFunction.addValue(value);
    }

    public Numeric getLastAddedValue() {
        return wrappedFunction.getLastAddedValue();
    }

    public Numeric calculateAggregateValue() {
        return wrappedFunction.calculateAggregateValue();
    }

    public String getDescription() {
        return wrappedFunction.getDescription();
    }

    public void clear() {
        wrappedFunction.clear();
    }

    public AggregateFunction getWrappedFunction() {
        return wrappedFunction;
    }

    public AggregateFunction newInstance() {
        AggregateFunction a = next();
        a.clear();
        return a;
    }

    public abstract AggregateFunction next();
}
