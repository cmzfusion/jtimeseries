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
package com.od.jtimeseries.timeseries.aggregation;

import com.od.jtimeseries.timeseries.TimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesEvent;
import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.timeseries.TimeSeriesListener;
import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunction;
import com.od.jtimeseries.timeseries.function.interpolation.LinearInterpolationFunction;
import com.od.jtimeseries.timeseries.impl.AbstractDelegatingTimeSeries;
import com.od.jtimeseries.timeseries.impl.DefaultTimeSeries;
import com.od.jtimeseries.timeseries.interpolation.DefaultInterpolatedTimeSeries;
import com.od.jtimeseries.timeseries.interpolation.InterpolatedTimeSeries;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 21-Jan-2009
 * Time: 19:52:00
 * To change this template use File | Settings | File Templates.
 */
public class DefaultAggregatedTimeSeries extends AbstractDelegatingTimeSeries implements AggregatedTimeSeries {

    private AggregateFunction aggregateFunction;
    private List<InterpolatedTimeSeries> children = new ArrayList<InterpolatedTimeSeries>();
    private long lastTimepoint;
    private TimeSeries masterSeries;

    public DefaultAggregatedTimeSeries(AggregateFunction aggregateFunction) {
        this(new DefaultTimeSeries(), aggregateFunction);
    }

    public DefaultAggregatedTimeSeries(TimeSeries wrappedTimeSeries, AggregateFunction aggregateFunction) {
        super(wrappedTimeSeries);
        this.aggregateFunction = aggregateFunction;
    }

    public synchronized void addTimeSeries(TimeSeries... timeSeries) {
        for ( TimeSeries s : timeSeries) {
            children.add(new DefaultInterpolatedTimeSeries(s, new LinearInterpolationFunction()));
        }
        addChildViewListeners(timeSeries);
        masterSeries = children.get(0);
        recalculateView();
    }

    private void addChildViewListeners(TimeSeries... timeSeries) {
        for ( TimeSeries s : timeSeries) {
            s.addTimeSeriesListener(new AggregateViewListener());
        }
    }

    private void recalculateView() {
        clear();
        lastTimepoint = Long.MAX_VALUE;
        addNewValues();
    }

    //generate aggregated timepoints based on the timepoints in the first child series - the 'master' series
    //but if this has entries earlier than the ealiest timestamp of any of the other child series we won't be able
    //to calculate an aggregated value. So the timepoints we show in the aggregated series will be timepoints from
    //the master series after the first common timepoint
    private void addNewValues() {
        if ( lastTimepoint == Long.MAX_VALUE) {
            lastTimepoint = getFirstCommonTimestamp();
        }

        long latestSharedTimepoint = calcLatestCommonTimestamp();
        while ( latestSharedTimepoint != -1 && lastTimepoint < latestSharedTimepoint) {
            if ( ! addNextValue() ) {
                break;
            }
        }
    }

    private boolean addNextValue() {
        long nextTimepoint = getNextTimestampForAggregatedSeries();
        boolean success = false;
        if ( nextTimepoint != -1 && nextTimepoint > lastTimepoint) {
            success = addNewAggregatedTimepoint(nextTimepoint);
        }
        return success;
    }

    private boolean addNewAggregatedTimepoint(long nextTimepoint) {
        AggregateFunction f = aggregateFunction.newInstance();
        boolean success = addValuesFromChildSeries(nextTimepoint, f);
        if (success) {
            append(new TimeSeriesItem(nextTimepoint, f.calculateAggregateValue()));
            lastTimepoint = nextTimepoint;
        }
        return success;
    }

    private boolean addValuesFromChildSeries(long nextTimepoint, AggregateFunction f) {
        boolean success = true;
        for (InterpolatedTimeSeries s : children) {
            TimeSeriesItem i = s.getInterpolatedValue(nextTimepoint);
            if ( i != null ) {
                f.addValue(i.getValue());
            } else {
                //one of the child timeseries has changed and now cannot supply a value.
                //this change should trigger a new event/recalculate view so we'll just ignore it and abort here
                success = false;
                break;
            }
        }
        return success;
    }

    private long getNextTimestampForAggregatedSeries() {
        return masterSeries.getTimestampAfter(lastTimepoint);
    }

    //return first timestamp for which we can obtain a value for all series, or Long.MAX_VALUE
    private long getFirstCommonTimestamp() {
        long earliest = Long.MIN_VALUE;
        for (TimeSeries s : children) {
            long earliestChildStamp = s.getEarliestTimestamp();
            earliestChildStamp = earliestChildStamp == -1 ? Long.MAX_VALUE : earliestChildStamp;
            earliest = Math.max(earliestChildStamp, earliest );
        }
        return earliest == Long.MIN_VALUE ? Long.MAX_VALUE : earliest;
    }

    //return latest timestamp for which we can obtain a value for all series, or -1
    private long calcLatestCommonTimestamp() {
        long time = Long.MAX_VALUE;
        for (TimeSeries s : children) {
            time = Math.min( s.getLatestTimestamp(), time );
        }
        return time == Long.MAX_VALUE ? -1 : time;
    }

    private class AggregateViewListener implements TimeSeriesListener {

        public void itemsAdded(TimeSeriesEvent h) {
            addNewValues();
        }

        public void itemsRemoved(TimeSeriesEvent h) {
            addNewValues();
        }

        public void itemChanged(TimeSeriesEvent e) {
            addNewValues();
        }

        public void seriesChanged(TimeSeriesEvent e) {
            addNewValues();
        }
    }

}
