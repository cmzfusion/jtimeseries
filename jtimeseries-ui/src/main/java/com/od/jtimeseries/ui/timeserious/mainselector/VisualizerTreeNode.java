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
package com.od.jtimeseries.ui.timeserious.mainselector;

import com.od.jtimeseries.ui.identifiable.DesktopContext;
import com.od.jtimeseries.ui.identifiable.VisualizerContext;
import com.od.jtimeseries.ui.selector.tree.AbstractSeriesSelectionTreeNode;
import com.od.jtimeseries.ui.util.ImageUtils;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 11/03/11
 * Time: 12:18
 */
public class VisualizerTreeNode  extends AbstractSeriesSelectionTreeNode {

    private VisualizerContext identifiable;

    public VisualizerTreeNode(VisualizerContext identifiable) {
        this.identifiable = identifiable;
    }

    public String toString() {
        return identifiable.toString();
    }

    public VisualizerContext getIdentifiable() {
        return identifiable;
    }

    protected Icon getIcon() {
        return identifiable.isShown() ? ImageUtils.VISUALIZER_16x16 : ImageUtils.VISUALIZER_HIDDEN_16x16;
    }
}

