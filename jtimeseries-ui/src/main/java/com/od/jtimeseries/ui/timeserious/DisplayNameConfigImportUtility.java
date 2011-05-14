package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.config.DisplayNamePattern;
import com.od.jtimeseries.ui.config.DisplayNamePatternConfig;
import com.od.jtimeseries.ui.config.ExportableConfig;
import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.ui.visualizer.ImportItem;
import com.od.jtimeseries.util.identifiable.Identifiable;

import javax.swing.*;
import java.awt.*;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 12/05/11
 * Time: 08:33
 */
public class DisplayNameConfigImportUtility extends ExportableConfigImportUtility {

    private DisplayNameCalculator displayNameCalculator;

    public DisplayNameConfigImportUtility(DisplayNameCalculator displayNameCalculator) {
        this.displayNameCalculator = displayNameCalculator;
    }

    protected void reset() {
    }

    protected boolean findUniqueName(Component component, ExportableConfig c, Identifiable target) {
        return false;
    }

    protected ImportItem doGetImportDetails(Component component, ExportableConfig c, Identifiable target) {
        return new ImportItem(
            target.getPath() + Identifiable.NAMESPACE_SEPARATOR + SettingsContext.SETTINGS_NODE_NAME + DisplayNamesContext.DISPLAY_NAME_NODE_NAME,
            DisplayNamesContext.DISPLAY_NAME_NODE_NAME,
            DisplayNamesContext.class,
            c
        );
    }

    public boolean handlesOwnImport() {
        return true;
    }

    protected void doImportForItem(Component component, Identifiable target, ImportItem item) {
        Window window = SwingUtilities.windowForComponent(component);
        int result = JOptionPane.showConfirmDialog(window, "Import Rules?", "Import Display Name Rules", JOptionPane.OK_CANCEL_OPTION);
        if ( result != JOptionPane.CANCEL_OPTION) {
            List<DisplayNamePattern> current = displayNameCalculator.getDisplayNamePatternConfig().getDisplayNamePatterns();
            current.addAll(((DisplayNamePatternConfig)item.getConfigObject()).getDisplayNamePatterns());

            int option = JOptionPane.showConfirmDialog(window, "Apply Now?", "Apply Display Name Rules", JOptionPane.YES_NO_OPTION);
            displayNameCalculator.displayPatternsChanged(current, option == JOptionPane.YES_OPTION);
        }
    }
}
