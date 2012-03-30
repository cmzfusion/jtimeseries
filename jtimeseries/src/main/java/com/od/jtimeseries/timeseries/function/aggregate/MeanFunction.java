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

/**
 * Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 21-Jan-2009
* Time: 16:20:09
*/
class MeanFunction extends AbstractDoubleBasedAggregateFunction {

    private static final String DESCRIPTION = "Mean";
    private double sum;
    private long count;

    protected void doAddValue(double d) {
        sum += d;
        count++;
    }

    public Numeric calculateResult() {
        double result = Double.NaN;
        if ( count > 0 ) {
            result = sum / count;
        }
        return DoubleNumeric.valueOf(result);
    }

    public void clear() {
        sum = 0;
        count = 0;
    }

    public String getDescription() {
        return DESCRIPTION;
    }

    public AggregateFunction nextInstance() {
        return new MeanFunction();
    }


}
