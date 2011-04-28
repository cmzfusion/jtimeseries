package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.selector.shared.IdentifiableListActionModel;
import com.od.jtimeseries.ui.timeserious.DesktopContext;
import com.od.jtimeseries.ui.timeserious.HideablePeerContext;
import com.od.swing.action.ModelDrivenAction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 28/04/11
 * Time: 16:03
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractRemoveHideablePeerAction extends ModelDrivenAction<IdentifiableListActionModel> {

    private Class<? extends HideablePeerContext> hideablePeerClass;

    public AbstractRemoveHideablePeerAction(IdentifiableListActionModel actionModel, String name, ImageIcon imageIcon, Class<? extends HideablePeerContext> hideablePeerClass) {
        super(actionModel, name, imageIcon);
        this.hideablePeerClass = hideablePeerClass;
    }

    public boolean isModelStateActionable() {
        return getActionModel().isSelectionLimitedToType(hideablePeerClass);
    }

    public void actionPerformed(ActionEvent e) {
        List<? extends HideablePeerContext> nodes = getActionModel().getSelected(hideablePeerClass);
        for ( final HideablePeerContext n : nodes ) {
            n.setShown(false);
            n.getParent().removeChild(n);
        }
    }


}
