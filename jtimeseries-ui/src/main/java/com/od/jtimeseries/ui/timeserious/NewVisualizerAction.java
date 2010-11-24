package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.util.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 26-Mar-2010
 * Time: 16:32:43
 */
public class NewVisualizerAction extends AbstractAction {

    private TimeSeriousDesktop desktopPane;

    public NewVisualizerAction(TimeSeriousDesktop desktopPane) {
        super("New Visualizer", ImageUtils.SERIES_ICON_16x16);
        this.desktopPane = desktopPane;
    }

    public void actionPerformed(ActionEvent e) {
        desktopPane.createAndAddVisualizer();
    }
}
