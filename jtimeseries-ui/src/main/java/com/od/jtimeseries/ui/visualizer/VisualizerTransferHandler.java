package com.od.jtimeseries.ui.visualizer;

import com.od.jtimeseries.ui.selector.shared.IdentifiableListActionModel;
import com.od.jtimeseries.ui.selector.shared.IdentifiableTransferable;
import com.od.jtimeseries.ui.selector.shared.LocalSelectionsTransferData;
import com.od.jtimeseries.ui.selector.shared.NoImportsSelectorTransferHandler;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

import java.awt.datatransfer.Transferable;
import java.util.List;

/**
* Created by IntelliJ IDEA.
* User: Nick
* Date: 30/04/11
* Time: 19:03
*
*/
class VisualizerTransferHandler extends NoImportsSelectorTransferHandler {

    private static final LogMethods logMethods = LogUtils.getLogMethods(VisualizerTransferHandler.class);

    public VisualizerTransferHandler(AbstractUIRootContext rootContext, IdentifiableListActionModel selectionsActionModel) {
        super(rootContext, selectionsActionModel);
    }

    public boolean canImport(TransferSupport supp) {
        boolean result = supp.isDataFlavorSupported(IdentifiableTransferable.LOCAL_SELECTIONS_FLAVOR);
        if ( result  ) {
            LocalSelectionsTransferData transferData;
            try {
                transferData = (LocalSelectionsTransferData)supp.getTransferable().getTransferData(IdentifiableTransferable.LOCAL_SELECTIONS_FLAVOR);
                AbstractUIRootContext rootContext = getRootContext();
                result = rootContext.canImport(transferData.getSelected(), rootContext);
             } catch (Exception e) {
               logMethods.logError("Failed during canImport", e);
            }
        }
        return result;
    }

    public boolean importData(TransferSupport supp) {
        if (!canImport(supp)) {
            return false;
        }

        // Fetch the Transferable and its data
        Transferable t = supp.getTransferable();
        List<Identifiable> data = null;
        LocalSelectionsTransferData transferData = null;
        try {
            transferData = (LocalSelectionsTransferData) t.getTransferData(IdentifiableTransferable.LOCAL_SELECTIONS_FLAVOR);
            data = transferData.getSelected();
        } catch (Exception e) {
            e.printStackTrace();
        }

        doImport(data, getRootContext());
        int action = supp.isDrop() ? supp.getDropAction() : transferData.getAction();
        transferData.getTransferListener().transferComplete(transferData, action);
        return true;
    }

    protected void doImport(List<Identifiable> data, Identifiable target) {
        getRootContext().doImport(data, target);
    }
}
