package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.config.ExportableConfig;
import com.od.jtimeseries.ui.config.VisualizerConfiguration;
import com.od.jtimeseries.ui.event.TimeSeriousBusListener;
import com.od.jtimeseries.ui.config.ConfigManagerForTimeSerious;
import com.od.swing.eventbus.EventSender;
import com.od.swing.eventbus.UIEventBus;
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

    private DesktopContext desktopContext;
    private ContextNameCheckUtility nameCheckUtility;

    public DesktopPaneTransferHandler(DesktopContext desktopContext, ContextNameCheckUtility nameCheckUtility) {
        this.desktopContext = desktopContext;
        this.nameCheckUtility = nameCheckUtility;
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
                        ExportableConfig uiConfig = cm.loadConfig("fromFile", ExportableConfig.class, new FileSource((File)f));  if ( uiConfig instanceof VisualizerConfiguration) {
                            final VisualizerConfiguration c = (VisualizerConfiguration)uiConfig;
                            c.setShown(true); //always show on import
                            String title = c.getChartsTitle();
                            title = nameCheckUtility.checkName(title);
                            if ( title != null) {  //check user cancelled
                                c.setChartsTitle(title); //update the config to reflect the checked name
                                desktopContext.addChild(new VisualizerContext(c));
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
