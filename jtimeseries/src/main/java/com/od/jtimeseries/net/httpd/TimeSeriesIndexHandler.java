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
package com.od.jtimeseries.net.httpd;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 24-May-2009
 * Time: 01:08:12
 * To change this template use File | Settings | File Templates.
 */
public class TimeSeriesIndexHandler extends AbstractHandler {

    public static final String INDEX_POSTFIX = "seriesindex";
    public final static String INDEX_XSL_RESOURCE = System.getProperty("JTimeSeriesTimeseriesIndexXslResource", "seriesindex.xsl");

    public TimeSeriesIndexHandler(TimeSeriesContext rootContext) {
        super(rootContext);
    }

    public NanoHTTPD.Response createResponse(String uri, String method, Properties header, Properties parms) {
        TimeSeriesContext context = findContextForRequest(uri);

        String xmlResponse = createIndexResponse(context);
        return new NanoHTTPD.Response(NanoHTTPD.HTTP_OK, "text/xml", xmlResponse);
    }

    private String createIndexResponse(TimeSeriesContext context) {
        StringBuilder builder = new StringBuilder("<?xml version=\"1.0\"?>");
        builder.append("\n<?xml-stylesheet type=\"text/xsl\" href=\"/").append(INDEX_XSL_RESOURCE).append("\"?>");
        builder.append("\n<timeSeries>");
        for (IdentifiableTimeSeries t : context.findAllTimeSeries().getAllMatches()) {
            appendSeries(createUrlForIdentifiable(t.getParent()), builder, t);
        }
        builder.append("\n</timeSeries>");
        return builder.toString();
    }
}
