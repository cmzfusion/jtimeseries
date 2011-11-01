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

import com.od.jtimeseries.timeseries.TimeSeries;
import com.od.jtimeseries.util.numeric.Numeric;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 20-Nov-2009
 * Time: 19:27:29
 * To change this template use File | Settings | File Templates.
 *
 * Subclasses can extend this if they don't want to provide an implementation for both methods
 */
public class CaptureListenerAdapter implements CaptureListener {

    public void captureStateChanged(Capture source, CaptureState oldState, CaptureState newState) {
    }

    public void captureTriggered(Capture source) {
    }

    public void captureComplete(Capture source, Numeric value, TimeSeries timeseries) {
    }

}
