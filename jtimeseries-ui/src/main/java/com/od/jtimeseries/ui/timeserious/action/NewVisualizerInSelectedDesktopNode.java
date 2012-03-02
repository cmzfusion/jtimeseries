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

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 26-Mar-2010
 * Time: 16:32:43
 */
public class NewVisualizerInSelectedDesktopNode extends AbstractNewVisualizerAction<IdentifiableListActionModel> {

    public NewVisualizerInSelectedDesktopNode(Component parentComponent, IdentifiableListActionModel m, VisualizerSelectionActionModel visualizerSelectionActionModel) {
        super(m, "New Visualizer...", ImageUtils.VISUALIZER_NEW_16x16, parentComponent, visualizerSelectionActionModel);
        super.putValue(SHORT_DESCRIPTION, "Create a new chart visualizer in current desktop");
    }

    public boolean isModelStateActionable() {
        return getActionModel().isSelectionLimitedToTypes(DesktopContext.class) &&
               getActionModel().getSelectionCount() == 1;
    }

    @Override
    protected DesktopContext getSelectedDesktop() {
        return getActionModel().getSelected(DesktopContext.class).get(0);
    }
}
