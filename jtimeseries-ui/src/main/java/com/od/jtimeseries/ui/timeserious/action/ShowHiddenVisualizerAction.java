package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.selector.shared.IdentifiableListActionModel;
import com.od.jtimeseries.ui.timeserious.VisualizerContext;
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
