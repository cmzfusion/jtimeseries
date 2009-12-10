package com.od.jtimeseries.timeseries.function.aggregate;

import com.od.jtimeseries.util.numeric.Numeric;
import com.od.jtimeseries.util.numeric.LongNumeric;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 06-Dec-2009
 * Time: 15:48:50
 * To change this template use File | Settings | File Templates.
 *
 * Count of the number of values
 */
public class CountFunction extends AbstractDoubleBasedAggregateFunction {

    private static final String NAME = "Count";
    private int count = 0;

    protected void doAddValue(double d) {
        count++;
    }

    public Numeric calculateAggregateValue() {
        return new LongNumeric(count);
    }

    public String getDescription() {
        return NAME;
    }

    public void clear() {
        count = 0;
    }

    public AggregateFunction nextInstance() {
        return new CountFunction();
    }
}
