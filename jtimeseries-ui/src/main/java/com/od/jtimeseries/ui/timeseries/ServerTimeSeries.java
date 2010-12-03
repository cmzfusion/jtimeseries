package com.od.jtimeseries.ui.timeseries;

import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 29-Nov-2010
 * Time: 13:29:09
 */
public class ServerTimeSeries extends DefaultUITimeSeries implements UIPropertiesTimeSeries {

    public ServerTimeSeries(String id, String description, URL seriesUrl) {
        super(id, description);
        setTimeSeriesURL(seriesUrl);
    }
}
