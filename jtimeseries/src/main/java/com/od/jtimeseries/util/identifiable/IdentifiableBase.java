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
package com.od.jtimeseries.util.identifiable;

import com.od.jtimeseries.context.*;
import com.od.jtimeseries.util.JTimeSeriesConstants;
import com.od.jtimeseries.util.PathParser;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 21-Jan-2009
 * Time: 11:59:44
 *
 * A base class implementing the Identifiable interface
 */
public class IdentifiableBase extends LockingIdentifiable {

    public static final String NAMESPACE_SEPARATOR = JTimeSeriesConstants.NAMESPACE_SEPARATOR;
    public static final String NAMESPACE_REGEX_PATH_SEPARATOR = "\\.";
    private volatile String id;
    private volatile String description;
    private volatile Identifiable parent;
    private Properties properties = new Properties();
    private final Map<String, Identifiable> childrenById = new TreeMap<String, Identifiable>();
    private CopyOnWriteArrayList<IdentifiableTreeListener> treeListeners = new CopyOnWriteArrayList<IdentifiableTreeListener>();
    private ChildTreeListener childTreeListener = new ChildTreeListener();
    
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
        identifiable.setParent(this);
        identifiable.addTreeListener(childTreeListener);
        fireNodesAdded(new IdentifiableTreeEvent(getPath(), identifiable));
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
            i.removeTreeListener(childTreeListener);
            removed = true;
            fireNodesRemoved(new IdentifiableTreeEvent(getPath(), i));
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
        if ( id == null ) {
            throw new IdentifierException("id cannot be null");
        }else if ( id.contains(NAMESPACE_REGEX_PATH_SEPARATOR)) {
            throw new IdentifierException("id cannot contain a '" + NAMESPACE_REGEX_PATH_SEPARATOR + "', this is the path separator symbol");
        } else if ( id.equals("")) {
            throw new IdentifierException("id cannot be an empty string");
        }
    }

    public String toString() {
        return getId();
    }

    protected Identifiable getRoot_Locked() {
        return isRoot() ? this : getParent().getRoot();
    }

    protected boolean addTreeListener_Locked(IdentifiableTreeListener l) {
        boolean result = false;
        if ( ! treeListeners.contains(l)) {
            treeListeners.add(l);
            result = true;
        }
        return result;
    }

    protected boolean removeTreeListener_Locked(IdentifiableTreeListener l) {
        return treeListeners.remove(l);
    }
    
    protected void fireNodesChanged(IdentifiableTreeEvent e) {
        for ( IdentifiableTreeListener l : treeListeners) {
            l.nodesChanged(e);
        }
    }
    
    protected void fireNodesAdded(IdentifiableTreeEvent e) {
        for ( IdentifiableTreeListener l : treeListeners) {
            l.nodesAdded(e);
        }
    }
    
    protected void fireNodesRemoved(IdentifiableTreeEvent e) {
        for ( IdentifiableTreeListener l : treeListeners) {
            l.nodesRemoved(e);
        }
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
                addChild(s);
            }
            return s;
        } else {
            //create the next context in the path, and recusively call create
            String nextNode = p.removeFirstNode();
            Identifiable c = create(nextNode, nextNode, Identifiable.class);
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
    
    //receive events from children, propogate them with updated path
    private class ChildTreeListener implements IdentifiableTreeListener {
        
        public void nodesChanged(IdentifiableTreeEvent contextTreeEvent) {
            fireNodesChanged(new IdentifiableTreeEvent(getId() + JTimeSeriesConstants.NAMESPACE_SEPARATOR + contextTreeEvent.getPath(), contextTreeEvent.getNodes()));
        }

        public void nodesAdded(IdentifiableTreeEvent contextTreeEvent) {
            fireNodesAdded(new IdentifiableTreeEvent(getId() + JTimeSeriesConstants.NAMESPACE_SEPARATOR + contextTreeEvent.getPath(), contextTreeEvent.getNodes()));
        }

        public void nodesRemoved(IdentifiableTreeEvent contextTreeEvent) {
            fireNodesRemoved(new IdentifiableTreeEvent(getId() + JTimeSeriesConstants.NAMESPACE_SEPARATOR + contextTreeEvent.getPath(), contextTreeEvent.getNodes()));
        }
    }
}
