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
package com.od.jtimeseries.capture.impl;

import com.od.jtimeseries.capture.Capture;
import com.od.jtimeseries.capture.CaptureFactory;
import com.od.jtimeseries.capture.TimedCapture;
import com.od.jtimeseries.capture.function.CaptureFunction;
import com.od.jtimeseries.source.ValueSource;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.util.identifiable.IdentifiableBase;
import com.od.jtimeseries.util.identifiable.Identifiable;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 07-Feb-2009
 * Time: 10:05:43
 * To change this template use File | Settings | File Templates.
 */
public class DefaultCaptureFactory extends IdentifiableBase implements CaptureFactory {

    public DefaultCaptureFactory() {
        this("DefaultCaptureFactory", "DefaultCaptureFactory");
    }

    public DefaultCaptureFactory(String id, String description) {
        super(id, description);
    }

    public Capture createCapture(Identifiable parent, String path, String id, ValueSource source, IdentifiableTimeSeries timeSeries) {
        return new DefaultCapture(id, source, timeSeries);
    }

    public TimedCapture createTimedCapture(Identifiable parent, String path, String id, ValueSource source, IdentifiableTimeSeries timeSeries, CaptureFunction captureFunction) {
        return new DefaultTimedCapture(id, source, timeSeries, captureFunction);
    }
}
