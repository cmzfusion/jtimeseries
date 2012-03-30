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
 * Date: 21-Jan-2009
 * Time: 16:19:39
 *
 * A function used to measure net change, i.e. the difference between the initial value and the last value during a time
 * period. This can be useful to calculate net change during a time period, e.g. how much a count has increased
 *
 * nextInstance returns a new ChangeFunction which takes the final value of the current function
 * as its initial value. (If this was not the case, we would lose track of any change between the final
 * value captured into one function and the first value captured into the second)
*/
class ChangeFunction extends AbstractDoubleBasedAggregateFunction implements ChainedFunction {

    private String description = "Change";
    private double initialValue = Double.NaN;
    private double currentValue = Double.NaN;


    ChangeFunction() {
        this("Change", Double.NaN);
    }

    ChangeFunction(double initialValue) {
        this("Change", initialValue);
    }

    /**
     * Change function where initial value is specified up front
     */
    ChangeFunction(String description, double initialValue) {
        this.description = description;
        this.initialValue = initialValue;
        this.currentValue = initialValue;
    }

    public void doAddValue(double value) {
        if ( Double.isNaN(initialValue)) { //an initial value is not yet set
            initialValue = value;
        }
        currentValue = value;
    }

    public Numeric calculateResult() {
        Numeric result = DoubleNumeric.NaN;
        if (! Double.isNaN(currentValue) && ! Double.isNaN(initialValue)) {
            result = DoubleNumeric.valueOf(currentValue - initialValue);
        }
        return result;
    }

    public void clear() {
        initialValue = Double.NaN;
        currentValue = Double.NaN;
    }

    public String getDescription() {
        return description;
    }

    public AggregateFunction nextInstance() {
        return new ChangeFunction(description, currentValue);
    }
}
