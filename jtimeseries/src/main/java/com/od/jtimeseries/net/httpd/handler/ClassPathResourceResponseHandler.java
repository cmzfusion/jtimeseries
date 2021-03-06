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
import com.od.jtimeseries.net.httpd.response.InputStreamResponse;
import com.od.jtimeseries.net.httpd.response.NanoHttpResponse;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 04-Jan-2009
 * Time: 13:17:41
 * To change this template use File | Settings | File Templates.
 */
public class ClassPathResourceResponseHandler extends AbstractHandler {

    private String mimeType;

    public ClassPathResourceResponseHandler(TimeSeriesContext rootContext, String mimeType) {
        super(rootContext);
        this.mimeType = mimeType;
    }

    public NanoHttpResponse createResponse(String uri, String method, Properties header, Properties parms) {
        String resourcePath = decodeUrl(uri);

        NanoHttpResponse result;
        URL resource = ClassPathResourceResponseHandler.class.getResource(resourcePath);
        if ( resource == null) {
            result = createNotFoundResponse(uri);
        } else {
            InputStream is = ClassPathResourceResponseHandler.class.getResourceAsStream(resourcePath);
            if ( is != null) {
                result = new InputStreamResponse(NanoHTTPD.HTTP_OK, mimeType, is);
            } else {
                result = createNotFoundResponse(uri);
            }
        }

        return result;
    }


}
