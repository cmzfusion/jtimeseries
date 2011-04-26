package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.selector.shared.IdentifiableListActionModel;
import com.od.jtimeseries.ui.timeserious.DesktopContext;
import com.od.jtimeseries.ui.timeserious.VisualizerContext;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.swing.action.ModelDrivenAction;

import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18/03/11
 * Time: 06:44
 */
public class RemoveVisualizerAction extends ModelDrivenAction<IdentifiableListActionModel> {

    public RemoveVisualizerAction(IdentifiableListActionModel selectionModel) {
        super(selectionModel, "Remove Visualizer", ImageUtils.VISUALIZER_DELETE_16x16);
        super.putValue(SHORT_DESCRIPTION, "Remove the selected visualizer");
    }

     public boolean isModelStateActionable() {
        return getActionModel().isSelectionLimitedToType(VisualizerContext.class);
    }

    public void actionPerformed(ActionEvent e) {
        List<VisualizerContext> nodes = getActionModel().getSelected(VisualizerContext.class);
        for ( final VisualizerContext n : nodes ) {
            n.setShown(false);
            n.getParent().removeChild(n);
        }
    }
}
