/**
 * Copyright (C) 2011 (nick @ objectdefinitions.com)
 *
 * This file is part of JTimeseries.
 *
 * JTimeseries is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JTimeseries is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JTimeseries.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.od.jtimeseries.ui.uicontext;

import com.od.jtimeseries.identifiable.Identifiable;
import com.od.jtimeseries.ui.config.ExportableConfig;
import com.od.jtimeseries.ui.util.IdentifiableSource;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
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
        boolean result = false;
        try {
            Identifiable target = getTargetIdentifiableForDropOrPaste(supp);

            if ( supp.isDataFlavorSupported(IdentifiableTransferable.LOCAL_SELECTIONS_FLAVOR) ) {
                LocalSelectionsTransferData transferData = (LocalSelectionsTransferData)supp.getTransferable().getTransferData(IdentifiableTransferable.LOCAL_SELECTIONS_FLAVOR);
                result = getRootContext().canImport(supp.getComponent(), transferData.getSelections(), target);
                //System.out.println("transfer canImportFromExternalConfig : " + target + ": " + result);
            } else if ( supp.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                result = getRootContext().canImportFromExternalConfig(supp.getComponent(), target);
            }
        } catch (Throwable t) {
           logMethods.error("Failed during canImportFromExternalConfig", t);
        }
        return result;
    }

    private List<ExportableConfig> getExportableConfigs(TransferSupport supp) throws UnsupportedFlavorException, IOException {
        List<File> fileList = (List<File>)supp.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
        return ImportFileList.getConfigs(fileList);
    }

    public boolean importData(TransferSupport supp) {
        try {
            if (!canImport(supp)) {
                return false;
            }

            // Fetch the Transferable and its data
            Transferable t = supp.getTransferable();
            Identifiable target = getTargetIdentifiableForDropOrPaste(supp);

            if ( supp.isDataFlavorSupported(IdentifiableTransferable.LOCAL_SELECTIONS_FLAVOR) ) {
                  LocalSelectionsTransferData transferData = (LocalSelectionsTransferData) t.getTransferData(IdentifiableTransferable.LOCAL_SELECTIONS_FLAVOR);
                  IdentifiableListActionModel data = transferData.getSelections();
                  getRootContext().doImport(supp.getComponent(), data, target);
                  //now complete the export from the source component, if this is a local paste
                  int action = supp.isDrop() ? supp.getDropAction() : transferData.getAction();
                  transferData.getTransferListener().transferComplete(transferData, action);
            } else if ( supp.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                  List<ExportableConfig> configs = getExportableConfigs(supp);
                  getRootContext().doImport(supp.getComponent(), configs, target);
            }
        } catch (Throwable t) {
            logMethods.error("Error in importData", t);
        }
        return true;
    }

    protected Identifiable getTargetIdentifiableForDropOrPaste(TransferSupport supp) {
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
                    if ( c instanceof IdentifiableSource) {
                        result = ((IdentifiableSource)c).getIdentifiable();
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

}
