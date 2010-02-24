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
import com.od.jtimeseries.context.ContextProperties;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesItem;

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

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static final DecimalFormat decinalFormat = new DecimalFormat("#.##################");


    public SeriesHandler(TimeSeriesContext rootContext) {
        super(rootContext);
    }

    public NanoHTTPD.Response createResponse(String uri, String method, Properties header, Properties parms) {
        TimeSeriesContext context = findContextForRequest(uri);
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

        NanoHTTPD.Response response;
        if ( timeSeries == null) {
            response = createNotFoundResponse(uri);
        } else {
            String xmlResponse = createTimeSeriesResponse(context, timeSeries, lastTimestamp, statsOnly);
            response = new NanoHTTPD.Response(NanoHTTPD.HTTP_OK, "text/xml", xmlResponse);
        }
        return response;
    }

    private String createTimeSeriesResponse(TimeSeriesContext context, IdentifiableTimeSeries timeSeries, long lastTimestamp, boolean statsOnly) {
        StringBuilder builder = new StringBuilder("<?xml version=\"1.0\"?>");
        builder.append("\n<?xml-stylesheet type=\"text/xsl\" href=\"/").append(SERIES_XSL_RESOURCE).append("\"?>");
        builder.append("\n<timeSeries>");
        String contextUrl = createUrlForIdentifiable(context);
        appendSeries(contextUrl, builder, timeSeries);
        builder.append("\n<summaryStats>");
        appendSummaryStats(contextUrl, builder, timeSeries);
        builder.append("\n</summaryStats>");
        //statsOnly feature is actually quite important for performance, since showing summary stats only
        //does not require a timeseries to be deserialized, which would be required for seriesItems
        if ( ! statsOnly ) {
            builder.append("\n<seriesItems>");
            appendTimeSeriesItems(timeSeries, builder, lastTimestamp);
            builder.append("\n</seriesItems>");
        }
        builder.append("\n</timeSeries>");
        return builder.toString();
    }

    private void appendSummaryStats(String contextUrl, StringBuilder builder, IdentifiableTimeSeries timeSeries) {
        Properties properties = timeSeries.getProperties();
        List<String> propertyNames = ContextProperties.getSummaryStatsPropertyNames(properties);
        for (String p : propertyNames) {
            builder.append("\n  <summaryStat name=\"").append(ContextProperties.parseStatisticName(p)).append("\" value=\"").append(properties.getProperty(p)).append("\"/>");
        }
    }

    private void appendTimeSeriesItems(IdentifiableTimeSeries timeSeries, StringBuilder builder, long lastTimestamp) {
        Collection<TimeSeriesItem> seriesItems = timeSeries.getSnapshot();
        Date date = new Date();
        for ( TimeSeriesItem h : seriesItems) {
            if ( h.getTimestamp() > lastTimestamp) {
                builder.append("\n<").append(ElementName.seriesItem).append(" ");
                builder.append(AttributeName.timestamp).append("=\"").append(h.getTimestamp()).append("\" ");
                date.setTime(h.getTimestamp());
                builder.append("datetime=\"").append(simpleDateFormat.format(date)).append("\" ");
                builder.append(AttributeName.value).append("=\"").append(decinalFormat.format(h.getValue().doubleValue())).append("\" ");
                builder.append("/>");
            }
        }
    }

}
