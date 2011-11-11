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
package com.od.jtimeseries.server.servermetrics;

import com.od.jtimeseries.component.managedmetric.AbstractManagedMetric;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.identifiable.Identifiable;
import com.od.jtimeseries.server.message.AppendToSeriesMessageListener;
import com.od.jtimeseries.source.ValueRecorder;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 25-Nov-2009
 * Time: 19:33:20
 * To change this template use File | Settings | File Templates.
 */
public class SeriesReceivingUdpUpdates extends AbstractManagedMetric {

    private static final String id = "SeriesReceivingUdpUpdates";
    private String parentContextPath;

    public SeriesReceivingUdpUpdates(String parentContextPath) {
        this.parentContextPath = parentContextPath;
    }

   protected String getSeriesPath() {
        return parentContextPath + Identifiable.NAMESPACE_SEPARATOR + id;
    }

    public void doInitializeMetric(TimeSeriesContext rootContext, String path) {
        ValueRecorder v = rootContext.createValueRecorderSeries(path, "A count of the series which have received UDP updates during the last " + AppendToSeriesMessageListener.STALE_SERIES_DELAY);
        AppendToSeriesMessageListener.setLiveSeriesTotalValueRecorder(v);
    }
}
