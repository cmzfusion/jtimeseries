package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.identifiable.HidablePeerContext;
import com.od.jtimeseries.ui.selector.shared.IdentifiableListActionModel;

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
public abstract class AbstractRemoveHideablePeerAction<E extends HidablePeerContext> extends AbstractTimeSeriousIdentifiableAction {

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
        if ( confirmRemove(nodes) ) {
            for ( final E n : nodes ) {
                n.getParent().removeChild(n);
            }
        }
    }

    protected boolean confirmRemove(List<E> nodes) {
        return true;
    }

}
