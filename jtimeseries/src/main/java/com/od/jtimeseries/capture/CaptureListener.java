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
 * User: Nick Ebbutt
 * Date: 06-Feb-2009
 * Time: 11:19:35
 *
 * Some classes, e.g. schedulers, may need to observe when capture is started/stopped
 */
public interface CaptureListener {

    void captureStateChanged(Capture source, CaptureState oldState, CaptureState newState);

    /**
     * Called when a capture has been triggered by a scheduler (for timed captures only)
     */
    void captureTriggered(Capture source);

    /**
     * Called each time a value has been committed to a TimeSeries
     */
    void captureComplete(Capture source, Numeric value, TimeSeries timeseries);

}
