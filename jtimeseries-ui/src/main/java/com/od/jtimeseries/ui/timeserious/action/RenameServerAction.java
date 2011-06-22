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

import com.od.jtimeseries.ui.identifiable.TimeSeriesServerContext;
import com.od.jtimeseries.ui.uicontext.IdentifiableListActionModel;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.swing.action.ModelDrivenAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 16/03/11
* Time: 06:56
*/
public class RenameServerAction extends ModelDrivenAction<IdentifiableListActionModel> {

    private JComponent parent;

    public RenameServerAction(JComponent parent, IdentifiableListActionModel actionModel) {
        super(actionModel, "Rename Server", ImageUtils.TIMESERIES_SERVER_RENAME_ICON_16x16);
        this.parent = parent;
    }

    public void actionPerformed(ActionEvent e) {
         java.util.List<TimeSeriesServerContext> serverContexts = getActionModel().getSelected(TimeSeriesServerContext.class);
         for ( TimeSeriesServerContext s : serverContexts ) {
            String name = JOptionPane.showInputDialog(
                    SwingUtilities.getRoot(parent),
                    "Rename Server " + s.getServer().getDescription() + "?",
                    s.getServer().getDescription()
            );
            if ( name != null && name.length() > 0) {
                s.getServer().setDescription(name);
            }
        }
    }

    public boolean isModelStateActionable() {
        return getActionModel().isSelectionLimitedToTypes(TimeSeriesServerContext.class);
    }
}
