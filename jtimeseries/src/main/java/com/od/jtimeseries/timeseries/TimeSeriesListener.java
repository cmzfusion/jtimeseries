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
package com.od.jtimeseries.timeseries;

public interface TimeSeriesListener {

    /**
     * A range of items were added/inserted to the series
     */
    void itemsAdded(TimeSeriesEvent e);

    /**
     * A range of items in the series were removed
     */
    void itemsRemoved(TimeSeriesEvent e);

    /**
     * A range of items in the series were replaced or had values changed
     * The replacement items in event list may have different timestamp or numeric value but the items must be one to one
     * replacements for the current items in the affected range - this event cannot be used to add or remove items from the series
     */
    void itemChanged(TimeSeriesEvent e);

    /**
     * The time series changed in a way which could not be efficiently
     * represented using the other event types
     */
    void seriesChanged(TimeSeriesEvent e);

}
