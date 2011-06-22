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
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.uicontext.IdentifiableListActionModel;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.swing.action.ActionModelListener;
import com.od.swing.action.ModelDrivenAction;

import java.awt.event.ActionEvent;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 16/03/11
* Time: 06:59
*/
public class AddSeriesToActiveVisualizerAction extends ModelDrivenAction<IdentifiableListActionModel> {

    private VisualizerSelectionActionModel visualizerSelectionActionModel;

    public AddSeriesToActiveVisualizerAction(VisualizerSelectionActionModel visualizerSelectionActionModel, IdentifiableListActionModel actionModel) {
        super(actionModel, "Add to Selected Visualizer", ImageUtils.VISUALIZER_ADD_TO_16x16);
        super.putValue(SHORT_DESCRIPTION, "Add series to the selected visualizer");
        this.visualizerSelectionActionModel = visualizerSelectionActionModel;
        visualizerSelectionActionModel.addActionModelListener(new ActionModelListener() {
            public void actionStateUpdated() {
               AddSeriesToActiveVisualizerAction.this.actionStateUpdated();
               setName();
            }
        });
    }

    private void setName() {
        String name = "Add to Selected Visualizer";
        if ( visualizerSelectionActionModel.isModelValid()) {
            name += " " + visualizerSelectionActionModel.getSelectedContext().getId();
        }
        putValue(NAME, name);
    }

    public void actionPerformed(ActionEvent e) {
        java.util.List<UIPropertiesTimeSeries> selectedSeries = getActionModel().getSelected(UIPropertiesTimeSeries.class);

        VisualizerContext v = visualizerSelectionActionModel.getSelectedContext();
        if ( v != null) {
            v.addTimeSeries(selectedSeries);
        }
    }

    public boolean isModelStateActionable() {
        return getActionModel().isSelectionLimitedToTypes(UIPropertiesTimeSeries.class) && visualizerSelectionActionModel.isModelValid();
    }
}
