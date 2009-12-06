/**
 * Copyright (C) 2009 (nick @ objectdefinitions.com)
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

import com.od.jtimeseries.util.numeric.Numeric;
import com.od.jtimeseries.util.numeric.LongNumeric;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 01-Feb-2009
 * Time: 22:42:17
 * To change this template use File | Settings | File Templates.
 */
public class AggregateFunctions {

    public static AggregateFunction MAX() {
        return new MaxFunction();
    }

    public static AggregateFunction MEAN() {
        return new MeanFunction();
    }

    public static AggregateFunction MIN() {
        return new MinFunction();
    }

    public static AggregateFunction SUM() {
        return new SumFunction();
    }

    /**
     * @return a Delta function which measures change starting from zero
     */
    public static AggregateFunction DELTA() {
        return new DeltaFunction(new LongNumeric(0));
    }

    /**
     * (initialValue may be Double.NaN if you want to take the initial value from the first recorded value.
     * This sometimes makes sense if you will, for example, get a very large initial value, but are only interested in small
     * changes thereafter)
     * @return A Delta function which measures change starting from initialValue.
     */
    public static AggregateFunction DELTA(Numeric initialValue) {
        return new DeltaFunction(initialValue);
    }

    public static AggregateFunction COUNT() {
        return new CountFunction();
    }
}
