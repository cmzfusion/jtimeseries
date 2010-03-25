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
package com.od.jtimeseries.server.servermetrics;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 06-Dec-2009
 * Time: 11:57:12
 *
 * Simply stores a list of metrics, usually created and populated from xml via spring context.
 * Other implementations might connect to a database to read in the details of metrics to be
 * created, for example
 */
public class DefaultMetricSource implements ServerMetricSource {

    private List<ServerMetric> metrics;

    public DefaultMetricSource(List<ServerMetric> metrics) {
        this.metrics = metrics;
    }

    public List<ServerMetric> getServerMetrics() {
        return metrics;
    }
}
