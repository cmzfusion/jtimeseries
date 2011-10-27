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
package com.od.jtimeseries.capture;

import com.od.jtimeseries.capture.function.CaptureFunction;
import com.od.jtimeseries.identifiable.IdentifiableFactory;
import com.od.jtimeseries.source.ValueSource;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.identifiable.Identifiable;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 07-Feb-2009
 * Time: 10:03:02
 * To change this template use File | Settings | File Templates.
 */
public interface CaptureFactory extends IdentifiableFactory {

     /**
     * All CaptureFactory should use this ID, to make sure only one CaptureFactory can exist per context
     */
    public static final String ID = "CaptureFactory";

    Capture createCapture(Identifiable parent, String pathForChild, String id, ValueSource valueSource, IdentifiableTimeSeries identifiableTimeSeries, CaptureFunction captureFunction, Class classType, Object[] parameters);
}
