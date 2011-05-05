package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.config.VisualizerConfiguration;
import com.od.jtimeseries.ui.timeserious.ContextNameCheckUtility;
import com.od.jtimeseries.ui.timeserious.DesktopContext;
import com.od.jtimeseries.ui.timeserious.VisualizerContext;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.swing.action.ModelDrivenAction;

import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 26-Mar-2010
 * Time: 16:32:43
 */
public class NewVisualizerAction extends ModelDrivenAction<DesktopSelectionActionModel> {

    private Component parentComponent;
    private VisualizerSelectionActionModel visualizerSelectionActionModel;

    public NewVisualizerAction(Component parentComponent, DesktopSelectionActionModel m, VisualizerSelectionActionModel visualizerSelectionActionModel) {
        super(m, "New Visualizer", ImageUtils.VISUALIZER_NEW_16x16);
        this.parentComponent = parentComponent;
        this.visualizerSelectionActionModel = visualizerSelectionActionModel;
        super.putValue(SHORT_DESCRIPTION, "Create a new chart visualizer in current desktop");
    }

    public void actionPerformed(ActionEvent e) {
        if ( getActionModel().isContextSelected() ) {
            DesktopContext desktopContext = getActionModel().getSelectedContext();
            String name = ContextNameCheckUtility.getNameFromUser(
                    parentComponent, desktopContext, "Name for visualizer?", "Choose Name", ""
            );
            if ( name != null) { //check if user cancelled
                VisualizerConfiguration c = new VisualizerConfiguration(name);
                VisualizerContext visualizerContext = desktopContext.create(c.getTitle(), c.getTitle(), VisualizerContext.class, c);
                visualizerSelectionActionModel.setSelectedContext(visualizerContext);
            }
        }
    }
}
