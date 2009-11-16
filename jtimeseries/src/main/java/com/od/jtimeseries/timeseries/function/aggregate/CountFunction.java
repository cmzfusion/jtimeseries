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

import com.od.jtimeseries.util.numeric.LongNumeric;
import com.od.jtimeseries.util.numeric.Numeric;

/**
 * Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 21-Jan-2009
* Time: 16:19:39
*/
class CountFunction extends AbstractDoubleBasedAggregateFunction {

    private static final String NAME = "Count";
    private long total = 0;

    public void doAddValue(double value) {
        total += value;
    }

    public Numeric calculateAggregateValue() {
        long result = total;
        total = 0;
        return new LongNumeric(result);
    }

    public String getDescription() {
        return NAME;
    }

    public AggregateFunction newInstance() {
        return new CountFunction();
    }
}
