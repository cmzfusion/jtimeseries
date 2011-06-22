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

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.ui.config.DesktopConfiguration;
import com.od.jtimeseries.ui.config.ExportableConfig;
import com.od.jtimeseries.ui.identifiable.DesktopContext;
import com.od.jtimeseries.ui.uicontext.ImportItem;
import com.od.jtimeseries.ui.util.CascadeLocationCalculator;
import com.od.jtimeseries.util.identifiable.Identifiable;

import javax.swing.*;
import java.awt.*;

/**
* Created by IntelliJ IDEA.
* User: Nick
* Date: 03/05/11
* Time: 11:10
*
*/
class DesktopExportableConfigImportUtility extends ExportableConfigImportUtility {

    private static final int DESKTOP_IMPORT_WIDTH = 800;
    private static final int DESKTOP_IMPORT_HEIGHT = 600;
    private CascadeLocationCalculator cascadeLocationCalculator = new CascadeLocationCalculator(50, 50);
    private Rectangle lastDesktopImportLocation;
    private TimeSeriesContext rootContext;

    DesktopExportableConfigImportUtility(TimeSeriesContext rootContext) {
        this.rootContext = rootContext;
    }

    protected void reset() {
        lastDesktopImportLocation = null;
    }

    protected Identifiable getRealTargetContext(Identifiable targetContext) {
        return rootContext; //desktops always live under main root context
    }

    protected ImportItem doGetImportDetails(Component component, ExportableConfig s, Identifiable target) {
        Rectangle parentWindowBounds = SwingUtilities.getWindowAncestor(component).getBounds();
        if ( lastDesktopImportLocation == null) {
            lastDesktopImportLocation = parentWindowBounds;
        }
        DesktopConfiguration c = (DesktopConfiguration)s;
        c.setShown(true); //always show on import
        c.setFrameExtendedState(JFrame.NORMAL);
        lastDesktopImportLocation = cascadeLocationCalculator.getNextLocation(lastDesktopImportLocation, parentWindowBounds, DESKTOP_IMPORT_WIDTH, DESKTOP_IMPORT_HEIGHT);
        c.setFrameLocation(lastDesktopImportLocation);

        return new ImportItem(
            target.getPath() + Identifiable.NAMESPACE_SEPARATOR + c.getTitle(),
            c.getTitle(),
            DesktopContext.class,
            c
        );
    }

    public boolean handlesOwnImport() {
        return false;
    }
}
