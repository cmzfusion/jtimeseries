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

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.ui.uicontext.IdentifiableListActionModel;
import com.od.jtimeseries.ui.util.Disposable;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.jtimeseries.identifiable.Identifiable;
import com.od.swing.action.ModelDrivenAction;

import java.awt.event.ActionEvent;
import java.util.List;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
*/
public class RemoveSeriesAction extends ModelDrivenAction<IdentifiableListActionModel> {

    public RemoveSeriesAction(IdentifiableListActionModel seriesSelectionModel) {
        super(seriesSelectionModel, "Remove Series", ImageUtils.REMOVE_ICON_16x16);
    }

    public void actionPerformed(ActionEvent e) {
        List<Identifiable> series = getActionModel().getSelected();
        for ( Identifiable s : series) {
            TimeSeriesContext c = (TimeSeriesContext)s.getParent();
            c.removeChild(s);

            if ( s instanceof Disposable) {
                ((Disposable)s).dispose();
            }
        }
    }
}
