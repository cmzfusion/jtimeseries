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

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18-Dec-2008
 * Time: 13:06:31
 *
 * Superclass for default aggregate function implementations which are based on double values
 * There may be some loss of accuracy due to the conversion to double if the source values are int/long
 * (If this is a problem a custom AggregateFunction should be implemented)
 */
public abstract class AbstractDoubleBasedAggregateFunction implements AggregateFunction {

    public final void addValue(Numeric value) {
        double d = value.doubleValue();
        if ( ! Double.isNaN(d)) {
            doAddValue(d);
        }
    }

    public final void addValue(double d) {
        doAddValue(d);
    }

    public final void addValue(long l) {
        doAddValue((double)l);
    }

    protected abstract void doAddValue(double d);

    public String toString() {
        return getDescription();
    }
}
