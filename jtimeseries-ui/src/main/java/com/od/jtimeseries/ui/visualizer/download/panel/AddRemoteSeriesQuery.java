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
package com.od.jtimeseries.ui.visualizer.download.panel;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.timeseries.impl.DefaultIdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.impl.DefaultTimeSeries;
import com.od.jtimeseries.ui.timeseries.ChartSeriesEvent;
import com.od.jtimeseries.ui.timeseries.ChartSeriesListener;
import com.od.jtimeseries.ui.timeseries.ChartingTimeSeries;
import com.od.jtimeseries.ui.timeseries.RemoteHttpTimeSeries;
import com.od.jtimeseries.ui.visualizer.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.util.time.Time;
import com.od.jtimeseries.util.time.TimePeriod;

import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 12-Jan-2009
 * Time: 16:18:37
 */
public class AddRemoteSeriesQuery {

    private TimeSeriesContext parent;
    private URL remoteContextUrl;
    private DisplayNameCalculator displayNameCalculator;

    public AddRemoteSeriesQuery(TimeSeriesContext destinationContext, URL remoteContextUrl, DisplayNameCalculator displayNameCalculator) {
        this.parent = destinationContext;
        this.remoteContextUrl = remoteContextUrl;
        this.displayNameCalculator = displayNameCalculator;
    }

    public void runQuery() throws Exception {
        FindRemoteTimeSeriesQuery findAllTimeSeries = new FindRemoteTimeSeriesQuery(remoteContextUrl);
        findAllTimeSeries.runQuery();
        for ( FindRemoteTimeSeriesQuery.RemoteTimeSeries timeSeriesResult : findAllTimeSeries.getResult()) {
            createTimeSeries(timeSeriesResult);
        }
    }

    private void createTimeSeries(FindRemoteTimeSeriesQuery.RemoteTimeSeries timeSeriesResult) {
        TimeSeriesContext c = parent.createContext(timeSeriesResult.getParentPath());
        ChartingTimeSeries series = new ChartingTimeSeries(
            new RemoteHttpTimeSeries(timeSeriesResult.getId(), timeSeriesResult.getDescription(), timeSeriesResult.getSeriesURL(), Time.minutes(1)),
            new ChartSeriesListener() { //the http series will not listen, is just being used to store the url
                public void chartSeriesChanged(ChartSeriesEvent chartSeriesEvent) {
                }
            }
        );
        series.putAllProperties(timeSeriesResult.getSummaryStatsProperties());
        c.addChild(series);

        //must do this after adding the series to the context because the contextPath will not
        //be complete until this is done, and the name calculation is based on the path
        displayNameCalculator.setDisplayName(series);
    }

}
