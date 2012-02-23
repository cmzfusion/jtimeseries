package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.identifiable.Identifiable;
import com.od.jtimeseries.ui.identifiable.HidablePeerContext;
import com.od.jtimeseries.ui.uicontext.IdentifiableListActionModel;
import com.od.swing.action.ModelDrivenAction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 21/02/12
 * Time: 18:38
 */
public abstract class CheckAllSelectedActionableAction<E extends Identifiable> extends AbstractTimeSeriousIdentifiableAction {

    private Class<E> clazz;

    public CheckAllSelectedActionableAction(IdentifiableListActionModel actionModel, String name, ImageIcon imageIcon, Class<E> clazz) {
        super(actionModel, name, imageIcon);
        this.clazz = clazz;
    }

    public boolean isModelStateActionable() {
        boolean result = getActionModel().isSelectionLimitedToTypes(clazz);
        if ( result ) {
            List<E> nodes = getActionModel().getSelected(clazz);
            for ( final E n : nodes ) {
                if (! isActionable(n)) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    protected abstract boolean isActionable(E n);

    public void actionPerformed(ActionEvent e) {
        List<E> nodes = getActionModel().getSelected(clazz);
        for ( final E n : nodes ) {
            doAction(n);
        }
    }

    protected abstract void doAction(E n);
}
