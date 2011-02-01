package com.od.jtimeseries.timeseries;

import com.od.jtimeseries.util.numeric.Numeric;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 31/01/11
 * Time: 11:07
 *
 * TimeSeries are made up of TimeSeriesItem, each TimeSeriesItem has a timestamp with an associated value
 *
 * It is up to the implementation to define whether a TimeSeriesItem is mutable - whether timestamp and/or value can be changed
 * It is also up to the implementation to define equals() and hashCode()
 *
 * It will very rarely, if ever, be sensible for a TimeSeriesItem to have a mutable timestamp.
 * TimeSeries implementations which expect immutable timestamp will probably fail if timestamps are changed after the
 * series is populated. Most, if not all, the current TimeSeries implementation expect timestamp to be fixed.
 *
 * It is more likely that mutable values are sensible - there may be big performance advantages in being able to change the value at a
 * given timepoint without having to create a new TimeSeriesItem instance.
 * If changing a value for an item in a timeseries, it may be necessary to arrange for the appropriate TimeSeriesEvent to be fired to TimeSeriesListener,
 * so that observers of the TimeSeries are informed of the value change.
 *
 * It is also be possible to create TimeSeriesItem implementations which group together several values for the same timestamp, eg.
 * a mean, max and min value, although only one of these will be exposed as the primary value via the TimeSeriesItem interface
 */
public interface TimeSeriesItem {

    long getTimestamp();

    Numeric getValue();

    double doubleValue();

    long longValue();
}
