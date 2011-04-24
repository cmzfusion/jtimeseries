package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.selector.shared.IdentifiableListActionModel;
import com.od.jtimeseries.ui.timeserious.VisualizerContext;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.swing.action.ModelDrivenAction;

import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 16/03/11
 * Time: 07:05
 */
public class ShowHiddenVisualizerAction extends ModelDrivenAction<IdentifiableListActionModel> {

    public ShowHiddenVisualizerAction(IdentifiableListActionModel actionModel) {
        super(actionModel, "Show Visualizer", ImageUtils.SERIES_ICON_16x16);
        super.putValue(SHORT_DESCRIPTION, "Restore the selected visualizer to the desktop pane");
    }

    public boolean isModelStateActionable() {
        boolean result = getActionModel().isSelectionLimitedToType(VisualizerContext.class);
        if ( result ) {
            List<VisualizerContext> nodes = getActionModel().getSelected(VisualizerContext.class);
            for ( final VisualizerContext n : nodes ) {
                result &= n.isHidden();
            }
        }
        return result;
    }

    public void actionPerformed(ActionEvent e) {
        List<VisualizerContext> nodes = getActionModel().getSelected(VisualizerContext.class);
        for ( final VisualizerContext n : nodes ) {
            if ( n.isHidden() ) {
                n.setShown(true);
            }
        }
    }

}
