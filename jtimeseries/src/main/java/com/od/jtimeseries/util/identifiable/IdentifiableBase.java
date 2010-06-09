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
public class IdentifiableBase extends LockingIdentifiable {

    protected static final String NAMESPACE_SEPARATOR = JTimeSeriesConstants.NAMESPACE_SEPARATOR;
    protected static final String NAMESPACE_REGEX_PATH_SEPARATOR = "\\.";
    private volatile String id;
    private volatile String description;
    private volatile Identifiable parent;
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

    protected boolean removeChild_Locked(Identifiable c) {
        return false;
    }

    protected boolean containsChildWithId_Locked(String id) {
        return false;
    }

    protected boolean containsChild_Locked(Identifiable i) {
        return false;
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
        return EMPTY_CHILD_LIST;
    }

    protected <E extends Identifiable> List<E> getChildren_Locked(Class<E> classType) {
        return (List<E>)EMPTY_CHILD_LIST;
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
        try {
            getContextLock().readLock().lock();
            return isRoot() ? this : getParent().getRoot();
        } finally {
            getContextLock().readLock().unlock();
        }
    }

    protected boolean isRoot_Locked() {
        return getParent() == null;
    }

    protected Identifiable get_Locked(String path) {
        return null;
    }

    protected <E extends Identifiable> E get_Locked(String id, Class<E> classType) {
        return null;
    }

    protected String getPathForChild(String id) {
        return getPath() + NAMESPACE_SEPARATOR + id;
    }
}
