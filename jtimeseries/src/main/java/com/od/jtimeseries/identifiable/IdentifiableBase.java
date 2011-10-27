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

import com.od.jtimeseries.util.TimeSeriesExecutorFactory;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 21-Jan-2009
 * Time: 11:59:44
 *
 * A base class implementing the Identifiable interface
 */
public class IdentifiableBase extends LockingIdentifiable {

    private volatile String id;
    private volatile String description;
    private volatile Identifiable parent;
    private Properties properties = new Properties();
    private final Map<String, Identifiable> childrenById = new TreeMap<String, Identifiable>();

    private List<IdentifiableTreeListener> treeListeners = new CopyOnWriteArrayList<IdentifiableTreeListener>();
    private ChildTreeEventPropagator childEventPropagator = new ChildTreeEventPropagator();
    private DefaultIdentifiableQueries queries = new DefaultIdentifiableQueries(this);

    public IdentifiableBase(Identifiable parent, String id, String description) {
        this(id, description);
        this.parent = parent;
    }
    
    public IdentifiableBase(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    protected Identifiable addChild_Locked(Identifiable... identifiables) {
        checkIdCharacters(identifiables);
        for ( Identifiable i : identifiables) {
            checkUniqueIdAndAdd(i);
        }
        return this;
    }


    protected <E extends Identifiable> void checkUniqueIdAndAdd(E identifiable) {
        if (childrenById.containsKey(identifiable.getId())) {
            throw new DuplicateIdException("id " + identifiable.getId() + " already exists in this context");
        } else {
            childrenById.put(identifiable.getId(), identifiable);
        }

        //remove from current parent, if any
        Identifiable currentParent = identifiable.getParent();
        if ( currentParent != null) {
            currentParent.removeChild(identifiable);
        }

        //set this node as new parent and fire event
        identifiable.setParent(this);
        identifiable.addTreeListener(childEventPropagator);
        fireDescendantsAdded(new IdentifiableTreeEvent(getRoot(), getPath(), identifiable));
    }

    protected String getParentPath_Locked() {
        String result;
        if ( parent == null) {
            result = ""; //root context has no id in namespace path
        } else {
            result = parent.getPath();
        }
        return result;
    }

    protected String getPath_Locked() {
        StringBuilder path = new StringBuilder(getParentPath());
        if ( path.length() > 0) {
            path.append(".");
        }
        path.append(parent == null ? "" : getId());
        return path.toString();
    }

    public String getDescription() {
        return description;
    }


    public Identifiable getParent() {
        return parent;
    }

    protected Identifiable setParent_Locked(Identifiable parent) {
        Identifiable oldParent = this.parent;
        this.parent = parent;
        return oldParent;
    }

    protected boolean removeChild_Locked(Identifiable i) {
        boolean removed = false;
        if ( containsChild(i) ) {
            childrenById.remove(i.getId());
            i.setParent(null);
            i.removeTreeListener(childEventPropagator);
            removed = true;
            fireDescendantsRemoved(new IdentifiableTreeEvent(getRoot(), getPath(), i));
        }
        return removed;
    }

    protected boolean containsChildWithId_Locked(String id) {
        return childrenById.containsKey(id);
    }

    protected boolean containsChild_Locked(Identifiable child) {
        boolean result = false;
        if ( child != null) {
            result = childrenById.get(child.getId()) == child;
        }
        return result;
    }

    protected String getProperty_Locked(String propertyName) {
        return properties.getProperty(propertyName);
    }

    protected Properties getProperties_Locked() {
        Properties p = new Properties();
        p.putAll(properties);
        return p;
    }

    protected void putAllProperties_Locked(Properties p) {
        properties.putAll(p);
    }

    protected String findProperty_Locked(String propertyName) {
        String property = getProperty(propertyName);
        if ( property == null && ! isRoot() ) {
            property = getParent().findProperty(propertyName);
        }
        return property;
    }

    protected String setProperty_Locked(String propertyName, String value) {
        return (String)properties.setProperty(propertyName,  value);
    }

    protected String removeProperty_Locked(String propertyName) {
        return (String)properties.remove(propertyName);
    }

    protected List<Identifiable> getChildren_Locked() {
        List<Identifiable> children = new ArrayList<Identifiable>();
        children.addAll(this. childrenById.values());
        return children;
    }

    protected <E extends Identifiable> List<E> getChildren_Locked(Class<E> classType) {
        List<E> list = new ArrayList<E>();
        for ( Identifiable i : childrenById.values()) {
            if ( classType.isAssignableFrom(i.getClass())) {
                list.add((E)i);
            }
        }
        return list;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    protected void checkIdCharacters(Identifiable[] identifiables) {
        for ( Identifiable i : identifiables) {
            checkId(i.getId());
        }
    }

    protected void checkId(String id) {
        String problem = IdentifiablePathUtils.checkId(id);
        if ( problem != null) {
            throw new IdentifierException(problem);
        }
    }

    public String toString() {
        return getId();
    }

    protected Identifiable getRoot_Locked() {
        return isRoot() ? this : getParent().getRoot();
    }

    protected void addTreeListener_Locked(final IdentifiableTreeListener l) {
        //event firing is handled asynchronously by an event thread/event queue
        //we don't want the new listener to receive any events from the event thread
        //which were placed in the event queue before the new listener was added -
        //solution - place a task at the end of the event queue to add the listener
        Executor e = TimeSeriesExecutorFactory.getExecutorForIdentifiableTreeEvents(this);
        e.execute(new Runnable() {
            public void run() {
                if (!treeListeners.contains(l)) {
                    treeListeners.add(l);
                }
            }
        });
    }

    protected void removeTreeListener_Locked(final IdentifiableTreeListener l) {
        treeListeners.remove(l);
    }

    public void fireNodeChanged(final Object changeDescription) {
        fireEvent(new Runnable() {
            public void run() {
                for ( IdentifiableTreeListener l : treeListeners) {
                    l.nodeChanged(IdentifiableBase.this, changeDescription);
                }
            }
        });
    }
    
    protected void fireDescendantsChanged(final IdentifiableTreeEvent e) {
        fireEvent(new Runnable() {
            public void run() {
                for ( IdentifiableTreeListener l : treeListeners) {
                    l.descendantChanged(e);
                }
            }
        });
    }
    
    protected void fireDescendantsAdded(final IdentifiableTreeEvent e) {
        fireEvent(new Runnable() {
            public void run() {
                for ( IdentifiableTreeListener l : treeListeners) {
                    l.descendantAdded(e);
                }
            }
        });
    }

    protected void fireDescendantsRemoved(final IdentifiableTreeEvent e) {
        fireEvent(new Runnable() {
            public void run() {
                for ( IdentifiableTreeListener l : treeListeners) {
                    l.descendantRemoved(e);
                }
            }
        });
    }

    //events are fired on a dedicated single thread executor
    //the executor queue guarantees correct ordering of events published without requiring
    //the thread which caused the change to hold any locks while events are fired
    private void fireEvent(Runnable r) {
        Executor e = TimeSeriesExecutorFactory.getExecutorForIdentifiableTreeEvents(this);
        e.execute(r);
    }

    protected boolean isRoot_Locked() {
        return getParent() == null;
    }

    protected <E extends Identifiable> E get_Locked(String path, Class<E> classType) {
        PathParser p = new PathParser(path);
        Identifiable result;
        if (p.isEmpty()) {
            result = this;
        } else if (p.isSingleNode()) {
            result = childrenById.get(path);
        } else {
            String childContext = p.removeFirstNode();
            Identifiable c = get(childContext);
            result = c == null ? null : c.get(p.getRemainingPath(), classType);
        }
        if ( result != null && ! classType.isAssignableFrom(result.getClass())) {
            throw new WrongClassTypeException("Cannot convert identifiable " + result.getPath() + " from " + result.getClass() + " to " + classType);
        }
        return (E)result;
    }

    protected <E extends Identifiable> E getFromAncestors_Locked(String id, Class<E> classType) {
        E result = get(id, classType);
        if (result == null && ! isRoot()) {
            result = getParent().getFromAncestors(id, classType);
        }
        return result;
    }

    protected <E extends Identifiable> E create_Locked(String path, String description, Class<E> clazz, Object... parameters) {
        PathParser p = new PathParser(path);
        if ( p.isSingleNode() || p.isEmpty() ) {
            E s = get(path, clazz);
            if ( s == null) {
                s = doCreate(path, description, clazz, parameters);
                if ( s != null) {
                    addChild(s);
                }
            }
            return s;
        } else {
            //create the next context in the path, and recusively call create
            String nextNode = p.removeFirstNode();
            Identifiable c = create(nextNode, nextNode, Identifiable.class, parameters);
            return c.create(p.getRemainingPath(), description, clazz, parameters);
        }
    }

    protected <E extends Identifiable> E remove_Locked(String path, Class<E> classType) {
        PathParser p = new PathParser(path);
        E result;
        if (p.isSingleNode()) {
            result = get(path, classType);
            if ( result != null ) {
                removeChild(result);
            }
        } else {
            String childContext = p.removeFirstNode();
            Identifiable c = get(childContext);
            result = c == null ? null : c.remove(p.getRemainingPath(), classType);
        }
        return result;
    }

    protected <E extends Identifiable> E doCreate(String path, String description, Class<E> clazz, Object[] parameters) {
        return null;
    }

    protected String getPathForChild(String id) {
        return getPath() + NAMESPACE_SEPARATOR + id;
    }


    protected <E extends Identifiable> QueryResult<E> findAll_Locked(Class<E> assignableToClass) {
        return queries.findAll(assignableToClass);
    }

    protected <E extends Identifiable> QueryResult<E> findAll_Locked(String searchPattern, Class<E> assignableToClass) {
        return queries.findAll(searchPattern, assignableToClass);
    }
    
    //receive events from children, propagate them with updated path
    private class ChildTreeEventPropagator implements IdentifiableTreeListener {

        public void nodeChanged(Identifiable node, Object changeDescription) {
            IdentifiableTreeEvent e = new IdentifiableTreeEvent(getRoot(), getPath(), node);
            for ( IdentifiableTreeListener l : treeListeners) {
                l.descendantChanged(e);
            }
        }

        public void descendantChanged(IdentifiableTreeEvent contextTreeEvent) {
            for ( IdentifiableTreeListener l : treeListeners) {
                l.descendantChanged(contextTreeEvent);
            }
        }

        public void descendantAdded(IdentifiableTreeEvent contextTreeEvent) {
            for ( IdentifiableTreeListener l : treeListeners) {
                l.descendantAdded(contextTreeEvent);
            }
        }

        public void descendantRemoved(IdentifiableTreeEvent contextTreeEvent) {
            for ( IdentifiableTreeListener l : treeListeners) {
                l.descendantRemoved(contextTreeEvent);
            }
        }
    }
}
