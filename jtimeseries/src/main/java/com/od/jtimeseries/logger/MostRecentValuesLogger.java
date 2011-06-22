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
package com.od.jtimeseries.logger;

import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.time.TimePeriod;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 05-Dec-2008
 * Time: 11:04:36
 */
public class MostRecentValuesLogger extends TimeSeriesLogger {

    private DateFormat dateFormat = DateFormat.getDateTimeInstance();
    private NumberFormat numberFormat = NumberFormat.getInstance();

    public MostRecentValuesLogger(TimePeriod logPeriodMillis, IdentifiableTimeSeries... series) {
        super(logPeriodMillis, series);
    }

    public MostRecentValuesLogger(LogMethods logMethods, TimePeriod timePeriod, IdentifiableTimeSeries... series) {
        super(logMethods, timePeriod, series);
    }

    public void setDateFormat(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    public void setNumberFormat(NumberFormat numberFormat) {
        this.numberFormat = numberFormat;
    }

    protected void writeToLogs(IdentifiableTimeSeries series) {
        TimeSeriesItem values = series.getLatestItem();
        if ( values != null ) {
            StringBuilder b = new StringBuilder();

            b.append(series.getDescription()).append(" last value at ");
            b.append(dateFormat.format(new Date(values.getTimestamp())));
            b.append(": ");
            b.append(numberFormat.format(values.getValue()));

            logInfo(b.toString());
        }
    }
}
