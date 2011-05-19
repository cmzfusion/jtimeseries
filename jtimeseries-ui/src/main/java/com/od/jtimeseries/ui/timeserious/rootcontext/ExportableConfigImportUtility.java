package com.od.jtimeseries.ui.timeserious.rootcontext;

import com.od.jtimeseries.ui.config.ExportableConfig;
import com.od.jtimeseries.ui.uicontext.ImportItem;
import com.od.jtimeseries.ui.util.ContextNameCheckUtility;
import com.od.jtimeseries.util.identifiable.Identifiable;

import java.awt.*;

/**
* Created by IntelliJ IDEA.
* User: Nick
* Date: 03/05/11
* Time: 11:09
*
* Logic to handle the import of an external config file
* we need to check for duplicate names and adjust other aspects of the config during import
*/
abstract class ExportableConfigImportUtility {

    protected abstract void reset();

    protected final ImportItem getImportDetails(Component component, ExportableConfig c, Identifiable target) {
        target = getRealTargetContext(target);
        boolean userCancelled = findUniqueName(component, c, target);
        ImportItem result = null;
        if ( ! userCancelled) {
            result = doGetImportDetails(component, c, target);
        }
        return result;
    }

    protected boolean findUniqueName(Component component, ExportableConfig c, Identifiable target) {
        String title = ContextNameCheckUtility.checkName(component, target, c.getTitle());
        boolean userCancelledImport = title == null;
        if ( ! userCancelledImport) {
            c.setTitle(title); //update the config to reflect the updated name
        }
        return userCancelledImport;
    }

    protected abstract ImportItem doGetImportDetails(Component component, ExportableConfig c, Identifiable target);

    protected Identifiable getRealTargetContext(Identifiable target) {
        return target;
    }

    /**
     * @return true, if this import utility contains custom logic to handle it's own import, which should be used
     * instead of the default ImportExportHandler logic
     */
    public abstract boolean handlesOwnImport();

    /**
     * Called if this ExportableConfigImportUtility handlesImport()
     */
    protected void doOwnImport(Component component, Identifiable target, ImportItem item) {
    }
}
