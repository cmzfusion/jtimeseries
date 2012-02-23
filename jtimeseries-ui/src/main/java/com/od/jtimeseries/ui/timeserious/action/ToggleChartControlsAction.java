package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.timeserious.frame.TimeSeriousDesktopPane;
import com.od.jtimeseries.ui.timeserious.frame.VisualizerInternalFrame;
import com.od.jtimeseries.ui.util.ImageUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 16/02/12
 * Time: 19:35
 */
public class ToggleChartControlsAction extends AbstractArrangeInternalFrameAction {

    private TimeSeriousDesktopPane desktopPane;

    public ToggleChartControlsAction(TimeSeriousDesktopPane desktopPane) {
        super("Toggle Chart Controls", ImageUtils.TOGGLE_CHART_CONTROLS_ICON_16x16);
        super.putValue(SHORT_DESCRIPTION, "Toggle show or hide chart controls for visualizer windows in this desktop");
        this.desktopPane = desktopPane;
    }

    public void actionPerformed(ActionEvent e) {
        showHideControls(desktopPane);
    }

    private void showHideControls(JDesktopPane desk) {
           // How many frames do we have?
        JInternalFrame[] allframes = desk.getAllFrames();
        int count = allframes.length;
        if (count == 0) return;


        Boolean show = null;

        // Iterate over the frames, making sure the show chart controls state is consistent
        for (JInternalFrame f : allframes ) {
            if ( f instanceof VisualizerInternalFrame ) {
                VisualizerInternalFrame frame = (VisualizerInternalFrame) f;
                if ( show == null) {
                    //take the new state to be the inverse of the state for the first frame in the desktop
                    show = ! frame.isChartControlsVisible();
                }
                frame.setChartControlsVisible(show);
            }
        }
    }
}
