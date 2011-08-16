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

import com.od.jtimeseries.ui.config.VisualizerConfiguration;
import com.od.jtimeseries.ui.identifiable.DesktopContext;
import com.od.jtimeseries.ui.identifiable.VisualizerContext;
import com.od.jtimeseries.ui.util.ContextNameCheckUtility;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.swing.action.ModelDrivenAction;

import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 26-Mar-2010
 * Time: 16:32:43
 */
public class NewVisualizerAction extends ModelDrivenAction<DesktopSelectionActionModel> {

    private Component parentComponent;
    private VisualizerSelectionActionModel visualizerSelectionActionModel;

    public NewVisualizerAction(Component parentComponent, DesktopSelectionActionModel m, VisualizerSelectionActionModel visualizerSelectionActionModel) {
        super(m, "New Visualizer...", ImageUtils.VISUALIZER_NEW_16x16);
        this.parentComponent = parentComponent;
        this.visualizerSelectionActionModel = visualizerSelectionActionModel;
        super.putValue(SHORT_DESCRIPTION, "Create a new chart visualizer in current desktop");
    }

    public void actionPerformed(ActionEvent e) {
        if ( getActionModel().isContextSelected() ) {
            DesktopContext desktopContext = getActionModel().getSelectedContext();
            String name = ContextNameCheckUtility.getNameFromUser(
                    parentComponent, desktopContext, "Name for Visualizer?", "Choose Name for Visualizer", ""
            );
            if ( name != null) { //check if user cancelled
                VisualizerConfiguration c = new VisualizerConfiguration(name);
                VisualizerContext visualizerContext = desktopContext.create(c.getTitle(), c.getTitle(), VisualizerContext.class, c);
                visualizerSelectionActionModel.setSelectedContext(visualizerContext);
            }
        }
    }
}
