package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.selector.shared.IdentifiableListActionModel;
import com.od.jtimeseries.ui.timeserious.VisualizerContext;
import com.od.jtimeseries.ui.util.ImageUtils;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18/03/11
 * Time: 06:44
 */
public class RemoveVisualizerAction extends AbstractRemoveHideablePeerAction<VisualizerContext> {

    public RemoveVisualizerAction(IdentifiableListActionModel selectionModel) {
        super(selectionModel, "Remove Visualizer", ImageUtils.VISUALIZER_DELETE_16x16, VisualizerContext.class);
        super.putValue(SHORT_DESCRIPTION, "Remove the selected visualizer");
    }
}
