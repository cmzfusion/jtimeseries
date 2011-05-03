package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.config.ExportableConfig;
import com.od.jtimeseries.ui.selector.shared.ImportFileList;
import com.od.jtimeseries.ui.visualizer.AbstractUIRootContext;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 21/03/11
 * Time: 17:08
 */
public class DesktopPaneTransferHandler extends TransferHandler {

    private static final LogMethods logMethods = LogUtils.getLogMethods(DesktopPaneTransferHandler.class);

    private AbstractUIRootContext rootContext;
    private DesktopContext desktopContext;
    private JDesktopPane desktopPane;

    public DesktopPaneTransferHandler(AbstractUIRootContext rootContext, DesktopContext desktopContext, JDesktopPane desktopPane) {
        this.rootContext = rootContext;
        this.desktopContext = desktopContext;
        this.desktopPane = desktopPane;
    }

    public boolean canImport(TransferHandler.TransferSupport support) {
        boolean result = false;
        if (support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            try {
                result = rootContext.canImportFromExternalConfig(desktopPane, desktopContext);
            } catch (Throwable t) {
                logMethods.logError("Error on canImportFromExternalConfig", t);
            }
        }
        support.setDropAction(COPY);
        return result;
    }

    public boolean importData(TransferHandler.TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }

        try {
            java.util.List<File> l = (java.util.List<File>)support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
            java.util.List<ExportableConfig> configs = ImportFileList.getConfigs(l);
            rootContext.doImport(desktopPane, configs, desktopContext);
        } catch (Throwable t) {
            logMethods.logError("Error on canImportFromExternalConfig", t);
        }
        return true;
    }
}
