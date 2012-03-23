package com.od.jtimeseries.net.udp.message;

import com.od.jtimeseries.timeseries.TimeSeriesItem;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 14/03/12
 * Time: 18:27
 *
 * A message containing a single time series value
 * (More efficient support for multiple value messages will be provided in another message type)
 *
 * Mandatory fields:
 * SeriesPath               -  path of time series to be updated
 * TimeSeriesValue          -  A single time series value
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

    TimeSeriesItem getTimeSeriesItem();
}
