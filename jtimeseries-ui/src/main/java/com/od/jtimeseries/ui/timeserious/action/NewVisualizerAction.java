package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.timeserious.DesktopVisualizerFactory;
import com.od.jtimeseries.ui.timeserious.TimeSeriousRootContext;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.swing.action.ModelDrivenAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 26-Mar-2010
 * Time: 16:32:43
 */
public class NewVisualizerAction extends ModelDrivenAction<DesktopSelectionActionModel> {

    private JFrame mainFrame;
    private TimeSeriousRootContext rootContext;

    public NewVisualizerAction(JFrame mainFrame, DesktopSelectionActionModel m, TimeSeriousRootContext rootContext) {
        super(m, "New Visualizer", ImageUtils.NEW_VISUALIZER_16x16);
        this.mainFrame = mainFrame;
        this.rootContext = rootContext;
        super.putValue(SHORT_DESCRIPTION, "Create a new chart visualizer in current desktop");
    }

    public void actionPerformed(ActionEvent e) {
        if ( getActionModel().isDesktopSelected()) {
            String name = DesktopVisualizerFactory.getVisualizerNameFromUser(
                    mainFrame, "Name for visualizer?", "Choose Name", ""
            );
            getActionModel().getDesktop().createNewVisualizer(name);
        }
    }
}
