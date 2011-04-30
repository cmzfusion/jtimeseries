package com.od.jtimeseries.ui.selector.shared;

import com.od.jtimeseries.ui.visualizer.AbstractUIRootContext;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

import javax.swing.*;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;

/**
* Created by IntelliJ IDEA.
* User: Nick
* Date: 08/01/11
* Time: 10:05
*
*/
public class NoImportsSelectorTransferHandler extends TransferHandler {

    private static final LogMethods logMethods = LogUtils.getLogMethods(NoImportsSelectorTransferHandler.class);

    private AbstractUIRootContext rootContext;
    private IdentifiableListActionModel selectionsModel;

    public NoImportsSelectorTransferHandler(AbstractUIRootContext rootContext, IdentifiableListActionModel selectionsModel) {
        this.rootContext = rootContext;
        this.selectionsModel = selectionsModel;
    }

    public int getSourceActions(JComponent c) {
        return rootContext.getSourceActions(selectionsModel.getSelected());
    }

    public Transferable createTransferable(JComponent c) {
        return new IdentifiableTransferable(selectionsModel);
    }

    public void exportDone(JComponent c, Transferable t, int action) {
        IdentifiableTransferable i = (IdentifiableTransferable)t;
        List<Identifiable> transferData;
        try {
            transferData = (List<Identifiable>)i.getTransferData(IdentifiableTransferable.LIST_OF_IDENTIFIABLE_FLAVOR);
            rootContext.doExport(transferData, action);
        } catch (Exception e) {
            logMethods.logError("Failed during exportDone", e);
        }
    }

    public boolean canImport(TransferSupport supp) {
        return false;
    }

    public boolean importData(TransferSupport supp) {
        return false;
    }

    protected void doImport(List<Identifiable> data, Identifiable target) {
    }

    protected AbstractUIRootContext getRootContext() {
        return rootContext;
    }
}
