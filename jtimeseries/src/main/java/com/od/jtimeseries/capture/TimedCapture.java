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
 * User: nick
 * Date: 04-Feb-2009
 * Time: 22:36:11
 * To change this template use File | Settings | File Templates.
 */
public interface TimedCapture extends Capture {

    long getCapturePeriodInMilliseconds();

    /**
     * Called by the scheduler when the Capture is in STARTING or STARTED state
     */
    void triggerCapture(long timestamp);
}
