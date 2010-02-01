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
package com.od.jtimeseries.capture.function;

import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunction;
import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunctions;
import com.od.jtimeseries.timeseries.function.aggregate.AbstractDelegatingAggregateFunction;
import com.od.jtimeseries.util.numeric.DoubleNumeric;
import com.od.jtimeseries.util.numeric.LongNumeric;
import com.od.jtimeseries.util.numeric.Numeric;
import com.od.jtimeseries.util.time.TimePeriod;

public class CaptureFunctions {

    public static CaptureFunction MAX(TimePeriod timePeriod) {
        return new DefaultCaptureFunction(timePeriod, AggregateFunctions.MAX());
    }

    public static CaptureFunction MIN(TimePeriod timePeriod) {
        return new DefaultCaptureFunction(timePeriod, AggregateFunctions.MIN());
    }

    public static CaptureFunction MEAN(TimePeriod timePeriod) {
        return new DefaultCaptureFunction(timePeriod, AggregateFunctions.MEAN());
    }

    public static CaptureFunction SUM(TimePeriod timePeriod) {
        return new DefaultCaptureFunction(timePeriod, AggregateFunctions.SUM());
    }

    /**
     * @return a function which measures net change over a time period, from a starting value of zero
     */
    public static CaptureFunction CHANGE(TimePeriod timePeriod) {
        return CHANGE(timePeriod, new LongNumeric(0));
    }

    /**
     * @return a function which measures net change over a time period, starting from the initialValue
     */
    public static CaptureFunction CHANGE(TimePeriod timePeriod, Numeric initialValue) {
        return new DefaultCaptureFunction(timePeriod, AggregateFunctions.CHANGE(initialValue));
    }

    /**
     * @return a function which measures net change over a time period, starting from zero, expressed
     * as a mean change over timeIntervalToExpressCount
     */
    public static CaptureFunction MEAN_CHANGE(TimePeriod timeIntervalToExpressCount, TimePeriod timePeriod) {
        return MEAN_CHANGE(timeIntervalToExpressCount, timePeriod, new LongNumeric(0));
    }

    /**
     * @return a function which measures net change over a time period, starting from initialValue, expressed
     * as a mean change over timeIntervalToExpressCount
     */
    public static CaptureFunction MEAN_CHANGE(TimePeriod timeIntervalToExpressCount, TimePeriod timePeriod, Numeric initialValue) {
        final double divisor = ((double) timePeriod.getLengthInMillis()) / timeIntervalToExpressCount.getLengthInMillis();
        return new DefaultCaptureFunction(timePeriod, new MeanChangeAggregateFunction("Mean Change Per " + timeIntervalToExpressCount + " Over", initialValue, divisor));
    }

    /**
     * Count Over is actually the same as the 'change' in the count during a time period, but when used with a Counter
     * this version can result in a more intelligable description:
     * (e.g  Login Attempts (Count Over 10ms) rather than Login Attempts (Change 10ms))
     * @return a function which records a change in a count in a given time period.
     */
    public static CaptureFunction COUNT_OVER(TimePeriod timePeriod) {
        return COUNT_OVER(timePeriod, new LongNumeric(0));
    }

    public static CaptureFunction COUNT_OVER(TimePeriod timePeriod, Numeric initialValue) {
        return new DefaultCaptureFunction(timePeriod, AggregateFunctions.CHANGE("Count Over", initialValue));
    }

    /**
     * Count Over is actually the same as the 'change' in the count during a time period, but when used with a Counter
     * this version can result in a more intelligable description:
     * (e.g  Login Attempts (Count Over 10ms) rather than Login Attempts (Change 10ms))
     * @return a function which records a change in a count in a given time period.
     */
    public static CaptureFunction MEAN_COUNT_OVER(TimePeriod timeIntervalToExpressCount, TimePeriod timePeriod) {
        return MEAN_COUNT_OVER(timeIntervalToExpressCount, timePeriod, new LongNumeric(0));
    }

    public static CaptureFunction MEAN_COUNT_OVER(TimePeriod timeIntervalToExpressCount, TimePeriod timePeriod, Numeric initialValue) {
        final double divisor = ((double) timePeriod.getLengthInMillis()) / timeIntervalToExpressCount.getLengthInMillis();
        return new DefaultCaptureFunction(timePeriod, new MeanChangeAggregateFunction("Mean Count Per " + timeIntervalToExpressCount + " Over", initialValue, divisor));
    }



    private static class MeanChangeAggregateFunction extends AbstractDelegatingAggregateFunction {

        private String description;
        private double divisor;
        
        public MeanChangeAggregateFunction(String description, Numeric initialValue, double divisor) {
            super(AggregateFunctions.CHANGE(description, initialValue));
            this.description = description;
            this.divisor = divisor;
        }

        public Numeric calculateAggregateValue() {
            return new DoubleNumeric(getWrappedFunction().calculateAggregateValue().doubleValue() / divisor);
        }

        public AggregateFunction nextInstance() {
            return new MeanChangeAggregateFunction(description, getLastAddedValue(), divisor);
        }
    }


}
