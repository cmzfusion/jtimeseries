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
package com.od.jtimeseries.capture.function;

import com.od.jtimeseries.timeseries.function.aggregate.AbstractDelegatingAggregateFunction;
import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunction;
import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunctions;
import com.od.jtimeseries.util.numeric.DoubleNumeric;
import com.od.jtimeseries.util.numeric.LongNumeric;
import com.od.jtimeseries.util.numeric.Numeric;
import com.od.jtimeseries.util.time.TimePeriod;

import static com.od.jtimeseries.capture.function.ChainingMode.LAST_VALUE_CHAINING;
import static com.od.jtimeseries.capture.function.ChainingMode.NO_CHAINING;

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
    public static final CaptureFunction RAW_VALUES = new DefaultCaptureFunction(null, null, null);

    public static CaptureFunction MAX(TimePeriod timePeriod) {
        return new DefaultCaptureFunction(timePeriod, AggregateFunctions.MAX(), NO_CHAINING);
    }

    public static CaptureFunction MIN(TimePeriod timePeriod) {
        return new DefaultCaptureFunction(timePeriod, AggregateFunctions.MIN(), NO_CHAINING);
    }

    public static CaptureFunction MEAN(TimePeriod timePeriod) {
        return new DefaultCaptureFunction(timePeriod, AggregateFunctions.MEAN(), NO_CHAINING);
    }

    public static CaptureFunction MEDIAN(TimePeriod timePeriod) {
        return new DefaultCaptureFunction(timePeriod, AggregateFunctions.MEDIAN(), NO_CHAINING);
    }

    public static CaptureFunction PERCENTILE(TimePeriod timePeriod, int percentile) {
        return new DefaultCaptureFunction(timePeriod, AggregateFunctions.PERCENTILE(percentile), NO_CHAINING);
    }

    public static CaptureFunction SUM(TimePeriod timePeriod) {
        return new DefaultCaptureFunction(timePeriod, AggregateFunctions.SUM(), NO_CHAINING);
    }

    /**
     * @return a function which measures net change over a time period, from a starting value of zero
     */
    public static CaptureFunction CHANGE(TimePeriod timePeriod) {
        return CHANGE(timePeriod, LongNumeric.ZERO);
    }

    /**
     * @return a function which measures net change over a time period, starting from the initialValue
     */
    public static CaptureFunction CHANGE(TimePeriod timePeriod, Numeric initialValue) {
        AggregateFunction change = AggregateFunctions.CHANGE();
        change.addValue(initialValue);
        return new DefaultCaptureFunction(timePeriod, change, ChainingMode.LAST_VALUE_CHAINING);
    }

    /**
     * @return a function which measures net change over a time period, starting from zero, expressed
     * as a mean change over timeIntervalToExpressCount
     */
    public static CaptureFunction MEAN_CHANGE(TimePeriod timeIntervalToExpressChange, TimePeriod timePeriod) {
        return MEAN_CHANGE(timeIntervalToExpressChange, timePeriod, LongNumeric.ZERO);
    }

    /**
     * @return a function which measures net change over a time period, starting from initialValue, expressed
     * as a mean change over timeIntervalToExpressCount
     */
    public static CaptureFunction MEAN_CHANGE(TimePeriod timeIntervalToExpressCount, TimePeriod timePeriod, Numeric initialValue) {
        MeanChangeFunction f = new MeanChangeFunction(timeIntervalToExpressCount, timePeriod);
        f.addValue(initialValue);
        return new DefaultCaptureFunction(timePeriod, f, LAST_VALUE_CHAINING);
    }

    /**
     * @return a function which measures the number of values received from ValueSource over the period, a true count rather than the sum of the values received
     */
    public static CaptureFunction VALUE_COUNT(TimePeriod timePeriod) {
        return new DefaultCaptureFunction(timePeriod, AggregateFunctions.COUNT(), NO_CHAINING) {
            protected String doGetDescription() {
                return "ValueCount" + " " + getCapturePeriod();
            }
        };
    }

    /**
     * @return a function which measures the change in a counter over a time period, eg my overall count to date is 1000, the count over the
     * last minute was 50. This function is intended for use with Counter value source.
     */
    public static CaptureFunction COUNT_OVER(TimePeriod timePeriod) {
        return new CountOverFunction(timePeriod, LongNumeric.ZERO);
    }

    /**
     * @return a function which measures the change in a count over a time period, expressed as a mean change per timeIntervalToExpressCount over that period
     * eg my overall count to date is 1000, the mean count per second over the last minute was 5. This function is intended for use with Counter value source
     */
    public static CaptureFunction MEAN_COUNT_OVER(TimePeriod timeIntervalToExpressCount, TimePeriod timePeriod) {
        MeanCountOverFunction f = new MeanCountOverFunction(LongNumeric.ZERO, timeIntervalToExpressCount, timePeriod);
        return new DefaultCaptureFunction(timePeriod, f, LAST_VALUE_CHAINING);
    }

    /**
     * @return a function which records the latest (most recent value overall) at the end of each time period
     * Since this is the latest value overall (not just the latest in the period) this function doesn't include
     * the time period the function description (e.g. will appear simply as Latest rather than Latest 30s)
     */
    public static CaptureFunction LATEST(TimePeriod timePeriod) {
        return new DefaultCaptureFunction(timePeriod, AggregateFunctions.LATEST(), LAST_VALUE_CHAINING){
            protected String doGetDescription() {
                return "Latest";
            }
        };
    }

    /**
     * @return a function for use with Counter value source which records the LatestCount at the end of each time period
     * Since this is the latest value for the counter overall (not just the latest in the period) this function doesn't include
     * the time period the function description (e.g. will appear simply as LatestCount rather than LatestCount 30s)
     */
    public static CaptureFunction LATEST_COUNT(TimePeriod timePeriod) {
        return new DefaultCaptureFunction(timePeriod, AggregateFunctions.LATEST(), LAST_VALUE_CHAINING){
            protected String doGetDescription() {
                return "LatestCount";
            }
        };
    }

    /**
     * Can be used to indicate we should capture Raw Values from a source, in addition to any time based functions specified
     */
    public static CaptureFunction RAW_VALUES() {
        return RAW_VALUES;
    }

    private static class MeanChangeFunction extends MeanPerXTimeOverYTimeFunction {

        public MeanChangeFunction(TimePeriod timeIntervalToExpressCount, TimePeriod timePeriod) {
            super(AggregateFunctions.CHANGE(), timeIntervalToExpressCount, timePeriod, "Change Per " + timeIntervalToExpressCount + " Over");
        }

        public AggregateFunction newInstance() {
            return new MeanChangeFunction(getTimeIntervalToExpressCount(), getTimePeriod());
        }
    }

    /**
     * This function is intended to be used with Counter and is logically identical to 'Change' function, but labelled as a 'Count' rather than 'Change'
     * It seems to be more intuitive to describe a metric as a 'count over 5 minutes' rather than a 'the change in value of a counter over five minutes'.
     */
    private static class CountOverFunction extends DefaultCaptureFunction {

        public CountOverFunction(TimePeriod timePeriod, Numeric initialValue) {
            super(timePeriod, AggregateFunctions.CHANGE(initialValue), LAST_VALUE_CHAINING);
        }

        protected String doGetDescription() {
            return "Count" + " " + getCapturePeriod();
        }
    }

    private static class MeanCountOverFunction extends MeanPerXTimeOverYTimeFunction {

        public MeanCountOverFunction(Numeric initialValue, TimePeriod timeIntervalToExpressCount, TimePeriod timePeriod) {
            super(AggregateFunctions.CHANGE(initialValue), timeIntervalToExpressCount, timePeriod, "Count Per " + timeIntervalToExpressCount + " Over");
        }

        public AggregateFunction newInstance() {
            return new MeanCountOverFunction(getLastAddedValue(), getTimeIntervalToExpressCount(), getTimePeriod());
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

        public Numeric calculateResult() {
            return DoubleNumeric.valueOf(getWrappedFunction().calculateResult().doubleValue() / divisor);
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
