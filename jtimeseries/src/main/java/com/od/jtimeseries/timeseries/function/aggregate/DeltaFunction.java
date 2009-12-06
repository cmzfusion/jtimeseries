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
import com.od.jtimeseries.util.numeric.DoubleNumeric;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 21-Jan-2009
 * Time: 16:19:39
 *
 * Difference between first value for period and last value for period
*/
class DeltaFunction extends AbstractDoubleBasedAggregateFunction {

    private static final String NAME = "Delta";
    private double initialValue = Double.NaN;
    private double currentValue = Double.NaN;

    /**
     * Delta function where initial value is specified up front
     * @param initialValue, initial value from which to measure delta, unless initialValue is Double.NaN in which case the first
     * recorded value will be used
     */
    DeltaFunction(Numeric initialValue) {
        this.initialValue = initialValue.doubleValue();

        //set currentValue too, otherwise we lose the initial value on the first call to
        // nextInstance(), unless we received a new value in the meantime
        this.currentValue = this.initialValue;
    }

    /**
     * Delta function where initial value is defined by the first value received from the value source
     */
    DeltaFunction() {
    }

    public void doAddValue(double value) {
        if ( Double.isNaN(initialValue)) {
            initialValue = value;
        }
        currentValue = value;
    }

    public Numeric calculateAggregateValue() {
        double result = currentValue - initialValue;
        return new DoubleNumeric(result);
    }

    public void clear() {
        initialValue = Double.NaN;
        currentValue = Double.NaN;
    }

    public double getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(double initialValue) {
        this.initialValue = initialValue;
    }

    public String getDescription() {
        return NAME;
    }

    public AggregateFunction nextInstance() {
        return new DeltaFunction(new DoubleNumeric(currentValue));
    }
}
