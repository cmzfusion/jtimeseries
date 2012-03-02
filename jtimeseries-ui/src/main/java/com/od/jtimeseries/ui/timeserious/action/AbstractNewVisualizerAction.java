package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.config.VisualizerConfiguration;
import com.od.jtimeseries.ui.identifiable.DesktopContext;
import com.od.jtimeseries.ui.identifiable.VisualizerContext;
import com.od.jtimeseries.ui.util.ContextNameCheckUtility;
import com.od.swing.action.ActionModel;
import com.od.swing.action.ModelDrivenAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 02/03/12
 * Time: 11:18
 */
public abstract class AbstractNewVisualizerAction<M extends ActionModel> extends ModelDrivenAction<M> {

    private M actionModel;
    protected Component parentComponent;
    protected VisualizerSelectionActionModel visualizerSelectionActionModel;

    public AbstractNewVisualizerAction(M actionModel, String name, ImageIcon imageIcon, Component parentComponent, VisualizerSelectionActionModel visualizerSelectionActionModel) {
        super(actionModel, name, imageIcon);
        this.actionModel = actionModel;
        this.parentComponent = parentComponent;
        this.visualizerSelectionActionModel = visualizerSelectionActionModel;
    }

    public void actionPerformed(ActionEvent e) {
        DesktopContext desktopContext = getSelectedDesktop();
        String name = ContextNameCheckUtility.getNameFromUser(
                parentComponent, desktopContext, "Name for Visualizer?", "Choose Name for Visualizer", ""
        );

        if ( name != null) { //check if user cancelled
            VisualizerConfiguration c = new VisualizerConfiguration(name);
            VisualizerContext visualizerContext = desktopContext.create(c.getTitle(), c.getTitle(), VisualizerContext.class, c);
            visualizerSelectionActionModel.setSelectedContext(visualizerContext);
        }
    }

    protected abstract DesktopContext getSelectedDesktop();

    public M getActionModel() {
        return actionModel;
    }
}
