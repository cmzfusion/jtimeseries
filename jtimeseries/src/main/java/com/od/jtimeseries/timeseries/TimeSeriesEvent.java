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

import java.util.Collections;
import java.util.List;

/**
 * An event representing a change to a TimeSeries
 */
public class TimeSeriesEvent implements Cloneable {

    private final List<TimeSeriesItem> items;
    private final EventType eventType;
    private Object source;
    private long seriesModCount;

    /**
     * @param source        - time series source for event
     * @param items         - list of items affected in order of timestamp
     */
    protected TimeSeriesEvent(Object source, List<TimeSeriesItem> items, EventType eventType, long seriesModCount) {
        this.items = items;
        this.source = source;
        this.eventType = eventType;
        this.seriesModCount = seriesModCount;
    }

    public long getFirstItemTimestamp() {
        return items.get(0).getTimestamp();
    }

    public long getLastItemTimestamp() {
        return items.get(items.size() - 1).getTimestamp();
    }

    /**
     * @return items affected in order of timestamp
     */
    public List<TimeSeriesItem> getItems() {
        return items;
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

    public long getSeriesModCount() {
        return seriesModCount;
    }

    public void setSeriesModCount(long modCount) {
        this.seriesModCount = modCount;
    }

    public static TimeSeriesEvent createEvent(Object source, List<TimeSeriesItem> items, EventType eventType, long seriesModCount) {
        return new TimeSeriesEvent(source, Collections.unmodifiableList(items), eventType, seriesModCount);
    }

    /**
     * A range of items was added/inserted into the series
     *
     * @param source of event
     * @param items - items added
     */
    public static TimeSeriesEvent createItemsAddedOrInsertedEvent(Object source, List<TimeSeriesItem> items, long seriesModCount) {
        return new TimeSeriesEvent(source, Collections.unmodifiableList(items), EventType.ADD_OR_INSERT, seriesModCount);
    }

    /**
     * A range of items in the series were removed
     *
     * @param source of event
     * @param items - items removed
     */
    public static TimeSeriesEvent createItemsRemovedEvent(Object source, List<TimeSeriesItem> items, long seriesModCount) {
        return new TimeSeriesEvent(source, Collections.unmodifiableList(items), EventType.REMOVE, seriesModCount);
    }

    /**
     * The time series changed in a way which could not be efficiently
     * represented using the other event types
     *
     * @param source of event
     * @param items - items in the series after change
     */
    public static TimeSeriesEvent createSeriesChangedEvent(Object source, List<TimeSeriesItem> items, long seriesModCount) {
        return new TimeSeriesEvent(source, Collections.unmodifiableList(items), EventType.SERIES_CHANGE, seriesModCount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeSeriesEvent that = (TimeSeriesEvent) o;

        if (eventType != that.eventType) return false;
        if (items != null ? !items.equals(that.items) : that.items != null) return false;
        if (source != null ? !source.equals(that.source) : that.source != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = items != null ? items.hashCode() : 0;
        result = 31 * result + (eventType != null ? eventType.hashCode() : 0);
        result = 31 * result + (source != null ? source.hashCode() : 0);
        return result;
    }

    public Object clone() {
        return new TimeSeriesEvent(
            source,
            items,
            eventType,
            seriesModCount
        );
    }

    public String toString() {
        return "TimeSeriesEvent{" + eventType +
                (items.size() < 10 ? ", items " + items : ", first 10 items=" + items.subList(0, 10)) +
                ", source=" + source + ", modCount=" + seriesModCount +
                '}';
    }

    public void setSource(Object proxySource) {
        this.source = proxySource;
    }

    public static enum EventType {
        ADD_OR_INSERT,
        REMOVE,
        SERIES_CHANGE
    }
}
