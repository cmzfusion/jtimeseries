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

import java.io.OutputStream;
import java.io.PrintWriter;
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
        NanoHTTPD.Response result;
        TimeSeriesContext context = findContextForRequest(uri);
        if ( context == null) {
            result = createNotFoundResponse(uri);
        } else {
            result = new IndexResponse(context);
        }
        return result;
    }

    private void writeIndexResponse(PrintWriter pw, TimeSeriesContext context) {
        pw.write("<?xml version=\"1.0\"?>");
        pw.write("\n<?xml-stylesheet type=\"text/xsl\" href=\"/");
        pw.write(INDEX_XSL_RESOURCE);
        pw.write("\"?>");
        pw.write("\n<timeSeries>");
        for (IdentifiableTimeSeries t : context.findAllTimeSeries().getAllMatches()) {
            appendSeries(pw, createUrlForIdentifiable(t.getParent()), t);
        }
        pw.write("\n</timeSeries>");
    }

    private class IndexResponse extends NanoHTTPD.Response {

        private TimeSeriesContext context;

        public IndexResponse(TimeSeriesContext context) {
            super(NanoHTTPD.HTTP_OK, "text/xml");
            this.context = context;
        }

        public void writeResponseBody(OutputStream out, PrintWriter pw) {
            writeIndexResponse(pw, context);
        }
    }

}
