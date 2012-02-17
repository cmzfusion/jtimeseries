package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.timeserious.frame.TimeSeriousDesktopPane;
import com.od.jtimeseries.ui.timeserious.frame.VisualizerInternalFrame;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.jtimeseries.ui.visualizer.chart.creator.ChartType;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 16/02/12
 * Time: 19:35
 */
public class ToggleChartTypeAction extends AbstractArrangeInternalFrameAction {

    private TimeSeriousDesktopPane desktopPane;

    public ToggleChartTypeAction(TimeSeriousDesktopPane desktopPane) {
        super("Toggle Chart Type", ImageUtils.TOGGLE_CHART_TYPE_ICON_16x16);
        super.putValue(SHORT_DESCRIPTION, "Toggle chart type for the visualizer windows in this desktop");
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


        ChartType chartType = null;

        // Iterate over the frames, making sure the chart type is consistent
        for (JInternalFrame f : allframes ) {
            if ( f instanceof VisualizerInternalFrame ) {
                VisualizerInternalFrame frame = (VisualizerInternalFrame) f;
                if ( chartType == null) {
                    //take the new type to be the next based on the state for the first frame in the desktop
                    chartType = ChartType.nextType(frame.getChartType());
                }
                frame.setChartType(chartType);
            }
        }
    }
}
