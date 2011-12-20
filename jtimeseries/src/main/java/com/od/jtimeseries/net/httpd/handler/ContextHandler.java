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
package com.od.jtimeseries.net.httpd.handler;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.net.httpd.NanoHTTPD;
import com.od.jtimeseries.net.httpd.response.NanoHttpResponse;
import com.od.jtimeseries.net.httpd.response.NoCacheResponse;
import com.od.jtimeseries.net.httpd.xml.AttributeName;
import com.od.jtimeseries.net.httpd.xml.ElementName;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 02-Jan-2009
 * Time: 10:54:10
 */
public class ContextHandler extends AbstractHandler {

    public final static String CONTEXT_XSL_RESOURCE = System.getProperty("JTimeSeriesContextXslResource", "context.xsl");

    public ContextHandler(TimeSeriesContext rootContext) {
        super(rootContext);
    }

    public NanoHttpResponse createResponse(String uri, String method, Properties header, Properties parms) {
        NanoHttpResponse result;
        TimeSeriesContext context = findContextForRequest(uri);
        if ( context == null) {
            result = createNotFoundResponse(uri);
        } else {
            return new ContextResponse(context);
        }
        return result;
    }

    
    private class ContextResponse extends NoCacheResponse {

        private TimeSeriesContext context;

        public ContextResponse(TimeSeriesContext context) {
            super(NanoHTTPD.HTTP_OK, "text/xml");
            this.context = context;
        }
        
        public void writeResponseBody(OutputStream out, PrintWriter pw) {
            writeContextXml(pw, context);
        }
    }
    
    
    private void writeContextXml(PrintWriter pw, TimeSeriesContext contextForRequest) {
        pw.write("<?xml version=\"1.0\"?>");
        pw.write("\n<?xml-stylesheet type=\"text/xsl\" href=\"/");
        pw.write(CONTEXT_XSL_RESOURCE);
        pw.write("\"?>");
        pw.write("\n<timeSeriesContext>");
        pw.write("\n<contextTree>");
        createContextTree(pw, getRootContext(), contextForRequest);
        pw.write("\n</contextTree>");

        pw.write("<selectedContext ");
        pw.write("id=\"");
        pw.write(encodeXml(contextForRequest.getId()));
        pw.write("\" ");
        pw.write("description=\"");
        pw.write(encodeXml(contextForRequest.getDescription()));
        pw.write("\" >");

        String urlForCurrentContext = createUrlForIdentifiable(contextForRequest);
        List<IdentifiableTimeSeries> series = contextForRequest.getTimeSeries();
        pw.write("\n<timeSeries>");
        for (IdentifiableTimeSeries s : series) {
            appendSeries(pw, urlForCurrentContext, s);
        }

        pw.write("\n</timeSeries>");
        pw.write("</selectedContext>");
        pw.write("\n</timeSeriesContext>");
    }

    private void createContextTree(PrintWriter pw, TimeSeriesContext context, TimeSeriesContext contextForRequest) {
        appendContextNode(pw, context, contextForRequest);
        List<TimeSeriesContext> childContexts = context.getChildContexts();
        if ( childContexts.size() > 0 ) {
            for ( TimeSeriesContext b : childContexts) {
                createContextTree(pw, b, contextForRequest);
            }
        }
        pw.write("</");
        pw.write(ElementName.context.toString());
        pw.write(">");
    }

    private void appendContextNode(PrintWriter pw, TimeSeriesContext c, TimeSeriesContext contextForRequest) {
        pw.write("\n<");
        pw.write(ElementName.context.toString());
        pw.write(" ");
        pw.write(AttributeName.contextUrl.toString());
        pw.write("=\"");
        pw.write(createUrlForIdentifiable(c));
        pw.write("\"");
        pw.write(" ");
        pw.write(AttributeName.id.toString());
        pw.write("=\"");
        pw.write(encodeXml(c.getId()));
        pw.write("\"");
        pw.write(" ");
        pw.write(AttributeName.contextPath.toString());
        pw.write("=\"");
        pw.write(encodeXml(c.getPath()));
        pw.write("\"");
        pw.write(" ");
        pw.write(AttributeName.description.toString());
        pw.write("=\"");
        pw.write(encodeXml(c.getDescription()));
        pw.write("\"");
        if ( c == contextForRequest) {
            pw.write(" ");
            pw.write(AttributeName.selected.toString());
            pw.write("=\"true\"");
        }
        pw.write(">");
    }


}
