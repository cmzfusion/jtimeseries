package com.od.jtimeseries.ui.selector.shared;

import com.od.jtimeseries.ui.config.ExportableConfigHolder;
import com.od.jtimeseries.util.identifiable.Identifiable;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 01/05/11
 * Time: 16:43
 * To change this template use File | Settings | File Templates.
 */
public class LocalSelectionsTransferData {

    private IdentifiableListActionModel selections;
    private TransferListener transferListener;
    private int action;

    public LocalSelectionsTransferData(IdentifiableListActionModel selections, TransferListener transferListener) {
        this.selections = selections;
        this.transferListener = transferListener;
    }

    public boolean isSelectionLimitedToType(Class c) {
        return selections.isSelectionLimitedToType(c);
    }

    public <C extends Identifiable> List<C> getSelected(Class<C> clazz) {
        return selections.getSelected(clazz);
    }

    public List<Identifiable> getSelected() {
        return selections.getSelected();
    }

    public TransferListener getTransferListener() {
        return transferListener;
    }

    public static interface TransferListener {
        void transferComplete(LocalSelectionsTransferData d, int actionType);
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }
}
