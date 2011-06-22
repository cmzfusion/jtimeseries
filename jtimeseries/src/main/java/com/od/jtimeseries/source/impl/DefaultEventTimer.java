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
package com.od.jtimeseries.source.impl;

import com.od.jtimeseries.source.EventTimer;
import com.od.jtimeseries.source.ValueSourceListener;

/**
 * A source which can be used to time events
 *
 * To time an event call startEventTimer() and then stopEventTimer()
 */
public class DefaultEventTimer extends AbstractValueSource implements EventTimer {

    private long startTime;
    private final Object internalLock = new Object();

    public DefaultEventTimer(String id, String description, ValueSourceListener... sourceDataListeners) {
        super(id, description, sourceDataListeners);
    }

    public void startEventTimer() {
        synchronized (internalLock) {
            startTime = System.currentTimeMillis();
        }
    }

    public void stopEventTimer() {
        synchronized (internalLock) {
            newSourceValue(System.currentTimeMillis() - startTime);
        }
    }

}
