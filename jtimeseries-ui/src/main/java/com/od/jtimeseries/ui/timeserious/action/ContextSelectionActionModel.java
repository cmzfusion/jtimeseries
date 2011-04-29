package com.od.jtimeseries.ui.timeserious.action;

import com.od.swing.action.AbstractActionModel;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 29/04/11
 * Time: 09:22
 * To change this template use File | Settings | File Templates.
 */
public class ContextSelectionActionModel<E> extends AbstractActionModel {
    private E selectedContext;

    public E getSelectedContext() {
        return selectedContext;
    }

    public void setSelectedContext(E selectedContext) {
        this.selectedContext = selectedContext;
        setModelValid(selectedContext != null);
    }

    protected void doClearActionModelState() {
        selectedContext = null;
    }

    public boolean isContextSelected() {
        return selectedContext != null;
    }
}
