package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.selector.shared.IdentifiableListActionModel;
import com.od.jtimeseries.ui.timeserious.HideablePeerContext;
import com.od.swing.action.ModelDrivenAction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 28/04/11
 * Time: 15:57
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractShowHiddenPeerAction extends ModelDrivenAction<IdentifiableListActionModel> {

    private Class<? extends HideablePeerContext> hideableClass;

    public AbstractShowHiddenPeerAction(IdentifiableListActionModel actionModel, String name, ImageIcon imageIcon, Class<? extends HideablePeerContext> hideableClass) {
        super(actionModel, name, imageIcon);
        this.hideableClass = hideableClass;
    }

    public boolean isModelStateActionable() {
        boolean result = getActionModel().isSelectionLimitedToType(hideableClass);
        if ( result ) {
            List<? extends HideablePeerContext> nodes = getActionModel().getSelected(hideableClass);
            for ( final HideablePeerContext n : nodes ) {
                result &= n.isHidden();
            }
        }
        return result;
    }

    public void actionPerformed(ActionEvent e) {
        List<? extends HideablePeerContext> nodes = getActionModel().getSelected(hideableClass);
        for ( final HideablePeerContext n : nodes ) {
            if ( n.isHidden() ) {
                n.setShown(true);
            }
        }
    }

}
