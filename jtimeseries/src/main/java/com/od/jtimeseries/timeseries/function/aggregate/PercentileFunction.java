/**
 * Copyright (C) 2011 (nick @ objectdefinitions.com)
 *
 * This file is part of JTimeseries.
 *
 * JTimeseries is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JTimeseries is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JTimeseries.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.od.jtimeseries.timeseries.function.aggregate;

import com.od.jtimeseries.util.numeric.DoubleNumeric;
import com.od.jtimeseries.util.numeric.Numeric;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public Numeric calculateResult() {
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

    public AggregateFunction newInstance() {
        return new PercentileFunction(description, percentile);
    }
}
