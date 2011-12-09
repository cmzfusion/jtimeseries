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

    public Numeric calculateResult() {
        return wrappedFunction.calculateResult();
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
        return wrappedFunction.newInstance();
    }

    public final AggregateFunction newInstance(Numeric initialValue) {
        return wrappedFunction.newInstance(initialValue);
    }
}
