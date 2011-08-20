/**
 * Copyright (C) 2011 (nick @ objectdefinitions.com)
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
import com.od.jtimeseries.context.ContextProperties;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.timeseries.util.SeriesUtils;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 04-Jan-2009
 * Time: 13:47:08
 * To change this template use File | Settings | File Templates.
 */
public class SeriesHandler extends AbstractHandler {

    public static final String SERIES_POSTFIX = ".series";
    public final static String SERIES_XSL_RESOURCE = System.getProperty("JTimeSeriesSeriesXslResource", "series.xsl");

    private ThreadLocal<SimpleDateFormat> simpleDateFormat = new ThreadLocal<SimpleDateFormat>() {
        public SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        }
    };

    private static final DecimalFormat decinalFormat = new DecimalFormat("#.##################");


    public SeriesHandler(TimeSeriesContext rootContext) {
        super(rootContext);
    }

    public NanoHTTPD.Response createResponse(String uri, String method, Properties header, Properties parms) {
        NanoHTTPD.Response result;
        TimeSeriesContext context = findContextForRequest(uri);
        if ( context == null) {
            result = createNotFoundResponse(uri);
        } else {
            result = createSeriesResponse(uri, parms, context);
        }
        return result;
    }

    private NanoHTTPD.Response createSeriesResponse(String uri, Properties parms, TimeSeriesContext context) {
        NanoHTTPD.Response result;
        String lastToken = getLastUriToken(uri);
        String seriesId = lastToken.substring(0, lastToken.length() - SERIES_POSTFIX.length() );
        IdentifiableTimeSeries timeSeries = context.getTimeSeries(seriesId);

        Object showAfter = parms.get(HttpParameterName.moreRecentThanTimestamp.name());
        long lastTimestamp = -1;
        if ( showAfter != null ) {
            lastTimestamp = Long.valueOf(showAfter.toString());
        }

        boolean statsOnly = false;
        if ( parms.containsKey(HttpParameterName.statsOnly.name())) {
            statsOnly = Boolean.valueOf(parms.getProperty(HttpParameterName.statsOnly.name()));
        }

        if ( timeSeries == null) {
            result = createNotFoundResponse(uri);
        } else {
            result = new SeriesResponse(context, timeSeries, lastTimestamp, statsOnly);
        }
        return result;
    }

    private class SeriesResponse extends NanoHTTPD.Response {

        private final TimeSeriesContext context;
        private final IdentifiableTimeSeries timeSeries;
        private final long lastTimestamp;
        private final boolean statsOnly;

        public SeriesResponse(TimeSeriesContext context, IdentifiableTimeSeries timeSeries, long lastTimestamp, boolean statsOnly) {
            super(NanoHTTPD.HTTP_OK, "text/xml");
            this.context = context;
            this.timeSeries = timeSeries;
            this.lastTimestamp = lastTimestamp;
            this.statsOnly = statsOnly;
        }

        public void writeResponseBody(OutputStream out, PrintWriter pw) {
            writeTimeSeriesResponse(pw, context, timeSeries, lastTimestamp, statsOnly);
        }
    }

    private void writeTimeSeriesResponse(PrintWriter pw, TimeSeriesContext context, IdentifiableTimeSeries timeSeries, long lastTimestamp, boolean statsOnly) {
        pw.write("<?xml version=\"1.0\"?>");
        pw.write("\n<?xml-stylesheet type=\"text/xsl\" href=\"/");
        pw.write(SERIES_XSL_RESOURCE);
        pw.write("\"?>");
        pw.write("\n<timeSeries>");
        String contextUrl = createUrlForIdentifiable(context);
        appendSeries(pw, contextUrl, timeSeries);
        pw.write("\n<summaryStats>");
        appendSummaryStats(pw, timeSeries);
        pw.write("\n</summaryStats>");
        //statsOnly feature is actually quite important for performance, since showing summary stats only
        //does not require a timeseries to be deserialized, which would be required for seriesItems
        if ( ! statsOnly ) {
            pw.write("\n<seriesItems>");
            appendTimeSeriesItems(timeSeries, pw, lastTimestamp);
            pw.write("\n</seriesItems>");
        }
        pw.write("\n</timeSeries>");
    }

    private void appendSummaryStats(PrintWriter pw, IdentifiableTimeSeries timeSeries) {
        Properties properties = timeSeries.getProperties();
        List<String> propertyNames = ContextProperties.getSummaryStatsPropertyNames(properties);
        for (String p : propertyNames) {
            pw.write("\n  <summaryStat name=\"");
            pw.write(ContextProperties.parseStatisticName(p));
            pw.write("\" value=\"");
            pw.write(properties.getProperty(p));
            pw.write("\"/>");
        }
    }

    private void appendTimeSeriesItems(IdentifiableTimeSeries timeSeries, PrintWriter pw, long lastTimestamp) {
        Collection<TimeSeriesItem> seriesItems = SeriesUtils.getSubSeries(lastTimestamp + 1, timeSeries);  //we require anything more recent than last timestamp
        Date date = new Date();
        for ( TimeSeriesItem h : seriesItems) {
            pw.write("\n<");
            pw.write(ElementName.seriesItem.toString());
            pw.write(" ");
            pw.write(AttributeName.timestamp.toString());
            pw.write("=\"");
            pw.write(String.valueOf(h.getTimestamp()));
            pw.write("\" ");
            date.setTime(h.getTimestamp());
            pw.write("datetime=\"");
            pw.write(simpleDateFormat.get().format(date));
            pw.write("\" ");
            pw.write(AttributeName.value.toString());
            pw.write("=\"");
            pw.write(decinalFormat.format(h.getValue().doubleValue()));
            pw.write("\" ");
            pw.write("/>");
        }
    }

}
