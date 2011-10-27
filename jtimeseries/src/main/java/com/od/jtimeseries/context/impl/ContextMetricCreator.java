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
package com.od.jtimeseries.context.impl;

import com.od.jtimeseries.capture.function.CaptureFunction;
import com.od.jtimeseries.identifiable.Identifiable;
import com.od.jtimeseries.source.ValueSource;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 28-Nov-2009
 * Time: 21:06:54
 *
 */
public interface ContextMetricCreator {

    //the user passes in just one id, which will be used for the timeseries, but the value source and
    //capture also require ids. We create these by adding a suffix to the timeseries id supplied.
    public static final String SOURCE_SUFFIX = "_Source";
    public static final String CAPTURE_SUFFIX = "_Capture";

    ValueSource createSourceCaptureAndSeries(Identifiable defaultTimeSeriesContext, String pathForChild, String id, String description, Class classType, List<CaptureFunction> functions, Object[] parameters);
}
