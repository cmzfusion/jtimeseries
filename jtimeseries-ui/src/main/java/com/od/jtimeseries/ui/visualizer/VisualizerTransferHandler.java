package com.od.jtimeseries.ui.visualizer;

import com.od.jtimeseries.ui.selector.shared.IdentifiableListActionModel;
import com.od.jtimeseries.ui.selector.shared.IdentifiableTransferable;
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
        boolean result = supp.isDataFlavorSupported(IdentifiableTransferable.LIST_OF_IDENTIFIABLE_FLAVOR);
        if ( result  ) {
            List<Identifiable> transferData;
            try {
                transferData = (List<Identifiable>)supp.getTransferable().getTransferData(IdentifiableTransferable.LIST_OF_IDENTIFIABLE_FLAVOR);
                AbstractUIRootContext rootContext = getRootContext();
                result = rootContext.canImport(transferData, rootContext);
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
        try {
            data = (List<Identifiable>)t.getTransferData(IdentifiableTransferable.LIST_OF_IDENTIFIABLE_FLAVOR);
        } catch (Exception e) {
            e.printStackTrace();
        }

        doImport(data, getRootContext());
        return true;
    }

    protected void doImport(List<Identifiable> data, Identifiable target) {
        getRootContext().doImport(data, target);
    }
}
