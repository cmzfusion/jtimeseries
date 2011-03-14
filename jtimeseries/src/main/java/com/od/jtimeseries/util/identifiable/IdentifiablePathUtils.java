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

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 17-Feb-2009
 * Time: 14:06:19
 */
public class IdentifiablePathUtils {

    public static final String DEFAULT_ROOT_CONTEXT_ID = "Root Context";
    public static final String NAMESPACE_SEPARATOR = ".";
    public static final String NAMESPACE_SEPARATOR_REGEX_TOKEN = "\\.";

    /**
     * @return a String value describing the problem, if the id is not valid, or null if the id is valid
     */
    public static String checkId(String id) {
        String problemDescription = null;
        if ( id == null ) {
            problemDescription = "The id cannot be null";
        }else if ( id.contains(IdentifiablePathUtils.NAMESPACE_SEPARATOR)) {
            problemDescription = "The id cannot contain a '" + IdentifiablePathUtils.NAMESPACE_SEPARATOR + "', this is the path separator symbol";
        } else if ( id.equals("")) {
            problemDescription = "The id cannot be an empty string";
        }
        return problemDescription;
    }
}

