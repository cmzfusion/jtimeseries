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
package com.od.jtimeseries.ui.config;

import com.od.jtimeseries.util.time.TimeSource;

import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 28/01/11
 * Time: 06:45
 */
public class DomainTimeSelection implements TimeSource {

    private ChartDomainMode mode;
    private int multiple;

    public DomainTimeSelection() {
        this(ChartDomainMode.DAYS, 1);
    }

    public DomainTimeSelection(ChartDomainMode mode, int multiple) {
        this.mode = mode;
        this.multiple = multiple;
    }

    public ChartDomainMode getMode() {
        return mode;
    }

    public int getMultiple() {
        return multiple;
    }

    public long getTime() {
        long time = 0;
        switch (mode) {
            case MINUTES :
                time = System.currentTimeMillis() - ((long)60000 * multiple);
                break;
            case HOURS :
                time = System.currentTimeMillis() - ((long)60000 * 60 * multiple);
                break;
            case DAYS : // 1 day = start of current day, 2 days = start of current - 24hrs
                Calendar c = getStartOfDay();
                c.add(Calendar.DATE, - (multiple - 1));
                time = c.getTimeInMillis();
                break;
            case WEEKS :   // 1 week = start of current week, 2 weeks = start of current week - 7 days
                c = getStartOfDay();
                c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
                c.add(Calendar.WEEK_OF_YEAR, - ( multiple - 1));
                time = c.getTimeInMillis();
                break;
            case MONTHS:  // 1 month = start of current month, 2 months = start of current - 1 months
                c = getStartOfDay();
                c.set(Calendar.DAY_OF_MONTH, 1);
                c.add(Calendar.MONTH, - (multiple - 1));
                time = c.getTimeInMillis();
                break;
            case YEARS:
                c = getStartOfDay();
                c.set(Calendar.DAY_OF_YEAR, 1);
                c.add(Calendar.YEAR, - (multiple - 1 ));
                time = c.getTimeInMillis();
                break;
            case ALL:
                time = 0;
        }
        return time;
    }

    private Calendar getStartOfDay() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.HOUR_OF_DAY, 0);
        return c;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DomainTimeSelection that = (DomainTimeSelection) o;

        if (multiple != that.multiple) return false;
        if (mode != that.mode) return false;

        return true;
    }

    public int hashCode() {
        int result = mode != null ? mode.hashCode() : 0;
        result = 31 * result + multiple;
        return result;
    }

}
