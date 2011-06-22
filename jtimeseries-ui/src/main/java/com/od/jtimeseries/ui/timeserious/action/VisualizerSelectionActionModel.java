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
package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.event.TimeSeriousBusListener;
import com.od.jtimeseries.ui.event.TimeSeriousBusListenerAdapter;
import com.od.jtimeseries.ui.identifiable.VisualizerContext;
import com.od.swing.eventbus.UIEventBus;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 04/01/11
 * Time: 17:12
 *
 */
public class VisualizerSelectionActionModel extends ContextSelectionActionModel<VisualizerContext> {

    public VisualizerSelectionActionModel() {
         //change the selected desktop when the bus event is sent
        UIEventBus.getInstance().addEventListener(TimeSeriousBusListener.class,
            new TimeSeriousBusListenerAdapter() {
                 public void visualizerSelected(VisualizerContext v) {
                     setSelectedContext(v);
                 }

                 public void visualizerFrameDisposed(VisualizerContext v) {
                     clearActionModelState();
                 }
            }
        );
    }

}
