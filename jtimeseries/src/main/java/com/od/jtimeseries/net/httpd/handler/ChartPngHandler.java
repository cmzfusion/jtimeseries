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

import com.od.jtimeseries.chart.MovingWindowXYDataset;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.net.httpd.NanoHTTPD;
import com.od.jtimeseries.net.httpd.response.InputStreamResponse;
import com.od.jtimeseries.net.httpd.response.NanoHttpResponse;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 02-Jan-2009
 * Time: 11:55:55
 */
public class ChartPngHandler extends AbstractHandler {
    public static final String CHART_PNG_POSTFIX = "-chart.png";
    private final int MAX_VERTICAL_RESOLUTION = 1600;
    private final int MAX_HORIZONTAL_RESOLUTION = 2560;


    public ChartPngHandler(TimeSeriesContext timeSeriesContext) {
        super(timeSeriesContext);
    }

    public NanoHttpResponse createResponse(String uri, String method, Properties header, Properties params) {
        NanoHttpResponse result;

        TimeSeriesContext requestContext = findContextForRequest(uri);
        if ( requestContext == null) {
            result = createNotFoundResponse(uri);
        } else {
            result = createChartResponse(uri, params, requestContext);
        }
        return result;
    }

    private NanoHttpResponse createChartResponse(String uri, Properties params, TimeSeriesContext requestContext) {
        NanoHttpResponse result;
        String idToken = getLastUriToken(uri);
        idToken = idToken.substring(0, idToken.length() - CHART_PNG_POSTFIX.length());

        IdentifiableTimeSeries h = requestContext.getTimeSeries(idToken);
        if ( h == null) {
            result = createNotFoundResponse(uri);
        } else {
            result = createImageResponse(params, h);
        }
        return result;
    }

    private NanoHttpResponse createImageResponse(Properties params, IdentifiableTimeSeries h) {
        NanoHttpResponse result;

        MovingWindowXYDataset<IdentifiableTimeSeries> xyDataset = new MovingWindowXYDataset<IdentifiableTimeSeries>();
        xyDataset.addTimeSeries(h.getId(), h);

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                h.getId(),
                "Time",
                "Value",
                xyDataset,
                true,
                false,
                false
        );

        int width = Math.min(getIntegerParameter(params, "width", 500), MAX_HORIZONTAL_RESOLUTION);
        int height = Math.min(getIntegerParameter(params, "height", 300), MAX_VERTICAL_RESOLUTION);

        BufferedImage bi = chart.createBufferedImage(width,height);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bi, "png", bos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        result = new InputStreamResponse(
                NanoHTTPD.HTTP_OK,
                "image/png",
                new ByteArrayInputStream(bos.toByteArray())
        );
        return result;
    }

    private int getIntegerParameter(Properties params, String paramName, int defaultValue) {
        int integerResult = defaultValue;
        String widthParam = params.getProperty(paramName);
        if ( widthParam != null ) {
            integerResult = Integer.valueOf(widthParam);
        }
        return integerResult;
    }
}
