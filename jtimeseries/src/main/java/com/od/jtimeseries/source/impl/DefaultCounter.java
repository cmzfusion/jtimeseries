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

import com.od.jtimeseries.identifiable.FindCriteria;
import com.od.jtimeseries.identifiable.Identifiable;
import com.od.jtimeseries.identifiable.IdentifiableTreeListener;
import com.od.jtimeseries.identifiable.QueryResult;
import com.od.jtimeseries.source.Counter;
import com.od.jtimeseries.source.ValueSourceListener;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 05-Dec-2008
 * Time: 14:05:55
 */
public class DefaultCounter implements Counter {

    /**
     * Can be used as a default Counter, injecting a real Counter instance only if the metric is required
     */
    public static final Counter NULL_COUNTER = new NullCounter();

    private final DefaultValueRecorder simpleSource;
    private long currentValue;

    public DefaultCounter(String id, ValueSourceListener... sourceDataListeners) {
        this(id, id, sourceDataListeners);
    }

    public DefaultCounter(String id, String description, ValueSourceListener... sourceDataListeners) {
        simpleSource = new DefaultValueRecorder(id, description, sourceDataListeners);
    }

    public void addValueListener(ValueSourceListener sourceDataListener) {
        simpleSource.addValueListener(sourceDataListener);
    }

    public void removeValueListener(ValueSourceListener sourceDataListener) {
        simpleSource.removeValueListener(sourceDataListener);
    }

    public void incrementCount() {
        synchronized (simpleSource) {
            simpleSource.newValue(++currentValue);
        }
    }

    public void incrementCount(long n) {
        synchronized (simpleSource) {
            currentValue += n;
            simpleSource.newValue(currentValue);
        }
    }

    public void decrementCount() {
        synchronized (simpleSource) {
            simpleSource.newValue(--currentValue);
        }
    }

    public void decrementCount(long n) {
        synchronized (simpleSource) {
            currentValue -= n;
            simpleSource.newValue(currentValue);
        }
    }

    public void setCount(long n) {
        synchronized (simpleSource) {
            currentValue = n;
            simpleSource.newValue(currentValue);
        }
    }

    public void reset() {
        synchronized (simpleSource) {
            currentValue = 0;
            simpleSource.newValue(0);
        }
    }

    public long getCount() {
        synchronized (simpleSource) {
            return currentValue;
        }
    }

    public String getId() {
        return simpleSource.getId();
    }

    public String getParentPath() {
        return simpleSource.getParentPath();
    }

    public String getPath() {
        return simpleSource.getPath();
    }

    public String getDescription() {
        return simpleSource.getDescription();
    }

    public void setDescription(String description) {
        simpleSource.setDescription(description);
    }

    public Identifiable getParent() {
        return simpleSource.getParent();
    }

    public Identifiable setParent(Identifiable parent) {
        return simpleSource.setParent(parent);
    }

    public List<Identifiable> getChildren() {
        return simpleSource.getChildren();
    }

    public int getChildCount() {
        return simpleSource.getChildCount();
    }

    public <E extends Identifiable> List<E> getChildren(Class<E> classType) {
        return simpleSource.getChildren(classType);
    }

    public boolean removeChild(Identifiable c) {
        return simpleSource.removeChild(c);
    }

    public ReentrantReadWriteLock getTreeLock() {
        return simpleSource.getTreeLock();
    }

    public Identifiable getRoot() {
        return simpleSource.getRoot();
    }

    public boolean isRoot() {
        return simpleSource.isRoot();
    }

    public Identifiable get(String id) {
        return simpleSource.get(id);
    }

    public <E extends Identifiable> E get(String id, Class<E> classType) {
        return simpleSource.get(id, classType);
    }

    public <E extends Identifiable> E create(String id, String description, Class<E> clazz, Object... parameters) {
        return simpleSource.create(id, description, clazz, parameters);
    }

    public <E extends Identifiable> E getOrCreate(String path, String description, Class<E> clazz, Object... parameters) {
        return simpleSource.getOrCreate(path, description, clazz, parameters);
    }

    public boolean containsChildWithId(String id) {
        return simpleSource.containsChildWithId(id);
    }

    public boolean containsChild(Identifiable i) {
        return simpleSource.containsChild(i);
    }

    public String setProperty(String propertyName, String value) {
        return simpleSource.setProperty(propertyName, value);
    }

    public Identifiable addChild(Identifiable... identifiables) {
        return simpleSource.addChild(identifiables);
    }

    public <E extends Identifiable> E getFromAncestors(String id, Class<E> clazz) {
        return simpleSource.getFromAncestors(id, clazz);
    }

    public String findProperty(String propertyName) {
        return simpleSource.findProperty(propertyName);
    }

    public String getProperty(String propertyName) {
        return simpleSource.getProperty(propertyName);
    }

    public String removeProperty(String propertyName) {
        return simpleSource.removeProperty(propertyName);
    }

    public Properties getProperties() {
        return simpleSource.getProperties();
    }

    public void putAllProperties(Properties p) {
        simpleSource.putAllProperties(p);
    }

    public String toString() {
        return simpleSource.toString();
    }

    public Identifiable remove(String path) {
        return simpleSource.remove(path);
    }

    public <E extends Identifiable> E remove(String path, Class<E> classType) {
        return simpleSource.remove(path, classType);
    }

    public void addTreeListener(IdentifiableTreeListener l) {
        simpleSource.addTreeListener(l);
    }

    public void removeTreeListener(IdentifiableTreeListener l) {
        simpleSource.removeTreeListener(l);
    }

    public void fireNodeChanged(Object changeDescription) {
        simpleSource.fireNodeChanged("change");
    }

    public <E extends Identifiable> QueryResult<E> findAll(Class<E> assignableToClass) {
        return simpleSource.findAll(assignableToClass);
    }

    public <E extends Identifiable> QueryResult<E> findAll(String searchPattern, Class<E> assignableToClass) {
        return simpleSource.findAll(searchPattern, assignableToClass);
    }

    public <E extends Identifiable> QueryResult<E> findAll(Class<E> assignableToClass, FindCriteria<E> findCriteria) {
        return simpleSource.findAll(assignableToClass, findCriteria);
    }

    public boolean contains(String path) {
        return simpleSource.contains(path);
    }

    /**
     *  Override DefaultCounter methods for performance, to ensure we take no locks
     */
    private static class NullCounter extends DefaultCounter {
        public NullCounter() {
            super("NULL_COUNTER");
        }

        public void incrementCount() {}

        public void incrementCount(long n) {}

        public void decrementCount() {}

        public void decrementCount(long n) {}

        public void setCount(long n) {}

        public void reset() {}

        public long getCount() {
            return 0;
        }
    }
}
