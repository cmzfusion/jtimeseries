package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.swing.action.ModelDrivenAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 26-Mar-2010
 * Time: 16:32:43
 */
public class NewVisualizerAction extends ModelDrivenAction<DesktopSelectionActionModel> {

    public NewVisualizerAction(DesktopSelectionActionModel m) {
        super(m, "New Visualizer", ImageUtils.SERIES_ICON_16x16);
    }

    public void actionPerformed(ActionEvent e) {
        if ( getActionModel().isDesktopSelected()) {
            getActionModel().getDesktop().createAndAddVisualizer();
        }
    }
}
