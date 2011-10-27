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
package com.od.jtimeseries.identifiable;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 02-Jun-2010
 * Time: 08:15:24
 *
 * The locking scheme for identifiable tree structure requires a lot of boiler plate code
 * This class localises all of that - so that subclasses can handle the real logic
 */
public abstract class LockingIdentifiable implements Identifiable {

    /**
     * A lock for the context tree structure which should be held while changing or traversing the tree structure, to ensure integrity
     */
    private static final ReentrantReadWriteLock CONTEXT_LOCK = new ReentrantReadWriteLock();

    public ReentrantReadWriteLock getTreeLock() {
        return CONTEXT_LOCK;
    }

    public final String getParentPath() {
        try {
            getTreeLock().readLock().lock();
            return getParentPath_Locked();
        } finally {
            getTreeLock().readLock().unlock();
        }
    }
    protected abstract String getParentPath_Locked();
    
    public final String getPath() {
        try {
            getTreeLock().readLock().lock();
            return getPath_Locked();
        } finally {
            getTreeLock().readLock().unlock();
        }
    }
    protected abstract String getPath_Locked();

    public final Identifiable setParent(Identifiable parent) {
        try {
            getTreeLock().writeLock().lock();
            return setParent_Locked(parent);
        } finally {
            getTreeLock().writeLock().unlock();
        }
    }

    protected abstract Identifiable setParent_Locked(Identifiable parent);

    public final List<Identifiable> getChildren() {
        try {
            getTreeLock().readLock().lock();
            return getChildren_Locked();
        } finally {
            getTreeLock().readLock().unlock();
        }
    }

    protected abstract List<Identifiable> getChildren_Locked();

    public final <E extends Identifiable> List<E> getChildren(Class<E> classType) {
        try {
            getTreeLock().readLock().lock();
            return getChildren_Locked(classType);
        } finally {
            getTreeLock().readLock().unlock();
        }
    }

    protected abstract <E extends Identifiable> List<E> getChildren_Locked(Class<E> classType);

    public <E extends Identifiable> E create(String path, String description, Class<E> clazz, Object... parameters) {
        try {
            getTreeLock().writeLock().lock();
            return create_Locked(path, description, clazz, parameters);
        } finally {
            getTreeLock().writeLock().unlock();
        }
    }
    protected abstract <E extends Identifiable> E create_Locked(String path, String description, Class<E> clazz, Object... parameters);

    public final boolean removeChild(Identifiable c) {
        try {
            getTreeLock().writeLock().lock();
            return removeChild_Locked(c);
        } finally {
            getTreeLock().writeLock().unlock();
        }
    }

    protected abstract boolean removeChild_Locked(Identifiable c);

    public Identifiable remove(String path) {
        return remove(path, Identifiable.class);
    }

    public <E extends Identifiable> E remove(String path, Class<E> classType) {
        try {
            getTreeLock().writeLock().lock();
            return remove_Locked(path, classType);
        } finally {
            getTreeLock().writeLock().unlock();
        }
    }
    protected abstract <E extends Identifiable> E remove_Locked(String path, Class<E> classType);

    public final boolean isRoot() {
        try {
            getTreeLock().readLock().lock();
            return isRoot_Locked();
        } finally {
            getTreeLock().readLock().unlock();
        }
    }

    protected abstract boolean isRoot_Locked();

    public final Identifiable get(String path) {
        return get(path, Identifiable.class);
    }

    public final <E extends Identifiable> E get(String path, Class<E> classType) {
        try {
            getTreeLock().readLock().lock();
            return get_Locked(path, classType);
        } finally {
            getTreeLock().readLock().unlock();
        }
    }
    protected abstract <E extends Identifiable> E get_Locked(String path, Class<E> classType);

    public boolean contains(String path) {
        Identifiable i = get(path);
        return i != null;
    }

    public <E extends Identifiable> E getFromAncestors(String id, Class<E> classType) {
        try {
            getTreeLock().readLock().lock();
            return getFromAncestors_Locked(id, classType);
        } finally {
            getTreeLock().readLock().unlock();
        }
    }
    protected abstract <E extends Identifiable> E getFromAncestors_Locked(String id, Class<E> classType);


    public final boolean containsChildWithId(String id) {
        try {
            getTreeLock().readLock().lock();
            return containsChildWithId_Locked(id);
        } finally {
            getTreeLock().readLock().unlock();
        }
    }
    protected abstract boolean containsChildWithId_Locked(String id);

    public final boolean containsChild(Identifiable i) {
        try {
            getTreeLock().readLock().lock();
            return containsChild_Locked(i);
        } finally {
            getTreeLock().readLock().unlock();
        }
    }

    protected abstract boolean containsChild_Locked(Identifiable i);

    public final String getProperty(String propertyName) {
        try {
            getTreeLock().readLock().lock();
            return getProperty_Locked(propertyName);
        } finally {
            getTreeLock().readLock().unlock();
        }
    }

    protected abstract String getProperty_Locked(String propertyName);

    public final Properties getProperties() {
        try {
            getTreeLock().readLock().lock();
            return getProperties_Locked();
        } finally {
            getTreeLock().readLock().unlock();
        }
    }

    protected abstract Properties getProperties_Locked();

    public final void putAllProperties(Properties p) {
        try {
            getTreeLock().writeLock().lock();
            putAllProperties_Locked(p);
        } finally {
            getTreeLock().writeLock().unlock();
        }
    }

    protected abstract void putAllProperties_Locked(Properties p);

    public final String findProperty(String propertyName) {
        try {
            getTreeLock().readLock().lock();
            return findProperty_Locked(propertyName);
        } finally {
            getTreeLock().readLock().unlock();
        }
    }

    protected abstract String findProperty_Locked(String propertyName);

    public final String setProperty(String propertyName, String value) {
        try {
            getTreeLock().writeLock().lock();
            return setProperty_Locked(propertyName, value);
        } finally {
            getTreeLock().writeLock().unlock();
        }
    }

    protected abstract String setProperty_Locked(String propertyName, String value);


    public final String removeProperty(String propertyName) {
        try {
            getTreeLock().writeLock().lock();
            return removeProperty_Locked(propertyName);
        } finally {
            getTreeLock().writeLock().unlock();
        }
    }

    protected abstract String removeProperty_Locked(String propertyName);

    public final Identifiable addChild(Identifiable... identifiables) {
        try {
            getTreeLock().writeLock().lock();
            return addChild_Locked(identifiables);
        } finally {
            getTreeLock().writeLock().unlock();
        }
    }

    protected abstract Identifiable addChild_Locked(Identifiable... identifiables);

    public final void addTreeListener(IdentifiableTreeListener l) {
        try {
            getTreeLock().readLock().lock();
            addTreeListener_Locked(l);
        } finally {
            getTreeLock().readLock().unlock();
        }
    }

    protected abstract void addTreeListener_Locked(IdentifiableTreeListener l);

    public final void removeTreeListener(IdentifiableTreeListener l) {
        try {
            getTreeLock().readLock().lock();
            removeTreeListener_Locked(l);
        } finally {
            getTreeLock().readLock().unlock();
        }
    }

    protected abstract void removeTreeListener_Locked(IdentifiableTreeListener l);

    public Identifiable getRoot() {
        try {
            getTreeLock().readLock().lock();
            return getRoot_Locked();
        } finally {
            getTreeLock().readLock().unlock();
        }
    }

    protected abstract Identifiable getRoot_Locked();


    public final <E extends Identifiable> QueryResult<E> findAll(Class<E> assignableToClass) {
        try {
            getTreeLock().readLock().lock();
            return findAll_Locked(assignableToClass);
        } finally {
            getTreeLock().readLock().unlock();
        }
    }

    protected abstract <E extends Identifiable> QueryResult<E> findAll_Locked(Class<E> assignableToClass);

    public final <E extends Identifiable> QueryResult<E> findAll(String searchPattern, Class<E> assignableToClass) {
        try {
            getTreeLock().readLock().lock();
            return findAll_Locked(searchPattern, assignableToClass);
        } finally {
            getTreeLock().readLock().unlock();
        }
    }

    protected abstract <E extends Identifiable> QueryResult<E> findAll_Locked(String searchPattern, Class<E> assignableToClass);
}
