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

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 04-Jan-2009
 * Time: 15:22:20
 * To change this template use File | Settings | File Templates.
 */
public interface Identifiable {

    String getId();

    String getParentPath();

    /**
     * @return fully qualified namespace and id
     */
    String getContextPath();

    void setDescription(String description);

    String getDescription();

    Identifiable getParent();

    /**
     * @return old parent or null if no previous parent
     */
    Identifiable setParent(Identifiable parent);

    List<Identifiable> getChildren();

    <E extends Identifiable> List<E> getChildren(Class<E> classType);

    boolean removeChild(Identifiable c);

    /**
     * Structural changes made to the context tree structure should be made
     * while holding the lock on this Object
     */
    Object getTreeLock();

    Identifiable getRoot();

    boolean isRoot();

    Identifiable get(String id);

    <E extends Identifiable> E get(String id, Class<E> classType);

    boolean containsChildWithId(String id);

    boolean containsChild(Identifiable i);

    /**
     * @return value associated with propertyName for this identifiable, or null if property not set
     */
    String getProperty(String propertyName);
    
     /**
     * This mechanism allows an Identifiable to 'inherit' a property from a parent
     *
     * @return a value for propertyName by searching the identifiable tree starting with this node
     * and progressing upwards to the root until a value is found. returns null if no value can be found.
     */
    String findProperty(String propertyName);

    /**
     * Set the propertyName to the supplied value
     * @return the previous value for propertyName, or null if property was not set
     */
    String setProperty(String propertyName, String value);
}
