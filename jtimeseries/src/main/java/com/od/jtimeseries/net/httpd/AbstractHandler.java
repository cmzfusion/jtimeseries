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
import com.od.jtimeseries.util.identifiable.Identifiable;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 02-Jan-2009
 * Time: 11:59:04
 */
public abstract class AbstractHandler implements HttpHandler {

    private TimeSeriesContext rootContext;

    public AbstractHandler(TimeSeriesContext rootContext) {
        this.rootContext = rootContext;
    }

    protected TimeSeriesContext getRootContext() {
        return rootContext;
    }

    public abstract NanoHTTPD.Response createResponse(String uri, String method, Properties header, Properties parms);

    protected TimeSeriesContext findContextForRequest(String uri) {
        String contextPath = getContextPathFromUri(uri);
        return rootContext.getContext(contextPath);
    }

    private String getContextPathFromUri(String uri) {
        //take the content up to the last forward slash to be the context path
        int endOfContextPathInUri = uri.lastIndexOf('/');

        String contextPath = uri.substring(1, Math.max(1,endOfContextPathInUri)); //strip leading /
        contextPath = contextPath.replace("/", Identifiable.NAMESPACE_SEPARATOR);
        return contextPath;
    }

    //TODO
    //this is far from complete, but it's a start - anyone know a convenient API hook to do this?
    protected String encodeXml(String s) {
        s = s.replace("<","&lt;");
        s = s.replace(">","&gt;");
        s = s.replace("&", "&amp;");
        return s;
    }

    protected String encodeUrlToken(String s) {
        try {
            //when this gets to the browser, the browser will expand the escaped chars even if it is part
            //of a URL, which requires the escaping to be valid. It seems we need to 'double' escape it,
            //so that the result of the browser unescaping is actually a valid url with escapes!
            //not 100% sure this is the best solution, but it seems to work OK
            String result = URLEncoder.encode( s, "UTF-8" );
            result = result.replaceAll("%", "%25");
            return result;
        } catch (UnsupportedEncodingException uee ) {
            throw new InternalError("Should always be able to encode UTF-8");
        }
    }

    protected String decodeUrl(String s) {
        try {
            return URLDecoder.decode( s, "UTF-8" );
        } catch (UnsupportedEncodingException uee ) {
            throw new InternalError("Should always be able to decode UTF-8");
        }
    }

    protected String getLastUriToken(String uri) {
        int lastSlashIndex = uri.lastIndexOf('/');
        return decodeUrl(uri.substring(lastSlashIndex + 1));
    }

    protected NanoHTTPD.Response createNotFoundResponse(String uri) {
        NanoHTTPD.Response result;
        result = new NanoHTTPD.TextResponse(NanoHTTPD.HTTP_NOTFOUND, NanoHTTPD.MIME_PLAINTEXT, "Could not find resource " + uri);
        return result;
    }

    protected String createUrlForIdentifiable(Identifiable c) {
        return createUrlForIdentifiable(c, new StringBuilder());
    }

    protected String createUrlForIdentifiable(Identifiable c, StringBuilder sb) {
        if ( sb.length() == 0) {
            sb.append("/");
        }
        if ( c != getRootContext()) {
            sb.insert(0,"/" + encodeUrlToken(c.getId()));
            return createUrlForIdentifiable(c.getParent(), sb);
        } else {
            return sb.toString();
        }
    }

    protected void appendSeries(PrintWriter pw, String parentContextUrl, IdentifiableTimeSeries s) {
        pw.write("\n<");
        pw.write(ElementName.series.toString());
        pw.write(" ");
        pw.write(AttributeName.parentPath.toString());
        pw.write("=\"");
        pw.write(encodeXml(s.getParentPath()));
        pw.write("\"");
        pw.write(" ");
        pw.write(AttributeName.id.toString());
        pw.write("=\"");
        pw.write(encodeXml(s.getId()));
        pw.write("\"");
        pw.write(" ");
        pw.write(AttributeName.description.toString());
        pw.write("=\"");
        pw.write(encodeXml(s.getDescription()));
        pw.write("\"");
        pw.write(" ");
        pw.write(AttributeName.seriesUrl.toString());
        pw.write("=\"");
        pw.write(parentContextUrl);
        pw.write(encodeUrlToken(s.getId()));
        pw.write(SeriesHandler.SERIES_POSTFIX);
        pw.write("\"");
        pw.write(" ");
        pw.write(AttributeName.chartImage.toString());
        pw.write("=\"");
        pw.write(encodeUrlToken(s.getId()));
        pw.write(ChartPngHandler.CHART_PNG_POSTFIX);
        pw.write("\"");
        pw.write(" ");
        pw.write(AttributeName.summaryStats.toString());
        pw.write("=\"");
        pw.write(encodeXml(ContextProperties.getSummaryStatsStringRepresentation(s.getProperties())));
        pw.write("\"");
        pw.write("/>");
    }

}
