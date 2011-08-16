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
import com.od.jtimeseries.ui.config.ExportableConfig;
import com.od.jtimeseries.ui.identifiable.DesktopContext;
import com.od.jtimeseries.ui.identifiable.HidablePeerContext;
import com.od.jtimeseries.ui.identifiable.VisualizerContext;
import com.od.jtimeseries.ui.uicontext.IdentifiableListActionModel;
import com.od.jtimeseries.ui.util.ContextNameCheckUtility;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 04/05/11
 * Time: 10:11
 *
 * Rename identifiable by changing the id
 * (as opposed to a logical name whereby the Displayable name changes only)
 */
public class RenameAction extends AbstractTimeSeriousIdentifiableAction {

    private JComponent parent;

    public RenameAction(JComponent parent, IdentifiableListActionModel actionModel) {
        super(actionModel, "Rename", null);
        this.parent = parent;
    }

    public void actionPerformed(ActionEvent e) {
         java.util.List<HidablePeerContext> serverContexts = getActionModel().getSelected(HidablePeerContext.class);
         for ( HidablePeerContext s : serverContexts ) {
            String name = (String)JOptionPane.showInputDialog(
                    SwingUtilities.getRoot(parent),
                    "Rename " + s.getId() + "?",
                    "New Name?",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    null,
                    s.getId()
            );
            if ( name != null && name.length() > 0 && ! name.equals(s.getId())) {
                TimeSeriesContext p = s.getParent();
                name = ContextNameCheckUtility.checkName(parent, p, name);
                p.removeChild(s);
                ExportableConfig configuration = s.getConfiguration();
                configuration.setTitle(name);
                HidablePeerContext c = s.newInstance(p, configuration);
                p.addChild(c);
            }
        }
    }

    public boolean isModelStateActionable() {
        return getActionModel().isSelectionLimitedToTypes(VisualizerContext.class) ||
                ( getActionModel().isSelectionLimitedToTypes(DesktopContext.class) &&
                ! isMainDesktopSelected());
    }
}
