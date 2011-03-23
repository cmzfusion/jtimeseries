package com.od.jtimeseries.ui.config;

import com.thoughtworks.xstream.XStream;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 23/03/11
 * Time: 06:47
 */
public class XStreamForJTimeSeries extends XStream {

    public XStreamForJTimeSeries() {
        alias("timeSeriousConfig", TimeSeriousConfig.class);
        alias("visualizerConfiguration", VisualizerConfiguration.class);
        alias("uiTimeSeriesConfig", UiTimeSeriesConfig.class);
        alias("columnSettings", ColumnSettings.class);
        alias("timeSeriesServerConfig", TimeSeriesServerConfig.class);
    }
}
