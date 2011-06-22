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

import java.io.IOException;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 02-Jan-2009
 * Time: 10:44:46
 */
public class JTimeSeriesHttpd extends NanoHTTPD {

    private int port;
    private volatile HandlerFactory handlerFactory;

    /**
    * Starts a HTTP server to given port.<p>
    * Throws an IOException if the socket is already in use
    */
    public JTimeSeriesHttpd(int port, TimeSeriesContext timeSeriesContext) throws IOException {
        super(port);
        this.port = port;
        this.handlerFactory = new TimeSeriesContextHandlerFactory(timeSeriesContext);
    }

    public void setHandlerFactory(HandlerFactory handlerFactory) {
        this.handlerFactory = handlerFactory;
    }

    public int getPort() {
        return port;
    }

    public Response serve( String uri, String method, Properties header, Properties parms ) {
        HttpHandler handler = handlerFactory.getHandler(uri, method, header, parms );
        if ( handler != null) {
            return handler.createResponse(uri, method, header, parms);
        } else {
            return new TextResponse(HTTP_NOTFOUND, MIME_PLAINTEXT, "Error 404, file not found - JTimeseries cannot serve resource " + uri);
        }
    }

}
