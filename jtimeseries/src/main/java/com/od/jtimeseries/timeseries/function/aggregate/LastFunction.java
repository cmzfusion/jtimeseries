package com.od.jtimeseries.timeseries.function.aggregate;

import com.od.jtimeseries.util.numeric.DoubleNumeric;
import com.od.jtimeseries.util.numeric.Numeric;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 01-Feb-2010
 * Time: 22:59:54
 * To change this template use File | Settings | File Templates.
 */
public class LastFunction extends AbstractDoubleBasedAggregateFunction {

    private static final String DESCRIPTION = "Last";
    private double last = Double.NaN;

    protected void doAddValue(double d) {
        last = d;
    }

    public Numeric calculateAggregateValue() {
        return DoubleNumeric.valueOf(last);
    }

    public String getDescription() {
        return DESCRIPTION;
    }

    public void clear() {
        last = Double.NaN;
    }

    public AggregateFunction next() {
        return new LastFunction();
    }
}
