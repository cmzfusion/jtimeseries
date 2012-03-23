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
package com.od.jtimeseries.util.numeric;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18-Dec-2008
 * Time: 12:30:16
 *
 * JTimeseries stores Numeric instances against timestamp rather than a Number subtype such as Double or Long
 *
 * This is for additional flexibility, since it is less restrictive and easier to adapt or extend a class implement the
 * Numeric interface than it would be to extend java.lang.Number.
 *
 * In general, since it is a multi-threaded library, JTimeseries expects Numeric instances to be immutable
 * (or at least not to have their value modified once added to a timeseries). Changing a Numeric value after adding
 * it to a timeseries may produce unpredictable results in any series which are registered as observers on that
 * series, or perform calculations using its values.
 *
 * Numeric supports the concept of NaN values. NaN generally means we attempted to calculate a value, but no sensible value could be calculated
 * (e.g. we record the Mean of zero values captured during a time period as a NaN value).
 *
 */
public interface Numeric {

    /**
     * @return value of this Numeric, as a double, Double.NaN if isNaN() is true
     */
    public double doubleValue();

    /**
     * @return value of this Numeric as a long
     * @throws UnsupportedOperationException if you call this method when isNaN() == true;
     */
    long longValue();

    /**
     * @return true, if the value is NaN, which indicates a value is not available (no attempt to record a value)
     * or a value could not be calculated (e.g. the Mean of zero items during a capture period)
     */
    public boolean isNaN();

}
