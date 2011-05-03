package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.config.ExportableConfig;
import com.od.jtimeseries.ui.visualizer.ImportDetails;
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

    protected final ImportDetails getImportDetails(Component component, ExportableConfig s, Identifiable target) {
        target = getRealTargetContext(target);
        boolean userCancelled = findUniqueName(component, s, target);
        ImportDetails result = null;
        if ( ! userCancelled) {
            result = doGetImportDetails(component, s, target);
        }
        return result;
    }

    private boolean findUniqueName(Component component, ExportableConfig c, Identifiable target) {
        String title = ContextNameCheckUtility.checkName(component, target, c.getTitle());
        boolean userCancelledImport = title == null;
        if ( ! userCancelledImport) {
            c.setTitle(title); //update the config to reflect the updated name
        }
        return userCancelledImport;
    }

    protected abstract ImportDetails doGetImportDetails(Component component, ExportableConfig s, Identifiable target);

    protected Identifiable getRealTargetContext(Identifiable target) {
        return target;
    }
}
