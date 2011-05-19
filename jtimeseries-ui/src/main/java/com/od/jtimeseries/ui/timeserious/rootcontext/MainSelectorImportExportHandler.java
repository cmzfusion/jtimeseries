package com.od.jtimeseries.ui.timeserious.rootcontext;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.ui.config.*;
import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.ui.identifiable.DesktopContext;
import com.od.jtimeseries.ui.identifiable.MainSelectorTreeContextFactory;
import com.od.jtimeseries.ui.identifiable.SettingsContext;
import com.od.jtimeseries.ui.identifiable.VisualizerContext;
import com.od.jtimeseries.ui.selector.shared.AbstractUIContextTimeSeriesFactory;
import com.od.jtimeseries.ui.timeseries.ServerTimeSeries;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.uicontext.ContextImportExportHandler;
import com.od.jtimeseries.ui.uicontext.IdentifiableListActionModel;
import com.od.jtimeseries.ui.uicontext.ImportItem;
import com.od.jtimeseries.ui.util.ContextNameCheckUtility;
import com.od.jtimeseries.util.identifiable.Identifiable;

import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 30/04/11
 * Time: 16:11
 */
public class MainSelectorImportExportHandler extends ContextImportExportHandler {

    private Map<Class, ExportableConfigImportUtility> exportableConfigImportUtilities = new HashMap<Class, ExportableConfigImportUtility>();

    public MainSelectorImportExportHandler(TimeSeriesContext rootContext, DisplayNameCalculator displayNameCalculator, ConfigAwareTreeManager configAwareTreeManager) {
        super(rootContext);
        setTimeSeriesFactory(new TimeSeriousRootContextTimeSeriesFactory());
        setContextFactory(new MainSelectorTreeContextFactory(displayNameCalculator));

        exportableConfigImportUtilities.put(DesktopConfiguration.class, new DesktopExportableConfigImportUtility(rootContext));
        exportableConfigImportUtilities.put(VisualizerConfiguration.class, new VisualizerExportableConfigImportUtility());
        exportableConfigImportUtilities.put(DisplayNamePatternConfig.class, new DisplayNameConfigImportUtility(displayNameCalculator));
        exportableConfigImportUtilities.put(TimeSeriousConfig.class, new MainConfigImportUtility(configAwareTreeManager));
    }

    protected boolean canImport(Component component, IdentifiableListActionModel identifiables, Identifiable target) {
        boolean result = target instanceof DesktopContext && identifiables.isSelectionLimitedToType(VisualizerContext.class);
        if ( result ) {
            result = checkTargetIsNotCurrentlyParent(identifiables, target);
        }
        return result;
    }

    protected boolean canImportFromExternalConfig(Component component, Identifiable target) {
        return target.isRoot() || target instanceof DesktopContext || target instanceof SettingsContext;
    }

    protected boolean shouldImport(ExportableConfig c, Identifiable target) {
        return (c instanceof DesktopConfiguration ||
               (c instanceof VisualizerConfiguration && target instanceof DesktopContext)) ||
               c instanceof DisplayNamePatternConfig ||
                c instanceof TimeSeriousConfig;
    }

    private boolean checkTargetIsNotCurrentlyParent(IdentifiableListActionModel identifiables, Identifiable target) {
        boolean result = true;
        for (Identifiable i : identifiables.getSelected()) {
            if ( i.getParent() == target) {
                result = false;
                break;
            }
        }
        return result;
    }

    protected ImportItem getImportItem(Component component, Identifiable identifiable, Identifiable target) {
        String name = ContextNameCheckUtility.checkName(component, target, identifiable.getId());
        VisualizerConfiguration configuration = ((VisualizerContext) identifiable).getConfiguration();
        configuration.setTitle(name);
        return new ImportItem(
            target.getPath() + Identifiable.NAMESPACE_SEPARATOR + name,
            identifiable.getDescription(),
            VisualizerContext.class,
            configuration
        );
    }

    protected void doImport(Component component, java.util.List<ExportableConfig> configs, Identifiable target) {
        //we have logic to cascade window locations on import of multiple items
        //we need to reset the start location for the cascade at the beginning of each import
        for (ExportableConfigImportUtility u : exportableConfigImportUtilities.values()) {
            u.reset();
        }
        super.doImport(component, configs, target);
    }

    protected ImportItem getImportItem(Component component, ExportableConfig s, Identifiable target) {
        ExportableConfigImportUtility u = exportableConfigImportUtilities.get(s.getClass());
        ImportItem result = null;
        if ( u != null) {
            result = u.getImportDetails(component, s, target);
        }
        return result;
    }

    protected void doImportForItem(Component component, Identifiable target, ImportItem item) {
        ExportableConfigImportUtility u = exportableConfigImportUtilities.get(item.getConfigObject().getClass());
        if ( u.handlesOwnImport()) {
            u.doOwnImport(component, target, item);
        } else {
            super.doImportForItem(component, target, item);
        }
    }

    public int getSourceActions(IdentifiableListActionModel selected) {
        return selected.isSelectionLimitedToType(VisualizerContext.class) ?
            DnDConstants.ACTION_COPY_OR_MOVE : DnDConstants.ACTION_COPY;
    }

    //create ServerTimeSeries, which are lighter weight and not backed by an HttpTimeSeries
    //we don't want to create a RemoteHttpTimeSeries for every series in the main selector tree
    private class TimeSeriousRootContextTimeSeriesFactory extends AbstractUIContextTimeSeriesFactory {

        protected UIPropertiesTimeSeries createTimeSeriesForConfig(UiTimeSeriesConfig config) throws MalformedURLException {
            return new ServerTimeSeries(config);
        }
    }

}
