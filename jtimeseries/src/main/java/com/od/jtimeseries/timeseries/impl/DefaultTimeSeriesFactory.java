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
package com.od.jtimeseries.timeseries.impl;

import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.TimeSeriesFactory;
import com.od.jtimeseries.util.identifiable.IdentifiableBase;
import com.od.jtimeseries.util.identifiable.Identifiable;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 31-Dec-2008
 * Time: 11:15:27
 */
public class DefaultTimeSeriesFactory extends IdentifiableBase implements TimeSeriesFactory {

    public DefaultTimeSeriesFactory() {
        super(ID, ID);
        setDescription(getClass().getName());
    }

    public IdentifiableTimeSeries createTimeSeries(Identifiable parent, String path, String id, String description, Class classType, Object... parameters) {
        if ( classType.isAssignableFrom(DefaultIdentifiableTimeSeries.class)) {
            return new DefaultIdentifiableTimeSeries(id, description);
        }
        throw new UnsupportedOperationException("Cannot create time series of class " + classType);
    }
}
