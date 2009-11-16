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
package com.od.jtimeseries.timeseries.impl;

import com.od.jtimeseries.timeseries.TimeSeriesItem;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 02-Mar-2009
 * Time: 18:24:49
 *
 * A wrapper around a RandomAccessDeque which prevents items from being inserted in the wrong
 * order by timestamp
 */
public class OrderValidatingRandomAccessDeque implements List<TimeSeriesItem> {

    private RandomAccessDeque<TimeSeriesItem> wrappedDeque;

    public OrderValidatingRandomAccessDeque(RandomAccessDeque<TimeSeriesItem> wrappedDeque) {
        this.wrappedDeque = wrappedDeque;
    }

    public void addFirst(TimeSeriesItem timeSeriesItem) {
        checkBeforePrepend(timeSeriesItem);
        wrappedDeque.addFirst(timeSeriesItem);
    }

    public void addLast(TimeSeriesItem timeSeriesItem) {
        checkBeforeAppend(timeSeriesItem);
        wrappedDeque.addLast(timeSeriesItem);
    }

    public boolean offerFirst(TimeSeriesItem timeSeriesItem) {
        checkBeforePrepend(timeSeriesItem);
        return wrappedDeque.offerFirst(timeSeriesItem);
    }

    public boolean offerLast(TimeSeriesItem timeSeriesItem) {
        checkBeforeAppend(timeSeriesItem);
        return wrappedDeque.offerLast(timeSeriesItem);
    }

    public TimeSeriesItem removeFirst() {
        return wrappedDeque.removeFirst();
    }

    public TimeSeriesItem removeLast() {
        return wrappedDeque.removeLast();
    }

    public TimeSeriesItem pollFirst() {
        return wrappedDeque.pollFirst();
    }

    public TimeSeriesItem pollLast() {
        return wrappedDeque.pollLast();
    }

    public TimeSeriesItem getFirst() {
        return wrappedDeque.getFirst();
    }

    public TimeSeriesItem getLast() {
        return wrappedDeque.getLast();
    }

    public TimeSeriesItem peekFirst() {
        return wrappedDeque.peekFirst();
    }

    public TimeSeriesItem peekLast() {
        return wrappedDeque.peekLast();
    }

    public boolean removeFirstOccurrence(Object o) {
        return wrappedDeque.removeFirstOccurrence(o);
    }

    public boolean removeLastOccurrence(Object o) {
        return wrappedDeque.removeLastOccurrence(o);
    }

    public boolean add(TimeSeriesItem timeSeriesItem) {
        checkBeforeAppend(timeSeriesItem);
        return wrappedDeque.add(timeSeriesItem);
    }

    public boolean offer(TimeSeriesItem timeSeriesItem) {
        checkBeforeAppend(timeSeriesItem);
        return wrappedDeque.offer(timeSeriesItem);
    }

    public TimeSeriesItem remove() {
        return wrappedDeque.remove();
    }

    public TimeSeriesItem poll() {
        return wrappedDeque.poll();
    }

    public TimeSeriesItem element() {
        return wrappedDeque.element();
    }

    public TimeSeriesItem peek() {
        return wrappedDeque.peek();
    }

    public void push(TimeSeriesItem timeSeriesItem) {
        checkBeforePrepend(timeSeriesItem);
        wrappedDeque.push(timeSeriesItem);
    }

    public TimeSeriesItem pop() {
        return wrappedDeque.pop();
    }

    public int size() {
        return wrappedDeque.size();
    }

    public boolean isEmpty() {
        return wrappedDeque.isEmpty();
    }

    public Iterator<TimeSeriesItem> iterator() {
        return wrappedDeque.iterator();
    }

    public Iterator<TimeSeriesItem> descendingIterator() {
        return wrappedDeque.descendingIterator();
    }

    public boolean contains(Object o) {
        return wrappedDeque.contains(o);
    }

    public boolean remove(Object o) {
        return wrappedDeque.remove(o);
    }

    public void clear() {
        wrappedDeque.clear();
    }

    public Object[] toArray() {
        return wrappedDeque.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return wrappedDeque.toArray(a);
    }

    public TimeSeriesItem get(int index) {
        return wrappedDeque.get(index);
    }

    public TimeSeriesItem set(int index, TimeSeriesItem element) {
        checkItemAtIndexHasLowerOrEqualTimestamp(index - 1, element.getTimestamp(), index);
        checkItemAtIndexHasGreaterOrEqualTimestamp(index + 1, element.getTimestamp(), index);
        return wrappedDeque.set(index, element);
    }

    public void add(int index, TimeSeriesItem element) {
        checkItemAtIndexHasLowerOrEqualTimestamp(index - 1, element.getTimestamp(), index);
        checkItemAtIndexHasGreaterOrEqualTimestamp(index, element.getTimestamp(), index);
        wrappedDeque.add(index, element);
    }

    public TimeSeriesItem remove(int index) {
        return wrappedDeque.remove(index);
    }

    public int indexOf(Object o) {
        return wrappedDeque.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return wrappedDeque.lastIndexOf(o);
    }

    public boolean addAll(int index, Collection<? extends TimeSeriesItem> c) {
        if ( c.size() > 0) {
            checkOrderingForInsertCollection(index, c);
        }
        return wrappedDeque.addAll(index, c);
    }

    private void checkOrderingForInsertCollection(int index, Collection<? extends TimeSeriesItem> c) {
        if ( c.size() > 0) {
            long lastStamp = -1, currentStamp = -1, firstTimestamp = -1;
            firstTimestamp = getFirstTimestamp(c);
            for ( TimeSeriesItem i : c) {
                currentStamp = i.getTimestamp();
                if ( currentStamp < lastStamp ) {
                    throw new TimeSeriesOrderingException(index, currentStamp);
                }
                lastStamp = currentStamp;
            }
            checkItemAtIndexHasLowerOrEqualTimestamp(index - 1, firstTimestamp, index);
            checkItemAtIndexHasGreaterOrEqualTimestamp(index, lastStamp, index);
        }
    }

    private long getFirstTimestamp(Collection<? extends TimeSeriesItem> c) {
        for ( TimeSeriesItem i  : c) {
            return i.getTimestamp();
        }
        return -1;
    }

    public ListIterator<TimeSeriesItem> listIterator() {
        return wrappedDeque.listIterator();
    }

    public ListIterator<TimeSeriesItem> listIterator(int index) {
        return wrappedDeque.listIterator(index);
    }

    public int getModCount() {
        return wrappedDeque.getModCount();
    }

    public List<TimeSeriesItem> subList(int fromIndex, int toIndex) {
        return wrappedDeque.subList(fromIndex, toIndex);
    }

    public boolean equals(Object o) {
        return wrappedDeque.equals(o);
    }

    public int hashCode() {
        return wrappedDeque.hashCode();
    }

    public boolean containsAll(Collection<?> c) {
        return wrappedDeque.containsAll(c);
    }

    public boolean addAll(Collection<? extends TimeSeriesItem> c) {
        checkOrderingForInsertCollection(wrappedDeque.size(), c);
        return wrappedDeque.addAll(c);
    }

    public boolean removeAll(Collection<?> c) {
        return wrappedDeque.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return wrappedDeque.retainAll(c);
    }

    public String toString() {
        return wrappedDeque.toString();
    }

    private void checkItemAtIndexHasGreaterOrEqualTimestamp(int index, long timestamp, int indexAffected) {
        if ( index >= 0 && index < wrappedDeque.size() ) {
            if (! (get(index).getTimestamp() >= timestamp)) {
                throw new TimeSeriesOrderingException(indexAffected, timestamp);
            }
        }
    }

    private void checkItemAtIndexHasLowerOrEqualTimestamp(int index, long timestamp, int indexAffected) {
        if ( index >= 0 && index < wrappedDeque.size() ) {
            if (! (get(index).getTimestamp() <= timestamp)) {
                throw new TimeSeriesOrderingException(indexAffected, timestamp);
            }
        }
    }

    private void checkBeforeAppend(TimeSeriesItem timeSeriesItem) {
        if ( wrappedDeque.size() > 0 && timeSeriesItem.getTimestamp() < wrappedDeque.getLast().getTimestamp() ) {
            throw new TimeSeriesOrderingException(wrappedDeque.size(), timeSeriesItem.getTimestamp());
        }
    }

    private void checkBeforePrepend(TimeSeriesItem timeSeriesItem) {
        if ( wrappedDeque.size() > 0 && (timeSeriesItem.getTimestamp() > wrappedDeque.getFirst().getTimestamp()) ) {
            throw new TimeSeriesOrderingException(0, timeSeriesItem.getTimestamp());
        }
    }
}
