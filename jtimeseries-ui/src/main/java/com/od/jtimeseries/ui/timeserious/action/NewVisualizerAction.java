package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.timeserious.action.DesktopSelectionActionModel;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.swing.action.ModelDrivenAction;

import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 26-Mar-2010
 * Time: 16:32:43
 */
public class NewVisualizerAction extends ModelDrivenAction<DesktopSelectionActionModel> {

    public NewVisualizerAction(DesktopSelectionActionModel m) {
        super(m, "New Visualizer", ImageUtils.FRAME_ICON_16x16);
        super.putValue(SHORT_DESCRIPTION, "Create a new chart visualizer in current desktop");
    }

    public void actionPerformed(ActionEvent e) {
        if ( getActionModel().isDesktopSelected()) {
            getActionModel().getDesktop().createAndAddVisualizer();
        }
    }
}
