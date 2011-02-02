package com.od.jtimeseries.timeseries;

import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 14/01/11
 * Time: 06:48
 *
 * A TimeSeriesEvent which also includes list index for changed items
 */
public class ListTimeSeriesEvent extends TimeSeriesEvent {

    private int startIndex;
    private int endIndex;

    /**
     * @param source     - time series source for event
     * @param startIndex - first index affected
     * @param endIndex   - last index affected, inclusive
     * @param items      - list of items affected
     */
    private ListTimeSeriesEvent(Object source, int startIndex, int endIndex, List<TimeSeriesItem> items, EventType eventType, long seriesModCount) {
        super(source, items, eventType, seriesModCount);
        this.startIndex = startIndex;
        this.endIndex = endIndex;
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

    public Object clone() {
        return new ListTimeSeriesEvent(
            getSource(),
            startIndex,
            endIndex,
            getItems(),
            getEventType(),
            getSeriesModCount()
        );
    }

   /**
     * A range of items was added/inserted into the series
     *
     * @param source of event
     * @param startIndex new index of first item which was added
     * @param endIndex new index of last item which was added, inclusive
     * @param items - items added
     */
    public static ListTimeSeriesEvent createItemsAddedOrInsertedEvent(Object source, int startIndex, int endIndex, List<TimeSeriesItem> items, long seriesModCount) {
        return new ListTimeSeriesEvent(source, startIndex, endIndex, Collections.unmodifiableList(items), EventType.ADD_OR_INSERT, seriesModCount);
    }

    /**
     * A range of items in the series were removed
     *
     * @param source of event
     * @param startIndex previous index of first item which was removed
     * @param endIndex previous index of last item which was removed, inclusive
     * @param items - items removed
     */
    public static ListTimeSeriesEvent createItemsRemovedEvent(Object source, int startIndex, int endIndex, List<TimeSeriesItem> items, long seriesModCount) {
        return new ListTimeSeriesEvent(source, startIndex, endIndex, Collections.unmodifiableList(items), EventType.REMOVE, seriesModCount);
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
    public static ListTimeSeriesEvent createItemsChangedEvent(Object source, int startIndex, int endIndex, List<TimeSeriesItem> items, long seriesModCount) {
        return new ListTimeSeriesEvent(source, startIndex, endIndex, Collections.unmodifiableList(items), EventType.ITEM_CHANGE, seriesModCount);
    }

    /**
     * The time series changed in a way which could not be efficiently
     * represented using the other event types
     *
     * @param source of event
     * @param items - new items in the series
     */
    public static ListTimeSeriesEvent createSeriesChangedEvent(Object source, List<TimeSeriesItem> items, long seriesModCount) {
        return new ListTimeSeriesEvent(source, 0, items.size() - 1, Collections.unmodifiableList(items), EventType.SERIES_CHANGE, seriesModCount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ListTimeSeriesEvent that = (ListTimeSeriesEvent) o;

        if (endIndex != that.endIndex) return false;
        if (startIndex != that.startIndex) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + startIndex;
        result = 31 * result + endIndex;
        return result;
    }

    @Override
    public String toString() {
        return "TimeSeriesEvent{" + getEventType() +
                (getItems().size() < 10 ? ", items " + getItems() : ", first 10 items=" + getItems().subList(0, 10)) +
                ", startIndex=" + startIndex + ", endIndex=" + endIndex + ", modCount=" + getSeriesModCount() +
+
                '}';
    }
}
