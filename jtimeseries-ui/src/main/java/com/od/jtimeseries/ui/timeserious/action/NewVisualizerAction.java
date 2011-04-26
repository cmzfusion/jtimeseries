package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.config.VisualizerConfiguration;
import com.od.jtimeseries.ui.timeserious.ContextNameCheckUtility;
import com.od.jtimeseries.ui.timeserious.TimeSeriousDesktopPane;
import com.od.jtimeseries.ui.timeserious.VisualizerContext;
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

    public NewVisualizerAction(JFrame mainFrame, DesktopSelectionActionModel m) {
        super(m, "New Visualizer", ImageUtils.VISUALIZER_NEW_16x16);
        this.mainFrame = mainFrame;
        super.putValue(SHORT_DESCRIPTION, "Create a new chart visualizer in current desktop");
    }

    public void actionPerformed(ActionEvent e) {
        if ( getActionModel().isDesktopSelected()) {
            TimeSeriousDesktopPane desktop = getActionModel().getDesktop();
            String name = ContextNameCheckUtility.getNameFromUser(
                mainFrame, "Name for visualizer?", "Choose Name", ""
            );
            name = desktop.getNameCheckUtility().checkName(name);
            VisualizerConfiguration c = new VisualizerConfiguration(name);
            VisualizerContext node = new VisualizerContext(c);
            node.setShown(true);
            desktop.getDesktopContext().addChild(node);
        }
    }
}
