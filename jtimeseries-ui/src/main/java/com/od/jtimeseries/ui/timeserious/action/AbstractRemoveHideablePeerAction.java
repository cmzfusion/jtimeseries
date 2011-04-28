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
public abstract class AbstractRemoveHideablePeerAction<E extends HideablePeerContext> extends ModelDrivenAction<IdentifiableListActionModel> {

    private Class<E> hideablePeerClass;

    public AbstractRemoveHideablePeerAction(IdentifiableListActionModel actionModel, String name, ImageIcon imageIcon, Class<E> hideablePeerClass) {
        super(actionModel, name, imageIcon);
        this.hideablePeerClass = hideablePeerClass;
    }

    public boolean isModelStateActionable() {
        return getActionModel().isSelectionLimitedToType(hideablePeerClass);
    }

    public void actionPerformed(ActionEvent e) {
        List<E> nodes = getActionModel().getSelected(hideablePeerClass);
        for ( final E n : nodes ) {
            if ( confirmRemove(n) ) {
                n.setShown(false);
                n.getParent().removeChild(n);
            }
        }
    }

    protected boolean confirmRemove(E peerContext) {
        return true;
    }

}
