package com.od.jtimeseries.util.identifiable;

import com.od.jtimeseries.context.TimeSeriesContext;

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

    public ReentrantReadWriteLock getContextLock() {
        return CONTEXT_LOCK;
    }

    public final String getParentPath() {
        try {
            getContextLock().readLock().lock();
            return getParentPath_Locked();
        } finally {
            getContextLock().readLock().unlock();                
        }
    }
    protected abstract String getParentPath_Locked();
    
    public final String getPath() {
        try {
            getContextLock().readLock().lock();
            return getPath_Locked();
        } finally {
            getContextLock().readLock().unlock();                
        }
    }
    protected abstract String getPath_Locked();

    public final Identifiable setParent(Identifiable parent) {
        try {
            getContextLock().writeLock().lock();
            return setParent_Locked(parent);
        } finally {
            getContextLock().writeLock().unlock();                
        }
    }

    protected abstract Identifiable setParent_Locked(Identifiable parent);

    public final List<Identifiable> getChildren() {
        try {
            getContextLock().readLock().lock();
            return getChildren_Locked();
        } finally {
            getContextLock().readLock().unlock();                
        }
    }

    protected abstract List<Identifiable> getChildren_Locked();

    public final <E extends Identifiable> List<E> getChildren(Class<E> classType) {
        try {
            getContextLock().readLock().lock();
            return getChildren_Locked(classType);
        } finally {
            getContextLock().readLock().unlock();                
        }
    }

    protected abstract <E extends Identifiable> List<E> getChildren_Locked(Class<E> classType);

    public <E extends Identifiable> E create(String path, String description, Class<E> clazz, Object... parameters) {
        try {
            getContextLock().writeLock().lock();
            return create_Locked(path, description, clazz, parameters);
        } finally {
            getContextLock().writeLock().unlock();
        }
    }
    protected abstract <E extends Identifiable> E create_Locked(String path, String description, Class<E> clazz, Object... parameters);

    public final boolean removeChild(Identifiable c) {
        try {
            getContextLock().writeLock().lock();
            return removeChild_Locked(c);
        } finally {
            getContextLock().writeLock().unlock();                
        }
    }

    protected abstract boolean removeChild_Locked(Identifiable c);

    public Identifiable remove(String path) {
        return remove(path, Identifiable.class);
    }

    public <E extends Identifiable> E remove(String path, Class<E> classType) {
        try {
            getContextLock().writeLock().lock();
            return remove_Locked(path, classType);
        } finally {
            getContextLock().writeLock().unlock();
        }
    }
    protected abstract <E extends Identifiable> E remove_Locked(String path, Class<E> classType);

    public final boolean isRoot() {
        try {
            getContextLock().readLock().lock();
            return isRoot_Locked();
        } finally {
            getContextLock().readLock().unlock();
        }
    }

    protected abstract boolean isRoot_Locked();

    public final Identifiable get(String path) {
        return get(path, Identifiable.class);
    }

    public final <E extends Identifiable> E get(String path, Class<E> classType) {
        try {
            getContextLock().readLock().lock();
            return get_Locked(path, classType);
        } finally {
            getContextLock().readLock().unlock();
        }
    }
    protected abstract <E extends Identifiable> E get_Locked(String path, Class<E> classType);


    public <E extends Identifiable> E getFromAncestors(String id, Class<E> classType) {
        try {
            getContextLock().readLock().lock();
            return getFromAncestors_Locked(id, classType);
        } finally {
            getContextLock().readLock().unlock();
        }
    }
    protected abstract <E extends Identifiable> E getFromAncestors_Locked(String id, Class<E> classType);


    public final boolean containsChildWithId(String id) {
        try {
            getContextLock().readLock().lock();
            return containsChildWithId_Locked(id);
        } finally {
            getContextLock().readLock().unlock();
        }
    }
    protected abstract boolean containsChildWithId_Locked(String id);

    public final boolean containsChild(Identifiable i) {
        try {
            getContextLock().readLock().lock();
            return containsChild_Locked(i);
        } finally {
            getContextLock().readLock().unlock();
        }
    }

    protected abstract boolean containsChild_Locked(Identifiable i);

    public final String getProperty(String propertyName) {
        try {
            getContextLock().readLock().lock();
            return getProperty_Locked(propertyName);
        } finally {
            getContextLock().readLock().unlock();
        }
    }

    protected abstract String getProperty_Locked(String propertyName);

    public final Properties getProperties() {
        try {
            getContextLock().readLock().lock();
            return getProperties_Locked();
        } finally {
            getContextLock().readLock().unlock();
        }
    }

    protected abstract Properties getProperties_Locked();

    public final void putAllProperties(Properties p) {
        try {
            getContextLock().writeLock().lock();
            putAllProperties_Locked(p);
        } finally {
            getContextLock().writeLock().unlock();
        }
    }

    protected abstract void putAllProperties_Locked(Properties p);

    public final String findProperty(String propertyName) {
        try {
            getContextLock().readLock().lock();
            return findProperty_Locked(propertyName);
        } finally {
            getContextLock().readLock().unlock();
        }
    }

    protected abstract String findProperty_Locked(String propertyName);

    public final String setProperty(String propertyName, String value) {
        try {
            getContextLock().writeLock().lock();
            return setProperty_Locked(propertyName, value);
        } finally {
            getContextLock().writeLock().unlock();
        }
    }

    protected abstract String setProperty_Locked(String propertyName, String value);

    public final Identifiable addChild(Identifiable... identifiables) {
        try {
            getContextLock().writeLock().lock();
            return addChild_Locked(identifiables);
        } finally {
            getContextLock().writeLock().unlock();
        }
    }

    protected abstract Identifiable addChild_Locked(Identifiable... identifiables);

    public void addTreeListener(IdentifiableTreeListener l) {
        try {
            getContextLock().readLock().lock();
            addTreeListener_Locked(l);
        } finally {
            getContextLock().readLock().unlock();
        }
    }

    protected abstract void addTreeListener_Locked(IdentifiableTreeListener l);

    public void removeTreeListener(IdentifiableTreeListener l) {
        try {
            getContextLock().readLock().lock();
            removeTreeListener_Locked(l);
        } finally {
            getContextLock().readLock().unlock();
        }
    }

    protected abstract void removeTreeListener_Locked(IdentifiableTreeListener l);

    public Identifiable getRoot() {
        try {
            getContextLock().readLock().lock();
            return getRoot_Locked();
        } finally {
            getContextLock().readLock().unlock();
        }
    }

    protected abstract Identifiable getRoot_Locked();
}
