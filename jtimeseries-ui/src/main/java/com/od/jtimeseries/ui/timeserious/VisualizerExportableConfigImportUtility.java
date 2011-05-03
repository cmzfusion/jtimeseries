package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.config.ExportableConfig;
import com.od.jtimeseries.ui.config.VisualizerConfiguration;
import com.od.jtimeseries.ui.util.CascadeLocationCalculator;
import com.od.jtimeseries.ui.visualizer.ImportDetails;
import com.od.jtimeseries.util.identifiable.Identifiable;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
* Created by IntelliJ IDEA.
* User: Nick
* Date: 03/05/11
* Time: 11:10
*
*/
class VisualizerExportableConfigImportUtility extends ExportableConfigImportUtility {

    private CascadeLocationCalculator cascadeLocationCalculator = new CascadeLocationCalculator(50, 50);
    private Map<DesktopContext, Rectangle> lastImportLocationByDesktop = new HashMap<DesktopContext, Rectangle>();

    protected void reset() {
        lastImportLocationByDesktop.clear();
    }

    protected ImportDetails doGetImportDetails(Component component, ExportableConfig s, Identifiable target) {

        VisualizerConfiguration c = (VisualizerConfiguration)s;

        DesktopContext d = (DesktopContext)target;
        if ( ! lastImportLocationByDesktop.containsKey(d)) {
            lastImportLocationByDesktop.put(d, new Rectangle(0, 0, c.getFrameLocation().width, c.getFrameLocation().height) );
        }

        c.setShown(true);  //always show on import
        c.setIsIcon(false);
        Rectangle lastVisualizerImportLocation = cascadeLocationCalculator.getNextLocation(lastImportLocationByDesktop.get(d), d.getFrameLocation(), c.getFrameLocation().width, c.getFrameLocation().height);
        lastImportLocationByDesktop.put(d, lastVisualizerImportLocation);
        c.setFrameLocation(lastVisualizerImportLocation);

        return new ImportDetails(
            target.getPath() + Identifiable.NAMESPACE_SEPARATOR + c.getTitle(),
            c.getTitle(),
            VisualizerContext.class,
            c
        );
    }
}
