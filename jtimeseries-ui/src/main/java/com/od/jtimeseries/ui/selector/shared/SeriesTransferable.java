package com.od.jtimeseries.ui.selector.shared;

import com.od.jtimeseries.ui.config.ExportableConfig;
import com.od.jtimeseries.ui.config.ExportableConfigHolder;
import com.od.jtimeseries.ui.timeserious.DesktopContext;
import com.od.jtimeseries.ui.timeserious.VisualizerContext;
import com.od.jtimeseries.ui.config.ConfigManagerForTimeSerious;
import com.od.jtimeseries.ui.config.VisualizerConfiguration;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;
import od.configutil.ConfigManager;
import od.configutil.ConfigManagerException;
import od.configutil.FileSink;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 08/01/11
 * Time: 09:41
 * To change this template use File | Settings | File Templates.
 */
public class SeriesTransferable implements Transferable {

    private static LogMethods logMethods = LogUtils.getLogMethods(SeriesTransferable.class);

    private static final String listType = DataFlavor.javaJVMLocalObjectMimeType +
                       ";class=java.util.List";
    public static DataFlavor LIST_OF_IDENTIFIABLE_FLAVOR;

    static {
        try {
            LIST_OF_IDENTIFIABLE_FLAVOR = new DataFlavor(listType);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private IdentifiableListActionModel selectionsModel;

    public SeriesTransferable(IdentifiableListActionModel selectionsModel) {
        this.selectionsModel = selectionsModel;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return ( selectionsModel.isSelectionLimitedToType(ExportableConfigHolder.class)) ?
         new DataFlavor[] { DataFlavor.javaFileListFlavor } :
         new DataFlavor[] { LIST_OF_IDENTIFIABLE_FLAVOR };
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        boolean result = false;
        if (flavor.equals(LIST_OF_IDENTIFIABLE_FLAVOR)) {
            result = true;
        } else if ( flavor.equals(DataFlavor.javaFileListFlavor )) {
            result = selectionsModel.isSelectionLimitedToType(DesktopContext.class);
        }
        return result;
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        Object result = null;
        if ( flavor == LIST_OF_IDENTIFIABLE_FLAVOR) {
            result = selectionsModel.getSelected();
        } else if ( flavor == DataFlavor.javaFileListFlavor ) {
            //System.out.println("Creating file list");
            List<ExportableConfigHolder> visualizerContexts = selectionsModel.getSelected(ExportableConfigHolder.class);
            ConfigManager m = new ConfigManagerForTimeSerious();
            List<File> files = new LinkedList<File>();
            for (ExportableConfigHolder n : visualizerContexts) {
                ExportableConfig c = n.getExportableConfig();
                String encodedTitle = URLEncoder.encode(n.getDefaultFileName(), "UTF-8");

                File tmpDir = new File(System.getProperty("java.io.tmpdir"));
                File tmpFile = new File(tmpDir, encodedTitle + ".xml");

                try {
                    m.saveConfig("exportableConfig", c, new FileSink(tmpFile));
                    files.add(tmpFile);
                    tmpFile.deleteOnExit();
                } catch (ConfigManagerException e) {
                    logMethods.logError("Failed to write temporary exportable config", e);
                }
            }
            result = files;
        }
        return result;
    }
}
