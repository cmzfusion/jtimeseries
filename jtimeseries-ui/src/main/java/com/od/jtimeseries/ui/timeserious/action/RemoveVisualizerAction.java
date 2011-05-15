package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.identifiable.VisualizerContext;
import com.od.jtimeseries.ui.selector.shared.IdentifiableListActionModel;
import com.od.jtimeseries.ui.util.ImageUtils;

import javax.swing.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18/03/11
 * Time: 06:44
 */
public class RemoveVisualizerAction extends AbstractRemoveHideablePeerAction<VisualizerContext> {

    private JComponent parentComponent;

    public RemoveVisualizerAction(IdentifiableListActionModel selectionModel, JComponent parentComponent) {
        super(selectionModel, "Remove Visualizer", ImageUtils.VISUALIZER_DELETE_16x16, VisualizerContext.class);
        this.parentComponent = parentComponent;
        super.putValue(SHORT_DESCRIPTION, "Remove the selected visualizer");
    }

    protected boolean confirmRemove(List<VisualizerContext> desktops) {
        return JOptionPane.showConfirmDialog(
            SwingUtilities.getRoot(parentComponent),
            "Remove visualizer" + (desktops.size() > 1 ? "s" : "") + " - are you sure?",
            "Remove Visualizer" + (desktops.size() > 1 ? "s" : "") + "?",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION;
    }
}
