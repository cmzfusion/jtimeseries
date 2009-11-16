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
package com.od.jtimeseries.capture;

/**
 * Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 24-Feb-2009
* Time: 17:26:54
*/
public enum CaptureState {

    /**
     * No values will be captured to the timeseries
     */
    STOPPED,

    /**
     * Starting but not yet started
     * TimedCaptures may enter this state before they receive their initial trigger 
     * from the CaptureScheduler
     */
    STARTING,

    /**
     * Values are being captured to the timeseries
     */
    STARTED
}
