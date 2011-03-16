package com.od.jtimeseries.ui.selector.shared;

import com.od.jtimeseries.ui.selector.shared.IdentifiableListActionModel;
import com.od.jtimeseries.ui.selector.shared.SeriesTransferable;
import com.od.jtimeseries.util.identifiable.Identifiable;

import javax.swing.*;
import java.awt.datatransfer.Transferable;
import java.util.List;

/**
* Created by IntelliJ IDEA.
* User: Nick
* Date: 08/01/11
* Time: 10:05
* To change this template use File | Settings | File Templates.
*/
public class NoImportsSelectorTransferHandler extends TransferHandler {

    private IdentifiableListActionModel selectionsModel;

    public NoImportsSelectorTransferHandler(IdentifiableListActionModel selectionsModel) {
        this.selectionsModel = selectionsModel;
    }

    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    public Transferable createTransferable(JComponent c) {
        return new SeriesTransferable(selectionsModel.getSelected());
    }

    public void exportDone(JComponent c, Transferable t, int action) {
    }

    public boolean canImport(TransferSupport supp) {
        return false;
    }

    public boolean importData(TransferSupport supp) {
        return false;
    }

    protected void doImport(List<Identifiable> data) {
    }

}
