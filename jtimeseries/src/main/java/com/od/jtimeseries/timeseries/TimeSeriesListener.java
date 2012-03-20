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
package com.od.jtimeseries.timeseries;

public interface TimeSeriesListener {

    /**
     * Items were added/inserted to the series
     *
     * Due to the reuse of event instances for performance, the event instance may only be valid for the duration of
     * the callback. Do not hold references and refer to the event subsequently - if you need to do this, take a copy
     */
    void itemsAddedOrInserted(TimeSeriesEvent e);

    /**
     * Items in the series were removed
     *
     * Due to the reuse of event instances for performance, the event instance may only be valid for the duration of
     * the callback. Do not hold references and refer to the event subsequently - if you need to do this, take a copy
     */
    void itemsRemoved(TimeSeriesEvent e);

    /**
     * The time series changed in a way which could not be efficiently
     * represented using the other event types
     *
     * Due to the reuse of event instances for performance, the event instance may only be valid for the duration of
     * the callback. Do not hold references and refer to the event subsequently - if you need to do this, take a copy     */
    void seriesChanged(TimeSeriesEvent e);

}
