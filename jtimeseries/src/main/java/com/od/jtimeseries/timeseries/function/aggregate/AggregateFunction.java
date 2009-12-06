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

public interface AggregateFunction {

    void addValue(Numeric value);

    void addValue(double value);

    void addValue(long value);

    Numeric calculateAggregateValue();

    String getDescription();

    void clear();

    /**
     * This method is to enable the use of the prototype pattern, so that an AggregateFunction instance can be used
     * as a prototype. Functions are sometimes used in sequence with the previous function providing an initial
     * value to the next (in the case of Delta function, for example)
     *
     * @return a new instance of the AggregateFunction
     */
    AggregateFunction nextInstance();

}
