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
 * User: nick
 * Date: 06-Dec-2009
 * Time: 15:48:50
 * To change this template use File | Settings | File Templates.
 *
 * Count of the number of values submitted during a time period
 */
class CountFunction extends AbstractDoubleBasedAggregateFunction {

    private static final String DESCRIPTION = "Count";
    private long count = 0;

    protected void doAddValue(double d) {
        count++;
    }

    public Numeric calculateResult() {
        return DoubleNumeric.valueOf(count);
    }

    public String getDescription() {
        return DESCRIPTION;
    }

    public void clear() {
        count = 0;
    }

    public AggregateFunction nextInstance() {
        return new CountFunction();
    }
}
