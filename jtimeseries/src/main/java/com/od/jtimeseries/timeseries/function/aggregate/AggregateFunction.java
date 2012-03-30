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

import com.od.jtimeseries.util.numeric.Numeric;

/**
 * Implement a function which calculates a result from a number of input values
 *
 * A new instance of the AggregateFunction is generally created for each calculation, using the
 * nextInstance() method. This enables the implementation to seed the new function with initial
 * values where this is useful - this is termed function chaining in jTimeSeries
 */
public interface AggregateFunction {

    void addValue(Numeric value);

    void addValue(double value);

    void addValue(long value);

    Numeric getLastAddedValue();

    /**
     * @return a Numeric value which is the result of the aggregation.
     * If it is not possible to calculate a value, the Numeric returned should have numeric.isNaN() == true, or use Numeric.NaN
     */
    Numeric calculateResult();

    String getDescription();

    void clear();

    /**
     * @return the next function instance to use
     */
    AggregateFunction nextInstance();

}
