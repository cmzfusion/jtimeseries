package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.selector.shared.IdentifiableListActionModel;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.timeserious.VisualizerInternalFrame;
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
            name += " " + visualizerSelectionActionModel.getSelectedVisualizer().getTitle();
        }
        putValue(NAME, name);
    }

    public void actionPerformed(ActionEvent e) {
        java.util.List<UIPropertiesTimeSeries> selectedSeries = getActionModel().getSelected(UIPropertiesTimeSeries.class);

        VisualizerInternalFrame v = visualizerSelectionActionModel.getSelectedVisualizer();
        if ( v != null) {
            v.getVisualizer().addTimeSeries(selectedSeries);
        }
    }

    public boolean isModelStateActionable() {
        return getActionModel().isSelectionLimitedToType(UIPropertiesTimeSeries.class) && visualizerSelectionActionModel.isModelValid();
    }
}
