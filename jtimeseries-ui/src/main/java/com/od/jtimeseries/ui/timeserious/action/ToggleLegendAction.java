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
public class ToggleLegendAction extends AbstractArrangeInternalFrameAction {

    private TimeSeriousDesktopPane desktopPane;

    public ToggleLegendAction(TimeSeriousDesktopPane desktopPane) {
        super("Toggle Legend", ImageUtils.TOGGLE_LEGEND_ICON_16x16);
        super.putValue(SHORT_DESCRIPTION, "Toggle show or hide legend for the visualizer windows in this desktop");
        this.desktopPane = desktopPane;
    }

    public void actionPerformed(ActionEvent e) {
        showHideLegend(desktopPane);
    }

    private void showHideLegend(JDesktopPane desk) {
           // How many frames do we have?
        JInternalFrame[] allframes = desk.getAllFrames();
        int count = allframes.length;
        if (count == 0) return;


        Boolean show = null;

        // Iterate over the frames, making sure the show legend state is consistent
        for (JInternalFrame f : allframes ) {
            if ( f instanceof VisualizerInternalFrame ) {
                VisualizerInternalFrame frame = (VisualizerInternalFrame) f;
                if ( show == null) {
                    //take the new state to be the inverse of the state for the first frame in the desktop
                    show = ! frame.isShowLegendOnChart();
                }
                frame.setShowLegendOnChart(show);
            }
        }
    }
}
