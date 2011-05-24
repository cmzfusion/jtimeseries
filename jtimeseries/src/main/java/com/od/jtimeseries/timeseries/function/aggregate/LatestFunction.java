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

import com.od.jtimeseries.util.numeric.DoubleNumeric;
import com.od.jtimeseries.util.numeric.Numeric;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 01-Feb-2010
 * Time: 22:59:54
 *
 * Take the latest value from a collection of values
 */
public class LatestFunction extends AbstractDoubleBasedAggregateFunction {

    private static final String DESCRIPTION = "Latest";
    private double latest = Double.NaN;

    public LatestFunction() {
    }

    public LatestFunction(double latest) {
        this.latest = latest;
    }

    protected void doAddValue(double d) {
        latest = d;
    }

    public Numeric calculateAggregateValue() {
        return DoubleNumeric.valueOf(latest);
    }

    public String getDescription() {
        return DESCRIPTION;
    }

    public void clear() {
        latest = Double.NaN;
    }

    public AggregateFunction next() {
        return new LatestFunction(latest);
    }
}
