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
package com.od.jtimeseries.server.summarystats;

import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunction;

import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 21-Feb-2010
 * Time: 12:53:14
 * To change this template use File | Settings | File Templates.
 */
public class TodayOnlySummaryStatistic extends DefaultSummaryStatistic {

    private static final int MILLIS_IN_DAY = 24 * 60 * 60 * 1000;

    public TodayOnlySummaryStatistic(String name, AggregateFunction function) {
        super(name, function);
    }

    protected long getStartTime() {
        Calendar c = getStartOfToday();
        return c.getTime().getTime();
    }
    
    protected long getEndTime() {
        Calendar c = getStartOfToday();
        return c.getTime().getTime() + MILLIS_IN_DAY;
    }

    private Calendar getStartOfToday() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.HOUR_OF_DAY, 0);
        return c;
    }

}
