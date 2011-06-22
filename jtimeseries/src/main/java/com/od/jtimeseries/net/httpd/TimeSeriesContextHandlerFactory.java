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

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 02-Jan-2009
 * Time: 10:51:18
 */
public class TimeSeriesContextHandlerFactory implements HandlerFactory {

    private TimeSeriesContext rootContext;

    public TimeSeriesContextHandlerFactory(TimeSeriesContext rootContext) {
        //To change body of created methods use File | Settings | File Templates.
        this.rootContext = rootContext;
    }

    public HttpHandler getHandler(String uri, String method, Properties header, Properties params) {
        if ( uri.toLowerCase().endsWith(ChartPngHandler.CHART_PNG_POSTFIX) ) {
           return new ChartPngHandler(rootContext);
        } else if ( uri.endsWith("/")) {
           return new ContextHandler(rootContext);
        } else if (isValidXslUri(uri)) {
            return new ClassPathResourceResponseHandler(rootContext, "application/xml");
        } else if ( uri.endsWith(SeriesHandler.SERIES_POSTFIX)){
            return new SeriesHandler(rootContext);
        } else if ( uri.endsWith((TimeSeriesIndexHandler.INDEX_POSTFIX))) {
            return new TimeSeriesIndexHandler(rootContext);                
        } else {
           return null;
        }
    }

    private boolean isValidXslUri(String uri) {
        return uri.endsWith("xsl");
    }
}
