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
package com.od.jtimeseries.ui.timeserious.rootcontext;

import com.od.jtimeseries.identifiable.Identifiable;
import com.od.jtimeseries.ui.config.ExportableConfig;
import com.od.jtimeseries.ui.uicontext.ImportItem;
import com.od.jtimeseries.ui.util.ContextNameCheckUtility;

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
