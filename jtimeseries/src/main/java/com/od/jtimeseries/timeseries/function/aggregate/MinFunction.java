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
* User: Nick Ebbutt
* Date: 21-Jan-2009
* Time: 16:20:27
*/
class MinFunction extends AbstractDoubleBasedAggregateFunction {

    private static final String DESCRIPTION = "Min";
    private double min = Double.NaN;

    public void doAddValue(double value) {
        min = Double.isNaN(min) ? value : Math.min(min, value);
    }

    public Numeric calculateAggregateValue() {
        return new DoubleNumeric(min);
    }

    public void clear() {
        min = Double.NaN;
    }

    public String getDescription() {
        return DESCRIPTION;
    }

    public AggregateFunction nextInstance() {
        return new MinFunction();
    }
}
