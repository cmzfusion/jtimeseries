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
package com.od.jtimeseries.util.identifiable;

import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 09-Jun-2010
* Time: 09:45:59
*/
public class PathParser {

    private StringBuilder path;

    public PathParser(String path) {
        this.path = new StringBuilder(path);
    }

    public String removeFirstNode() {
        String result;
        if ( path.length() == 0 ) {
            throw new UnsupportedOperationException("Cannot remove a node from an empty path");
        }
        int index = path.indexOf(Identifiable.NAMESPACE_SEPARATOR);
        if ( index == -1) {
            result = path.toString();
            path.setLength(0);
        } else {
            result = path.substring(0, index);
            path.delete(0, index + 1);
        }
        return result;
    }

    public String removeLastNode() {
        String result;
        if ( path.length() == 0 ) {
            throw new UnsupportedOperationException("Cannot remove a node from an empty path");
        }
        int index = path.lastIndexOf(Identifiable.NAMESPACE_SEPARATOR);
        if ( index == -1 ) {
            result = path.toString();
            path.setLength(0);
        } else {
            result = path.substring(index + 1, path.length());
            path.delete(index, path.length());
        }
        return result;
    }

    public String getRemainingPath() {
        return path.toString();
    }

    public boolean isSingleNode() {
        int index = path.indexOf(Identifiable.NAMESPACE_SEPARATOR);
        return index == -1 && path.length() > 0;
    }

    public boolean isEmpty() {
        return path.toString().trim().length() == 0;
    }

    public static String lastNode(String path) {
        int index = path.lastIndexOf(Identifiable.NAMESPACE_SEPARATOR);
        return ( index == -1 ) ? path :  path.substring(index + 1, path.length());
    }

    public static String firstNode(String path) {
        int index = path.lastIndexOf(Identifiable.NAMESPACE_SEPARATOR);
        return ( index == -1) ? path : path.substring(0, index);
    }

    public static LinkedList<String> splitPath(String path) {
        LinkedList<String> l = new LinkedList<String>();
        StringTokenizer st = new StringTokenizer(path, Identifiable.NAMESPACE_SEPARATOR);
        while(st.hasMoreTokens()) {
            l.add(st.nextToken());
        }
        return l;
    }
}
