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
 * Timeseries in JTimeseries store Numeric instances against timestamp rather than a Number subtype such as Double or Long
 * This is for additional flexibility, since it is less restrictive and easier to adapt or extend a class implement the
 * Numeric interface than it would be to extend java.lang.Number
 *
 * In general, since it is a multi-threaded library, JTimeseries expects Numeric instances to be immutable
 * (or at least not to have their value modified once added to a timeseries). Changing a Numeric value after adding
 * it to a timeseries may produce unpredicatable results in any series which are registered as observers on that
 * series, or perform calculations using its values.
 *
 * Numeric supports the concept of NaN values. NaN generally means we attempted to calculate a value, but no sensible value could be calculated
 * (e.g. we record the Mean of zero values captured during a time period as a NaN value). NaN may also be used to represent that no
 * value exists because no attempt was made to record a value.
 *
 * Numeric instances make a distinction between the above cases. Where a value is NaN because no attempt was made to
 * calculate or record a value, the Numeric instance should return isNull() == true, as well as isNaN() == true. In the case where
 * an attempt was made but no sensible value could be calculated (e.g. Mean of zero values) isNaN() == true but isNull() == false.
 *
 * We have to choose our own value to represent NaN as a long, since there is no formal definition as there is for double in IEEE 754
 * - the primitive value for a long NaN will be Long.MIN_VALUE
 */
public interface Numeric {

    /**
     * @return value of this Numeric, as a double, Double.NaN if either isNaN() or isNull() is true
     */
    public double doubleValue();

    /**
     * @return value of this Numeric, as a long, Long.MIN_VALUE if either isNaN() or isNull() is true
     */
    long longValue();

    /**
     * @return true, if the value is NaN, which indicates a value is not available (no attempt to record a value) or a value could not be calculated (e.g. the Mean of zero items during a capture period)
     */
    public boolean isNaN();

    /**
     * @return true, if isNaN() because no value was recorded (e.g because no attempt was made to record a value)
     */
    public boolean isNull();

}
