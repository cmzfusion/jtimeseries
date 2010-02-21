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

    /**
     * The RAW_VALUES function can be used to signify that in addition to a specified list of
     * time period functions, we also want to capture raw values from a source into a timeseries
     * e.g. the below would capture the MAX every 15 mins, the MEAN every 15 mins, and separately
     * a series with all the individual (raw) values recorded
     * context.newValueRecorder(
     *  "Memory Usage",
     *  "Heap Memory Usage",
     *  CaptureFunctions.MAX(Time.minutes(15)),
     *  CaptureFunctions.MEAN(Time.minutes(15)),
     *  CaptureFunctions.RAW_VALUES
     * )
     */
    public static final CaptureFunction RAW_VALUES = new DefaultCaptureFunction(null, null);

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
        return CHANGE(timePeriod, LongNumeric.valueOf(0));
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
        return MEAN_CHANGE(timeIntervalToExpressCount, timePeriod, LongNumeric.valueOf(0));
    }

    /**
     * @return a function which measures net change over a time period, starting from initialValue, expressed
     * as a mean change over timeIntervalToExpressCount
     */
    public static CaptureFunction MEAN_CHANGE(TimePeriod timeIntervalToExpressCount, TimePeriod timePeriod, Numeric initialValue) {
        return new DefaultCaptureFunction(timePeriod, new MeanChangeAggregateFunction(initialValue, timeIntervalToExpressCount, timePeriod));
    }

    public static CaptureFunction COUNT(TimePeriod timePeriod) {
        return new DefaultCaptureFunction(timePeriod, AggregateFunctions.COUNT());
    }

    public static CaptureFunction MEAN_COUNT(TimePeriod timeIntervalToExpressCount, TimePeriod timePeriod) {
        return new DefaultCaptureFunction(timePeriod, new MeanCountAggregateFunction(timeIntervalToExpressCount, timePeriod));
    }

    public static CaptureFunction LAST(TimePeriod timePeriod) {
        return new DefaultCaptureFunction(timePeriod, AggregateFunctions.LAST());
    }

    /**
     * Can be used to indicate we should capture Raw Values from a source, in addition to any time based functions specified
     */
    public static CaptureFunction RAW_VALUES() {
        return RAW_VALUES;
    }

    private static class MeanChangeAggregateFunction extends MeanPerXTimeOverYTimeFunction {

        public MeanChangeAggregateFunction(Numeric initialValue, TimePeriod timeIntervalToExpressCount, TimePeriod timePeriod) {
            super(AggregateFunctions.CHANGE(initialValue), timeIntervalToExpressCount, timePeriod, "Change Per " + timeIntervalToExpressCount + " Over");
        }

        public AggregateFunction next() {
            return new MeanChangeAggregateFunction(getLastAddedValue(), getTimeIntervalToExpressCount(), getTimePeriod());
        }
    }

    private static class MeanCountAggregateFunction extends MeanPerXTimeOverYTimeFunction {

        public MeanCountAggregateFunction(TimePeriod timeIntervalToExpressCount, TimePeriod timePeriod) {
            super(AggregateFunctions.COUNT(), timeIntervalToExpressCount, timePeriod, "Count Per " + timeIntervalToExpressCount + " Over");
        }

        public AggregateFunction next() {
            return new MeanCountAggregateFunction(getTimeIntervalToExpressCount(), getTimePeriod());
        }
    }

    private abstract static class MeanPerXTimeOverYTimeFunction extends AbstractDelegatingAggregateFunction {

        private String description;
        private double divisor;
        private TimePeriod timeIntervalToExpressCount;
        private TimePeriod timePeriod;

        public MeanPerXTimeOverYTimeFunction(AggregateFunction aggregateFunction, TimePeriod timeIntervalToExpressCount, TimePeriod timePeriod, String description) {
            super(aggregateFunction);
            this.timeIntervalToExpressCount = timeIntervalToExpressCount;
            this.timePeriod = timePeriod;
            this.divisor =  ((double) timePeriod.getLengthInMillis()) / timeIntervalToExpressCount.getLengthInMillis();
            this.description = description;
        }

        public Numeric calculateAggregateValue() {
            return DoubleNumeric.valueOf(getWrappedFunction().calculateAggregateValue().doubleValue() / divisor);
        }

        public String getDescription() {
            return description;
        }

        public TimePeriod getTimeIntervalToExpressCount() {
            return timeIntervalToExpressCount;
        }

        public TimePeriod getTimePeriod() {
            return timePeriod;
        }
    }
}
