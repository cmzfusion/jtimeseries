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

import com.od.jtimeseries.util.JTimeSeriesConstants;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 21-Jan-2009
 * Time: 11:59:44
 *
 * A base class implementing the Identifiable interface
 */
public class IdentifiableBase implements Identifiable {

    /**
     * A lock for the context tree structure which should be held while changing or traversing the tree structure, to ensure integrity
     */
    private static final Object TREE_LOCK = new Object();

    protected static final String NAMESPACE_SEPARATOR = JTimeSeriesConstants.NAMESPACE_SEPARATOR;
    protected static final String NAMESPACE_REGEX_PATH_SEPARATOR = "\\.";
    private volatile String id;
    private volatile String description;
    private Identifiable parent;
    private Properties properties = new Properties();

    private static List<Identifiable> EMPTY_CHILD_LIST = Collections.unmodifiableList(new ArrayList<Identifiable>());

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

    public String getParentPath() {
        synchronized (getTreeLock()) {
            String result;
            if ( parent == null) {
                result = ""; //root context has no id in namespace path
            } else {
                result = parent.getPath();
            }
            return result;
        }
    }

    public String getPath() {
        synchronized (getTreeLock()) {
            StringBuilder path = new StringBuilder(getParentPath());
            if ( path.length() > 0) {
                path.append(".");
            }
            path.append(parent == null ? "" : getId());
            return path.toString();
        }
    }

    public String getDescription() {
        return description;
    }

    public Identifiable getParent() {
        return parent;
    }

    public Identifiable setParent(Identifiable parent) {
        synchronized (getTreeLock()) {
            Identifiable oldParent = this.parent;
            this.parent = parent;
            return oldParent;
        }
    }

    public boolean removeChild(Identifiable c) {
        return false;
    }

    public boolean containsChildWithId(String id) {
        return false;
    }

    public boolean containsChild(Identifiable i) {
        return false;
    }

    public String getProperty(String propertyName) {
        return properties.getProperty(propertyName);
    }

    public String findProperty(String propertyName) {
        String property = getProperty(propertyName);
        if ( property == null && ! isRoot() ) {
            property = getParent().findProperty(propertyName);
        }
        return property;
    }

    public String setProperty(String propertyName, String value) {
        return (String)properties.setProperty(propertyName,  value);
    }

    public List<Identifiable> getChildren() {
        return EMPTY_CHILD_LIST;
    }

    public <E extends Identifiable> List<E> getChildren(Class<E> classType) {
        return (List<E>)EMPTY_CHILD_LIST;
    }

    public Object getTreeLock() {
       return TREE_LOCK;
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

    protected LinkedList<String> splitPath(String path) {
        return new LinkedList<String>(Arrays.asList(path.split(NAMESPACE_REGEX_PATH_SEPARATOR)));
    }

    public Identifiable getRoot() {
        synchronized (getTreeLock()) {
            return isRoot() ? this : getParent().getRoot();
        }
    }

    public boolean isRoot() {
        return getParent() == null;
    }

    public Identifiable get(String id) {
        return null;
    }

    public <E extends Identifiable> E get(String id, Class<E> classType) {
        return null;
    }

    protected String getPathForChild(String id) {
        return getPath() + NAMESPACE_SEPARATOR + id;
    }
}
