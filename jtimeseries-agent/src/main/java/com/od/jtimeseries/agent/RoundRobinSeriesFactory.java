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
package com.od.jtimeseries.agent;

import com.od.jtimeseries.identifiable.Identifiable;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.impl.DefaultIdentifiableTimeSeries;
import com.od.jtimeseries.timeseries.impl.DefaultTimeSeriesFactory;
import com.od.jtimeseries.timeseries.impl.RoundRobinTimeSeries;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 11-Jun-2010
 * Time: 16:44:20
 */
public class RoundRobinSeriesFactory extends DefaultTimeSeriesFactory {

    private int maxSeriesSize;

    public RoundRobinSeriesFactory(int maxSeriesSize) {
        this.maxSeriesSize = maxSeriesSize;
    }

    public IdentifiableTimeSeries createTimeSeries(Identifiable parent, String path, String id, String description, Class classType, Object... parameters) {
        if ( classType.isAssignableFrom(IdentifiableTimeSeries.class)) {
            return new DefaultIdentifiableTimeSeries(
                id,
                description,
                new RoundRobinTimeSeries(maxSeriesSize)
            );
        }
        return super.createTimeSeries(parent, path, id, description, classType, parameters);
    }

}
