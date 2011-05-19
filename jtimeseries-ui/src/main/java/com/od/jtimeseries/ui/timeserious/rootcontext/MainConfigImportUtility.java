package com.od.jtimeseries.ui.timeserious.rootcontext;

import com.od.jtimeseries.ui.config.ConfigAwareTreeManager;
import com.od.jtimeseries.ui.config.ExportableConfig;
import com.od.jtimeseries.ui.config.TimeSeriousConfig;
import com.od.jtimeseries.ui.uicontext.ImportItem;
import com.od.jtimeseries.util.identifiable.Identifiable;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 19/05/11
 * Time: 08:40
 */
public class MainConfigImportUtility extends ExportableConfigImportUtility {

    private ConfigAwareTreeManager configTreeManager;

    public MainConfigImportUtility(ConfigAwareTreeManager configTreeManager) {
        this.configTreeManager = configTreeManager;
    }

    protected void reset() {
    }

    protected ImportItem doGetImportDetails(Component component, ExportableConfig c, Identifiable target) {
        //we will handle the import ourselves, so doesn't matter greatly what we return here
        return new ImportItem("timeSerious", "timeSerious", Identifiable.class, c);
    }

    public boolean handlesOwnImport() {
        return true;
    }

    protected void doOwnImport(Component component, Identifiable target, ImportItem item) {
        int option = JOptionPane.showConfirmDialog(SwingUtilities.windowForComponent(component), "Import Config, lose current workspace and settings?", "Import Config?", JOptionPane.OK_CANCEL_OPTION);
        if ( option == JOptionPane.OK_OPTION) {
            configTreeManager.clearAndRestoreConfig((TimeSeriousConfig)item.getConfigObject());
        }
    }
}
