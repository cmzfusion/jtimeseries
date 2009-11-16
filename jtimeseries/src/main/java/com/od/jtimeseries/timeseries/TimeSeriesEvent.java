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

import java.util.Collections;
import java.util.List;

/**
 * An event representing a change to a TimeSeries
 */
public class TimeSeriesEvent {

    private EventType eventType;
    private int startIndex;
    private int endIndex;
    private final List<TimeSeriesItem> items;
    private final Object source;

    /**
     * @param source        - time series source for event
     * @param startIndex    - first index affected
     * @param endIndex      - last index affected, inclusive
     * @param items         - list of items affected
     */
    private TimeSeriesEvent(Object source, int startIndex, int endIndex, List<TimeSeriesItem> items, EventType eventType) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.items = items;
        this.source = source;
        this.eventType = eventType;
    }

    /**
     * @return items affected
     */
    public List<TimeSeriesItem> getItems() {
        return items;
    }

    /**
     * For added events this is the first index of the inserted range, after the insert took place
     * For remove or item changed this is the first index affected before applying the changes
     *
     * @return first index affected
     */
    public int getStartIndex() {
        return startIndex;
    }

    /**
     * For added events this is the last index of the inserted range, after the insert took place
     * For remove or item changed this is the last index affected before applying the changes
     * 
     * @return last index affected inclusive
     */
    public int getEndIndex() {
        return endIndex;
    }

    /**
     * @return time series source of the event
     */
    public Object getSource() {
        return source;
    }

    /**
     * @return EventType for timeseries event
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * A range of items was added/inserted into the series
     *
     * @param source of event
     * @param startIndex new index of first item which was added
     * @param endIndex new index of last item which was added, inclusive
     * @param items - items added
     */
    public static TimeSeriesEvent createItemsAddedEvent(Object source, int startIndex, int endIndex, List<TimeSeriesItem> items) {
        return new TimeSeriesEvent(source, startIndex, endIndex, Collections.unmodifiableList(items), EventType.INSERT);
    }

    /**
     * A range of items in the series were removed
     *
     * @param source of event
     * @param startIndex previous index of first item which was removed
     * @param endIndex previous index of last item which was removed, inclusive
     * @param items - items removed
     */
    public static TimeSeriesEvent createItemsRemovedEvent(Object source, int startIndex, int endIndex, List<TimeSeriesItem> items) {
        return new TimeSeriesEvent(source, startIndex, endIndex, Collections.unmodifiableList(items), EventType.REMOVE);
    }

    /**
     * A range of items in the series had values changed
     * The replacement items in event list may have different timestamp or numeric value but the items must be one to one
     * replacements for the current items in the affected range - this event cannot be used to add or remove items from the series
     *
     * @param source of event
     * @param startIndex previous index of first item which changed
     * @param endIndex previous index of last item which changed, inclusive
     * @param items - replacement items for indexes from startIndex to endIndex
     */
    public static TimeSeriesEvent createItemsChangedEvent(Object source, int startIndex, int endIndex, List<TimeSeriesItem> items) {
        return new TimeSeriesEvent(source, startIndex, endIndex, Collections.unmodifiableList(items), EventType.ITEM_CHANGE);
    }

    /**
     * The time series changed in a way which could not be efficiently
     * represented using the other event types
     *
     * @param source of event
     * @param items - new items in the series
     */
    public static TimeSeriesEvent createSeriesChangedEvent(Object source, List<TimeSeriesItem> items) {
        return new TimeSeriesEvent(source, 0, items.size() - 1, Collections.unmodifiableList(items), EventType.SERIES_CHANGE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeSeriesEvent that = (TimeSeriesEvent) o;

        if (endIndex != that.endIndex) return false;
        if (startIndex != that.startIndex) return false;
        if (eventType != that.eventType) return false;
        if (items != null ? !items.equals(that.items) : that.items != null) return false;
        if (source != null ? !source.equals(that.source) : that.source != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = eventType != null ? eventType.hashCode() : 0;
        result = 31 * result + startIndex;
        result = 31 * result + endIndex;
        result = 31 * result + (items != null ? items.hashCode() : 0);
        result = 31 * result + (source != null ? source.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "TimeSeriesEvent{" + eventType +
                " startIndex=" + startIndex +
                ", endIndex=" + endIndex +
                (items.size() < 10 ? ", items " + items : ", first 10 items=" + items.subList(0, 10)) +
                ", source=" + source +
                '}';
    }

    public static enum EventType {
        INSERT,
        REMOVE,
        ITEM_CHANGE,
        SERIES_CHANGE
    }
}
