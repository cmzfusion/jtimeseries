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

import com.od.jtimeseries.ui.identifiable.VisualizerContext;
import com.od.jtimeseries.ui.uicontext.IdentifiableListActionModel;
import com.od.jtimeseries.ui.util.ImageUtils;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 16/03/11
 * Time: 07:05
 */
public class ShowHiddenVisualizerAction extends AbstractShowHiddenPeerAction {

    public ShowHiddenVisualizerAction(IdentifiableListActionModel actionModel) {
        super(actionModel, "Show Visualizer", ImageUtils.VISUALIZER_SHOW_16x16, VisualizerContext.class);
        super.putValue(SHORT_DESCRIPTION, "Restore the selected visualizer to the desktop pane");
    }
}
