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
 * Date: 18-Dec-2008
 * Time: 13:06:31
 *
 * Superclass for default aggregate function implementations which return a DoubleNumeric as the result,
 * and generally use a double as the internal representation.
 *
 * Functions derived from AbstractDoubleBasedAggregateFunction ignore any NaN values
 * which are added
 */
public abstract class AbstractDoubleBasedAggregateFunction implements AggregateFunction {

    private double lastValue = Double.NaN;

    public final void addValue(Numeric value) {
        add(value.doubleValue());
    }

    public final void addValue(double d) {
        add(d);
    }

    public final void addValue(long l) {
        add((double)l);
    }

    private void add(double d) {
        if ( ! Double.isNaN(d)) {
            this.lastValue = d;
            doAddValue(d);
        }
    }

    protected abstract void doAddValue(double d);

    public Numeric getLastAddedValue() {
        return DoubleNumeric.valueOf(lastValue);
    }

    public String toString() {
        return getDescription();
    }

    public AggregateFunction newInstance() {
        AggregateFunction a = next();
        a.clear();
        return a;
    }
}
