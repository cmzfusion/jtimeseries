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

import com.od.jtimeseries.ui.identifiable.DesktopContext;
import com.od.jtimeseries.ui.uicontext.IdentifiableListActionModel;
import com.od.jtimeseries.ui.util.ImageUtils;

import javax.swing.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 26/04/11
 * Time: 10:19
 * To change this template use File | Settings | File Templates.
 */
public class RemoveDesktopAction extends AbstractRemoveHideablePeerAction<DesktopContext> {

    private JComponent parentComponent;

    public RemoveDesktopAction(IdentifiableListActionModel selectionModel, JComponent parentComponent) {
        super(selectionModel, "Remove Desktop", ImageUtils.DESKTOP_DELETE_16x16, DesktopContext.class);
        this.parentComponent = parentComponent;
        super.putValue(SHORT_DESCRIPTION, "Remove the selected desktop");
    }

    public boolean isModelStateActionable() {
        boolean actionable = super.isModelStateActionable();
        actionable &= ! isMainDesktopSelected();
        return actionable;
    }

    protected boolean confirmRemove(List<DesktopContext> desktops) {
        return JOptionPane.showConfirmDialog(
            SwingUtilities.getRoot(parentComponent),
            "Remove desktop" + (desktops.size() > 1 ? "s" : "") + " and all associated visualizers?",
            "Remove Desktop" + (desktops.size() > 1 ? "s" : "") + "?",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION;
    }
}
