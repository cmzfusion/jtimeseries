package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.ui.config.DesktopConfiguration;
import com.od.jtimeseries.ui.config.ExportableConfig;
import com.od.jtimeseries.ui.config.VisualizerConfiguration;
import com.od.jtimeseries.ui.config.ConfigManagerForTimeSerious;
import od.configutil.ConfigManager;
import od.configutil.ConfigManagerException;
import od.configutil.FileSource;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 21/03/11
 * Time: 17:08
 */
public class DesktopPaneTransferHandler extends TransferHandler {

    private TimeSeriesContext desktopContainingContext;
    private DesktopContext desktopContext;
    private ContextNameCheckUtility desktopNameCheckUtility;
    private ContextNameCheckUtility visualizerNameCheckUtility;

    public DesktopPaneTransferHandler(TimeSeriousRootContext desktopContainingContext, DesktopContext desktopContext, JFrame parentFrame) {
        this.desktopContainingContext = desktopContainingContext;
        this.desktopContext = desktopContext;
        this.desktopNameCheckUtility = new ContextNameCheckUtility(parentFrame, desktopContainingContext);
        this.visualizerNameCheckUtility = new ContextNameCheckUtility(parentFrame, desktopContext);
    }

    public boolean canImport(TransferHandler.TransferSupport support) {
            //System.out.println(support.getSourceDropActions());
            if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                return false;
            }
            support.setDropAction(COPY);
            return true;
        }

        public boolean importData(TransferHandler.TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }

            Transferable t = support.getTransferable();

            try {
                java.util.List l =
                    (java.util.List)t.getTransferData(DataFlavor.javaFileListFlavor);

                ConfigManager cm = new ConfigManagerForTimeSerious();
                for (Object f : l) {
                    try {
                        ExportableConfig uiConfig = cm.loadConfig("fromFile", ExportableConfig.class, new FileSource((File)f));

                        if ( uiConfig instanceof VisualizerConfiguration || uiConfig instanceof DesktopConfiguration ) {
                            String title = uiConfig.getTitle();
                            ContextNameCheckUtility cu = uiConfig instanceof VisualizerConfiguration ? visualizerNameCheckUtility : desktopNameCheckUtility;
                            title = cu.checkName(title);
                            if ( title != null) {  //only if user did not cancel
                                uiConfig.setTitle(title); //update the config to reflect the checked name

                                if ( uiConfig instanceof VisualizerConfiguration) {
                                    VisualizerConfiguration c = (VisualizerConfiguration)uiConfig;
                                    c.setShown(true);
                                    desktopContext.addChild(new VisualizerContext(c));
                                } else if ( uiConfig instanceof DesktopConfiguration) {
                                    DesktopConfiguration c = (DesktopConfiguration)uiConfig;
                                    c.setShown(true); //always show on import
                                    desktopContainingContext.addChild(new DesktopContext(c));
                                }
                            }
                        }
                    } catch (ConfigManagerException e) {
                        e.printStackTrace();
                    }
                }
            } catch (UnsupportedFlavorException e) {
                return false;
            } catch (IOException e) {
                return false;
            }

            return true;
        }

}
