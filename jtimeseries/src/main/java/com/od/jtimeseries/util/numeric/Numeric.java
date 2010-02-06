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
package com.od.jtimeseries.util.numeric;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18-Dec-2008
 * Time: 12:30:16
 *
 * Time Series in JTimeseries store Numeric instances against timestamp rather than a standard numeric wrapper type such as Double or Long
 * This is for additional flexibility, since we may wish to store more information against each timepoint than a single number
 *
 * Although the provided LongNumeric and DoubleNumeric are immutable, immutable versions are possible, and may be necssary for certain
 * high performance algorithms.
 *
 * Although each Numeric must at minimum be able to supply a single double value (so that this value may for example be graphed)
 * Numeric classes may be defined which internally store more information, e.g min and max over a time period.
 */
public interface Numeric {

    public static final Numeric NaN = new Numeric() {

        public double doubleValue() {
            return Double.NaN;
        }

        public boolean isNaN() {
            return true;
        }

        public long longValue() {
            return (long)Double.NaN;
        }
    };

    /**
     * @return value of this Numeric, as a double
     */
    public double doubleValue();


    /**
     * @return true, if the value is Double.NaN - which usually indicates a value could not be calculated
     */
    public boolean isNaN();


    long longValue();
}
