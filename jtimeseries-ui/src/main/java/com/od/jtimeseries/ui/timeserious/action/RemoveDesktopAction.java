package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.identifiable.DesktopContext;
import com.od.jtimeseries.ui.selector.shared.IdentifiableListActionModel;
import com.od.jtimeseries.ui.util.ImageUtils;

import javax.swing.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 26/04/11
 * Time: 10:19
 * To change this template use File | Settings | File Templates.
 */
public class RemoveDesktopAction extends AbstractRemoveHideablePeerAction<DesktopContext> {

    private JComponent parentComponent;

    public RemoveDesktopAction(IdentifiableListActionModel selectionModel, JComponent parentComponent) {
        super(selectionModel, "Remove Desktop", ImageUtils.DESKTOP_DELETE_16x16, DesktopContext.class);
        this.parentComponent = parentComponent;
        super.putValue(SHORT_DESCRIPTION, "Remove the selected desktop");
    }

    public boolean isModelStateActionable() {
        boolean actionable = super.isModelStateActionable();
        actionable &= ! isMainDesktopSelected();
        return actionable;
    }

    protected boolean confirmRemove(List<DesktopContext> desktops) {
        return JOptionPane.showConfirmDialog(
            SwingUtilities.getRoot(parentComponent),
            "Remove desktop" + (desktops.size() > 1 ? "s" : "") + " and all associated visualizers?",
            "Remove Desktop" + (desktops.size() > 1 ? "s" : "") + "?",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION;
    }
}
