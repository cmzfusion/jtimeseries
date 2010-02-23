package com.od.jtimeseries.timeseries.function.aggregate;

import com.od.jtimeseries.util.numeric.Numeric;
import com.od.jtimeseries.util.numeric.DoubleNumeric;

import java.util.TreeSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 22-Feb-2010
 * Time: 10:11:36
 */
public class PercentileFunction extends AbstractDoubleBasedAggregateFunction {

    private List<Double> values = new ArrayList<Double>();
    private int percentile;
    private String description;

    public PercentileFunction(int percentile) {
        this(percentile + " Percentile", percentile);
    }

    public PercentileFunction(String description, int percentile) {
        this.description = description;
        this.percentile = percentile;
    }

    protected void doAddValue(double d) {
        values.add(d);
    }

    public Numeric calculateAggregateValue() {
        double result = Double.NaN;
        int N = values.size();
        if ( N > 0) {
            //http://en.wikipedia.org/wiki/Percentile - Alternative Methods
            Collections.sort(values);
            float n = (((float)percentile / 100) * (N - 1)) + 1;
            if ( n == 1 ) {
                result = values.get(0);
            } else if ( n == N) {
                result = values.get(N - 1);
            } else {
                int k = (int)n;
                float d = n - k;
                result = values.get(k - 1) + (d * (values.get(k) - values.get(k - 1)));
            }
        }
        return DoubleNumeric.valueOf(result);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void clear() {
        values.clear();
    }

    public AggregateFunction next() {
        return new PercentileFunction(description, percentile);
    }
}
