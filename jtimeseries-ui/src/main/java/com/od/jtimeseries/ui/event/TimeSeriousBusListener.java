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
package com.od.jtimeseries.ui.event;

import com.od.jtimeseries.net.udp.TimeSeriesServer;
import com.od.jtimeseries.ui.identifiable.DesktopContext;
import com.od.jtimeseries.ui.identifiable.VisualizerContext;

/**
* Created by IntelliJ IDEA.
* User: Nick
* Date: 12-Dec-2010
* Time: 16:21:06
* To change this template use File | Settings | File Templates.
*/
public interface TimeSeriousBusListener {

    void serverAdded(TimeSeriesServer s);

    void serverRemoved(TimeSeriesServer s);

    void desktopSelected(DesktopContext desktopPane);

    void desktopDisposed(DesktopContext desktopPane);

    void visualizerSelected(VisualizerContext visualizerFrame);

    void visualizerFrameDisposed(VisualizerContext visualizerFrame);
}
