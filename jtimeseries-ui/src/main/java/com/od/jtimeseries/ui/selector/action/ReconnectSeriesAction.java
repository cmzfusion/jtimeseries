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
package com.od.jtimeseries.ui.selector.action;

import com.od.jtimeseries.identifiable.Identifiable;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.uicontext.IdentifiableListActionModel;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.swing.action.ModelDrivenAction;

import java.awt.event.ActionEvent;
import java.util.List;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
*/
public class ReconnectSeriesAction extends ModelDrivenAction<IdentifiableListActionModel> {

    public ReconnectSeriesAction(IdentifiableListActionModel selectionModel) {
        super(selectionModel, "Reconnect Time Series to Server", ImageUtils.CONNECT_ICON_16x16);
    }

    public void actionPerformed(ActionEvent e) {
        List<Identifiable> series = getActionModel().getSelected();
        for ( Identifiable s : series) {
           if ( s instanceof UIPropertiesTimeSeries) {
               if (((UIPropertiesTimeSeries)s).isStale()) {
                    ((UIPropertiesTimeSeries)s).setStale(false);
               }
           }
        }
    }

    protected boolean isModelStateActionable() {
        boolean result = false;
        if (getActionModel().isSelectionLimitedToTypes(UIPropertiesTimeSeries.class) ) {
            result = true;
            //actionable if all selected are stale
            for (Identifiable i : getActionModel().getSelected()) {
                result &= ((UIPropertiesTimeSeries)i).isStale();
            }
        }
        return result;
    }

}
