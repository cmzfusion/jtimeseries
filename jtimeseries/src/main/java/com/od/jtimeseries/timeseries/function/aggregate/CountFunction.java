package com.od.jtimeseries.timeseries.function.aggregate;

import com.od.jtimeseries.util.numeric.Numeric;
import com.od.jtimeseries.util.numeric.DoubleNumeric;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 06-Dec-2009
 * Time: 15:48:50
 * To change this template use File | Settings | File Templates.
 *
 * Count of the number of values
 */
class CountFunction extends AbstractDoubleBasedAggregateFunction {

    private static final String DESCRIPTION = "Count";
    private long count = 0;

    protected void doAddValue(double d) {
        count++;
    }

    public Numeric calculateAggregateValue() {
        return DoubleNumeric.valueOf(count);
    }

    public String getDescription() {
        return DESCRIPTION;
    }

    public void clear() {
        count = 0;
    }

    public AggregateFunction nextInstance() {
        return new CountFunction();
    }
}
