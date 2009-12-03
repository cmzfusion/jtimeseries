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
import com.od.jtimeseries.util.numeric.DoubleNumeric;
import com.od.jtimeseries.util.numeric.Numeric;
import com.od.jtimeseries.util.time.TimePeriod;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 08-Jan-2009
 * Time: 10:46:56
 *
 * Capture, for example, the mean count per second, or the mean count per minute, over capturePeriod
 */
public class MeanCountCaptureFunction extends AbstractCaptureFunction {

    private TimePeriod timeIntervalToExpressAverage;
    private double divisor;

    public MeanCountCaptureFunction(TimePeriod capturePeriod, TimePeriod timePeriodInWhichToExpressCount) {
        super(capturePeriod);
        this.timeIntervalToExpressAverage = timePeriodInWhichToExpressCount;
        this.divisor = calculateDivisor();
    }

    public AggregateFunction getFunctionInstance() {
        return new MeanCountFunctionImpl(divisor);
    }

    private double calculateDivisor() {
        return ((double)getCapturePeriodInMillis())/timeIntervalToExpressAverage.getLengthInMillis();
    }

    public String doGetDescription() {
        return "Avg Count per " + timeIntervalToExpressAverage;
    }

    private class MeanCountFunctionImpl implements AggregateFunction {
        private long total = 0;
        private double divisor;

        public MeanCountFunctionImpl(double divisor) {
            this.divisor = divisor;
        }

        public void addValue(Numeric value) {
            addValue(value.doubleValue());
        }

        public void addValue(double value) {
            total += value;
        }

        public void addValue(long value) {
            addValue((double)value);
        }

        public Numeric calculateAggregateValue() {
            double result = total;
            return new DoubleNumeric(result / divisor);
        }

        public String getDescription() {
            return doGetDescription();
        }

        public void clear() {
            total = 0;
        }

        public AggregateFunction newInstance() {
            return new MeanCountFunctionImpl(divisor);
        }
    }
}
