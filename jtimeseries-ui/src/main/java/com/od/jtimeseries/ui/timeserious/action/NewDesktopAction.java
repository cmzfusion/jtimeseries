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
import com.od.jtimeseries.ui.config.DesktopConfiguration;
import com.od.jtimeseries.ui.identifiable.DesktopContext;
import com.od.jtimeseries.ui.util.ContextNameCheckUtility;
import com.od.jtimeseries.ui.util.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 06/04/11
 * Time: 07:10
 */
public class NewDesktopAction extends AbstractAction {

    private Component component;
    private TimeSeriesContext desktopContainingContext;
    private DesktopSelectionActionModel desktopSelectionActionModel;

    public NewDesktopAction(Component component, TimeSeriesContext desktopContainingContext, DesktopSelectionActionModel desktopSelectionActionModel) {
        super("New Desktop", ImageUtils.DESKTOP_NEW_16x16);
        this.component = component;
        this.desktopContainingContext = desktopContainingContext;
        this.desktopSelectionActionModel = desktopSelectionActionModel;
        super.putValue(SHORT_DESCRIPTION, "Create a new desktop");
    }

    public void actionPerformed(ActionEvent e) {
        String name = ContextNameCheckUtility.getNameFromUser(component, desktopContainingContext, "Name for Desktop?", "Choose Name for Desktop", "");
        if ( name != null) { //check if user cancelled
            DesktopConfiguration config = new DesktopConfiguration(name);
            DesktopContext desktopContext = desktopContainingContext.create(name, name, DesktopContext.class, config);
            desktopSelectionActionModel.setSelectedContext(desktopContext);
        }
    }
}
