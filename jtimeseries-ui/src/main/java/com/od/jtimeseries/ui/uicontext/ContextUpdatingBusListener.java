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
package com.od.jtimeseries.ui.uicontext;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.identifiable.Identifiable;
import com.od.jtimeseries.net.udp.TimeSeriesServer;
import com.od.jtimeseries.ui.event.TimeSeriousBusListenerAdapter;
import com.od.jtimeseries.ui.identifiable.TimeSeriesServerContext;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 16/02/11
 * Time: 17:45
 *
 * Remove series if a server is removed
 */
public class ContextUpdatingBusListener extends TimeSeriousBusListenerAdapter {

    private TimeSeriesContext rootContext;

    public ContextUpdatingBusListener(TimeSeriesContext rootContext) {
        this.rootContext = rootContext;
    }

    public void serverRemoved(TimeSeriesServer s) {
        for (Identifiable i : rootContext.getChildren()) {
            if ( i instanceof TimeSeriesServerContext) {
                if (((TimeSeriesServerContext)i).getServer().equals(s)) {
                    rootContext.removeChild(i);
                    break;
                }
            }
        }
    }
}
