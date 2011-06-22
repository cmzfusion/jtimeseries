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

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.ui.download.panel.LoadSeriesFromServerCommand;
import com.od.jtimeseries.ui.identifiable.TimeSeriesServerContext;
import com.od.jtimeseries.ui.uicontext.IdentifiableListActionModel;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.swing.action.ModelDrivenAction;

import java.awt.event.ActionEvent;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 16/03/11
* Time: 06:58
*/
public class RefreshServerSeriesAction extends ModelDrivenAction<IdentifiableListActionModel> {

    private TimeSeriesContext rootContext;

    public RefreshServerSeriesAction(TimeSeriesContext rootContext, IdentifiableListActionModel actionModel) {
        super(actionModel, "Refresh Series from Server", ImageUtils.TIMESERIES_SERVER_REFRESH_ICON_16x16);
        this.rootContext = rootContext;
    }

    public void actionPerformed(ActionEvent e) {
        java.util.List<TimeSeriesServerContext> serverContexts = getActionModel().getSelected(TimeSeriesServerContext.class);
        LoadSeriesFromServerCommand l = new LoadSeriesFromServerCommand(rootContext);
        for ( TimeSeriesServerContext c : serverContexts ) {
            l.execute(c.getServer());
        }
    }

    public boolean isModelStateActionable() {
        return getActionModel().isSelectionLimitedToTypes(TimeSeriesServerContext.class);
    }
}
