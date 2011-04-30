package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.ui.config.DesktopConfiguration;
import com.od.jtimeseries.ui.config.ExportableConfig;
import com.od.jtimeseries.ui.config.VisualizerConfiguration;
import com.od.jtimeseries.ui.config.ConfigManagerForTimeSerious;
import com.od.jtimeseries.ui.util.CascadeLocationCalculator;
import od.configutil.ConfigManager;
import od.configutil.ConfigManagerException;
import od.configutil.FileSource;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 21/03/11
 * Time: 17:08
 */
public class DesktopPaneTransferHandler extends TransferHandler {

    private static final int DESKTOP_IMPORT_WIDTH = 800;
    private static final int DESKTOP_IMPORT_HEIGHT = 600;

    private Map<Class,ImportHandler> importHandlers = new HashMap<Class, ImportHandler>();
    private TimeSeriesContext desktopContainingContext;
    private DesktopContext desktopContext;
    private JDesktopPane desktopPane;
    private JFrame parentFrame;
    private ContextNameCheckUtility desktopNameCheckUtility;
    private ContextNameCheckUtility visualizerNameCheckUtility;
    private CascadeLocationCalculator cascadeLocationCalculator = new CascadeLocationCalculator(50, 50);
    private Rectangle lastVisualizerImportLocation;
    private Rectangle lastDesktopImportLocation;


    public DesktopPaneTransferHandler(TimeSeriousRootContext desktopContainingContext, DesktopContext desktopContext, JDesktopPane desktopPane, JFrame parentFrame) {
        this.desktopContainingContext = desktopContainingContext;
        this.desktopContext = desktopContext;
        this.desktopPane = desktopPane;
        this.parentFrame = parentFrame;
        this.desktopNameCheckUtility = new ContextNameCheckUtility(parentFrame, desktopContainingContext);
        this.visualizerNameCheckUtility = new ContextNameCheckUtility(parentFrame, desktopContext);

        importHandlers.put(VisualizerConfiguration.class, new VisualizerImportHandler());
        importHandlers.put(DesktopConfiguration.class, new DesktopImportHandler());
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
                lastVisualizerImportLocation = desktopPane.getVisibleRect();
                lastDesktopImportLocation = parentFrame.getBounds();
                for (Object file : l) {
                    checkNameAndImport(cm, (File) file);
                }
            } catch (UnsupportedFlavorException e) {
                return false;
            } catch (IOException e) {
                return false;
            }

            return true;
        }

    private void checkNameAndImport(ConfigManager cm, File f) {
        try {
            ExportableConfig uiConfig = cm.loadConfig("fromFile", ExportableConfig.class, new FileSource((File)f));
            ImportHandler i =  importHandlers.get(uiConfig.getClass());
            if ( i != null) {
                i.doImport(uiConfig);
            }
        } catch (ConfigManagerException e) {
            e.printStackTrace();
        }
    }

    private abstract class ImportHandler<E> {

        public void doImport(ExportableConfig e) {
            boolean userCancelled = findUniqueName(e);
            if ( ! userCancelled) {
                handleImport((E)e);
            }
        }

        private boolean findUniqueName(ExportableConfig uiConfig) {
            String title = uiConfig.getTitle();
            ContextNameCheckUtility cu = getNameCheckUtility(uiConfig);
            title = cu.checkName(title);
            boolean userCancelledImport = title == null;
            if ( ! userCancelledImport) {
                uiConfig.setTitle(title); //update the config to reflect the updated name
            }
            return userCancelledImport;
        }

        protected abstract ContextNameCheckUtility getNameCheckUtility(ExportableConfig uiConfig);

        protected abstract void handleImport(E config);
    }

    private class DesktopImportHandler extends ImportHandler<DesktopConfiguration> {

        protected ContextNameCheckUtility getNameCheckUtility(ExportableConfig uiConfig) {
            return desktopNameCheckUtility;
        }

        protected void handleImport(DesktopConfiguration c) {
            c.setShown(true); //always show on import
            c.setFrameExtendedState(JFrame.NORMAL);
            lastDesktopImportLocation = cascadeLocationCalculator.getNextLocation(lastDesktopImportLocation, parentFrame.getBounds(), DESKTOP_IMPORT_WIDTH, DESKTOP_IMPORT_HEIGHT);
            c.setFrameLocation(lastDesktopImportLocation);
            desktopContainingContext.addChild(new DesktopContext(c));
        }
    }

     private class VisualizerImportHandler extends ImportHandler<VisualizerConfiguration> {

        protected ContextNameCheckUtility getNameCheckUtility(ExportableConfig uiConfig) {
            return visualizerNameCheckUtility;
        }

        protected void handleImport(VisualizerConfiguration c) {
            c.setShown(true);  //always show on import
            c.setIsIcon(false);
            lastVisualizerImportLocation = cascadeLocationCalculator.getNextLocation(lastVisualizerImportLocation, desktopPane.getVisibleRect(), c.getFrameLocation().width, c.getFrameLocation().height);
            c.setFrameLocation(lastVisualizerImportLocation);
            desktopContext.addChild(new VisualizerContext(c));
        }
    }
}
