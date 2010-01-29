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

import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunctions;
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
    public static CaptureFunction MEAN_CHANGE(TimePeriod timePeriod, TimePeriod timeIntervalToExpressCount) {
        return MEAN_CHANGE(timePeriod, timeIntervalToExpressCount, new LongNumeric(0));
    }

    /**
     * @return a function which measures net change over a time period, starting from initialValue, expressed
     * as a mean change over timeIntervalToExpressCount
     */
    public static CaptureFunction MEAN_CHANGE(TimePeriod timePeriod, TimePeriod timeIntervalToExpressCount, Numeric initialValue) {
        double divisor = calculateDivisorForMeanChange(timePeriod, timeIntervalToExpressCount);
        return new DefaultCaptureFunction(timePeriod, AggregateFunctions.MEAN_CHANGE(initialValue, divisor));
    }

    private static double calculateDivisorForMeanChange(TimePeriod t, TimePeriod v) {
        return ((double)t.getLengthInMillis())/v.getLengthInMillis();
    }

}
