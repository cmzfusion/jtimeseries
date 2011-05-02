package com.od.jtimeseries.ui.visualizer;

import com.od.jtimeseries.ui.selector.shared.IdentifiableListActionModel;
import com.od.jtimeseries.ui.selector.shared.IdentifiableTransferable;
import com.od.jtimeseries.ui.selector.shared.LocalSelectionsTransferData;
import com.od.jtimeseries.ui.selector.shared.NoImportsSelectorTransferHandler;
import com.od.jtimeseries.ui.selector.tree.AbstractSeriesSelectionTreeNode;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.datatransfer.Transferable;
import java.util.List;

/**
* Created by IntelliJ IDEA.
* User: Nick
* Date: 30/04/11
* Time: 19:03
*
*/
public class ImportExportTransferHandler extends NoImportsSelectorTransferHandler {

    private static final LogMethods logMethods = LogUtils.getLogMethods(ImportExportTransferHandler.class);

    public ImportExportTransferHandler(AbstractUIRootContext rootContext, IdentifiableListActionModel selectionsActionModel) {
        super(rootContext, selectionsActionModel);
    }

    public boolean canImport(TransferSupport supp) {
        boolean result = supp.isDataFlavorSupported(IdentifiableTransferable.LOCAL_SELECTIONS_FLAVOR);
        if ( result  ) {
            LocalSelectionsTransferData transferData;
            try {
                transferData = (LocalSelectionsTransferData)supp.getTransferable().getTransferData(IdentifiableTransferable.LOCAL_SELECTIONS_FLAVOR);
                Identifiable target = getTargetIdentifiableForDropOrPaste(supp);
                result = getRootContext().canImport(transferData.getSelected(), target);
                System.out.println("transfer canImport : " + target + ": " + result);
             } catch (Throwable t) {
               logMethods.logError("Failed during canImport", t);
            }
        }
        return result;
    }

    public boolean importData(TransferSupport supp) {
        try {
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

            Identifiable target = getTargetIdentifiableForDropOrPaste(supp);
            doImport(data, target);
            int action = supp.isDrop() ? supp.getDropAction() : transferData.getAction();
            transferData.getTransferListener().transferComplete(transferData, action);
        } catch (Throwable t) {
            logMethods.logError("Error in importData", t);
        }
        return true;
    }

    private Identifiable getTargetIdentifiableForDropOrPaste(TransferSupport supp) {
        Identifiable result = getRootContext();
        if ( supp.isDrop()) {
            DropLocation l = supp.getDropLocation();
            if ( l instanceof JTree.DropLocation ) {
                //TODO, implement for table
                //not that important right now since table
                //contains only series which are not supported
                //targets
                TreePath path = ((JTree.DropLocation) l).getPath();
                if ( path != null) {
                    Object c = path.getLastPathComponent();
                    if ( c instanceof AbstractSeriesSelectionTreeNode) {
                        result = ((AbstractSeriesSelectionTreeNode)c).getIdentifiable();
                    }
                }
            }
        } else {  // this is a paste action / ctrl + v?
            List<Identifiable> l = getSelectionsModel().getSelected();
            if ( l.size() > 0) {
                return l.get(0);
            }
        }
        return result;
    }

    protected void doImport(List<Identifiable> data, Identifiable target) {
        getRootContext().doImport(data, target);
    }
}
