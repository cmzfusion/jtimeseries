package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.timeserious.frame.TimeSeriousDesktopPane;
import com.od.jtimeseries.ui.util.CascadeLocationCalculator;
import com.od.jtimeseries.ui.util.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 05/07/11
 * Time: 08:56
 */
public class CascadeVisualizersAction extends AbstractArrangeInternalFrameAction {

    private TimeSeriousDesktopPane desktopPane;

    public CascadeVisualizersAction(TimeSeriousDesktopPane desktopPane) {
        super("Tile Visualizers", ImageUtils.CASCADE_VISUALIZERS_ICON_16x16);
        super.putValue(SHORT_DESCRIPTION, "Cascade visualizer windows in this desktop");
        this.desktopPane = desktopPane;
    }

    public void actionPerformed(ActionEvent e) {
        cascadeInternalFrames(desktopPane);
    }

    private void cascadeInternalFrames(JDesktopPane desk) {
        java.util.List<JInternalFrame> allframes = Arrays.asList(desk.getAllFrames());
        Collections.reverse(allframes);
        //reverse so that current z order is respected in cascade

        CascadeLocationCalculator cascadeCalculator = new CascadeLocationCalculator(20, 20);

        Rectangle lastLocation = new Rectangle(0,0,0,0);
        for ( JInternalFrame f : allframes) {
            lastLocation = cascadeCalculator.getNextLocation(lastLocation, desktopPane.getBounds(), f.getWidth(), f.getHeight());
            f.setBounds(lastLocation);
            deiconify(f);
            desktopPane.setSelectedFrame(f);
        }
    }

}

