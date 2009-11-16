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

    public NanoHTTPD.Response createResponse(String uri, String method, Properties header, Properties parms) {

        TimeSeriesContext context = findContextForRequest(uri);
        String xml = createContextXml(context);

        return new NanoHTTPD.NoCacheResponse(
                NanoHTTPD.HTTP_OK,
                "text/xml",
                xml
        );
    }

    private String createContextXml(TimeSeriesContext contextForRequest) {
        StringBuilder builder = new StringBuilder("<?xml version=\"1.0\"?>");
        builder.append("\n<?xml-stylesheet type=\"text/xsl\" href=\"/").append(CONTEXT_XSL_RESOURCE).append("\"?>");
        builder.append("\n<timeSeriesContext>");
        builder.append("\n<contextTree>");
        createContextTree(builder, getRootContext(), contextForRequest);
        builder.append("\n</contextTree>");

        builder.append("<selectedContext ");
        builder.append("id=\"").append(encodeXml(contextForRequest.getId())).append("\" ");
        builder.append("description=\"").append(encodeXml(contextForRequest.getDescription())).append("\" >");

        String urlForCurrentContext = createUrlForIdentifiable(contextForRequest);
        List<IdentifiableTimeSeries> series = contextForRequest.getTimeSeries();
        builder.append("\n<timeSeries>");
        for (IdentifiableTimeSeries s : series) {
            appendSeries(urlForCurrentContext, builder, s);
        }

        builder.append("\n</timeSeries>");
        builder.append("</selectedContext>");
        builder.append("\n</timeSeriesContext>");
        return builder.toString();
    }

    private void createContextTree(StringBuilder builder, TimeSeriesContext context, TimeSeriesContext contextForRequest) {
        appendContextNode(builder, context, contextForRequest);
        List<TimeSeriesContext> childContexts = context.getChildContexts();
        if ( childContexts.size() > 0 ) {
            for ( TimeSeriesContext b : childContexts) {
                createContextTree(builder, b, contextForRequest);
            }
        }
        builder.append("</").append(ElementName.context).append(">");
    }

    private void appendContextNode(StringBuilder builder, TimeSeriesContext c, TimeSeriesContext contextForRequest) {
        builder.append("\n<").append(ElementName.context);
        builder.append(" ").append(AttributeName.contextUrl).append("=\"").append(createUrlForIdentifiable(c)).append("\"");
        builder.append(" ").append(AttributeName.id).append("=\"").append(encodeXml(c.getId())).append("\"");
        builder.append(" ").append(AttributeName.contextPath).append("=\"").append(encodeXml(c.getContextPath())).append("\"");
        builder.append(" ").append(AttributeName.description).append("=\"").append(encodeXml(c.getDescription())).append("\"");
        if ( c == contextForRequest) {
            builder.append(" ").append(AttributeName.selected).append("=\"true\"");
        }
        builder.append(">");
    }

}
