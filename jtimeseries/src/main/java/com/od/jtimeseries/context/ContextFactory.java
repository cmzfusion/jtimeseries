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
package com.od.jtimeseries.context;

import com.od.jtimeseries.util.identifiable.IdentifiableFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 17-Feb-2009
 * Time: 13:51:19
 */
public interface ContextFactory extends IdentifiableFactory {

    /**
     * All ContextFactory should use this ID, to make sure only one ContextFactory can exist per context
     */
    public static final String ID = "ContextFactory";

    /**
     * @param parent, may be null in which case a root context should be created.
     */
    TimeSeriesContext createContext(TimeSeriesContext parent, String id, String description, Class classType, Object... parameters);

}
