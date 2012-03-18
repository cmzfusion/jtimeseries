package com.od.jtimeseries.net.udp.message;

import com.od.jtimeseries.timeseries.TimeSeriesItem;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 14/03/12
 * Time: 18:27
 *
 * One or more time series values for a series
 *
 * Mandatory fields:
 * SeriesPath               -  path of time series to be updated
 * TimeSeriesValue (1..n)   -  one or more new time series value
 *
 * Optional fields:
 * SourceDescription        -  a description of the agent sending the update
 *
 * Description (deprecated, send as a TS_DESCRIPTION message instead)
 *
 */
public interface TimeSeriesValueMessage extends UdpMessage {

    String getSeriesPath();

    /**
     * @deprecated for greater efficiency use the new TS_Description message type to send the description for a series rather than
     * sending with each value update
     *
     * @return String description for the series, or null if one was not provided
     */
    String getDescription();

    TimeSeriesItem getTimeSeriesItem(int index);

    int getItemCount();
}
