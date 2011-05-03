package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.context.impl.DefaultContextFactory;
import com.od.jtimeseries.ui.config.DesktopConfiguration;
import com.od.jtimeseries.ui.config.ExportableConfig;
import com.od.jtimeseries.ui.config.UiTimeSeriesConfig;
import com.od.jtimeseries.ui.config.VisualizerConfiguration;
import com.od.jtimeseries.ui.selector.shared.IdentifiableListActionModel;
import com.od.jtimeseries.ui.timeseries.ServerTimeSeries;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.util.CascadeLocationCalculator;
import com.od.jtimeseries.ui.visualizer.AbstractUIContextTimeSeriesFactory;
import com.od.jtimeseries.ui.visualizer.ContextImportExportHandler;
import com.od.jtimeseries.ui.visualizer.ImportDetails;
import com.od.jtimeseries.util.identifiable.Identifiable;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.net.MalformedURLException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 30/04/11
 * Time: 16:11
 */
public class MainSelectorImportExportHandler extends ContextImportExportHandler {

    private Map<Class, ExportableConfigImportUtility> exportableConfigImportUtilities = new HashMap<Class, ExportableConfigImportUtility>();

    public MainSelectorImportExportHandler(TimeSeriesContext rootContext) {
        super(rootContext);
        setTimeSeriesFactory(new TimeSeriousRootContextTimeSeriesFactory());
        setContextFactory(new MainSelectorContextFactory());

        exportableConfigImportUtilities.put(DesktopConfiguration.class, new DesktopExportableConfigImportUtility(rootContext));
        exportableConfigImportUtilities.put(VisualizerConfiguration.class, new VisualizerExportableConfigImportUtility());

    }

    protected boolean canImport(Component component, IdentifiableListActionModel identifiables, Identifiable target) {
        boolean result = target instanceof DesktopContext && identifiables.isSelectionLimitedToType(VisualizerContext.class);
        if ( result ) {
            result = checkTargetIsNotCurrentlyParent(identifiables, target);
        }
        return result;
    }

    protected boolean canImportFromExternalConfig(Component component, Identifiable target) {
        return target.isRoot() || target instanceof DesktopContext;
    }

    protected boolean shouldIgnoreForImport(ExportableConfig c, Identifiable target) {
        return ! (c instanceof DesktopConfiguration ||
              (c instanceof VisualizerConfiguration && target instanceof DesktopContext));
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

    protected ImportDetails getImportDetails(Component component, Identifiable identifiable, Identifiable target) {
        String name = ContextNameCheckUtility.checkName(component, target, identifiable.getId());
        VisualizerConfiguration configuration = ((VisualizerContext) identifiable).getConfiguration();
        configuration.setTitle(name);
        return new ImportDetails(
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

    protected ImportDetails getImportDetails(Component component, ExportableConfig s, Identifiable target) {
        ExportableConfigImportUtility u = exportableConfigImportUtilities.get(s.getClass());
        ImportDetails result = null;
        if ( u != null) {
            result = u.getImportDetails(component, s, target);
        }
        return result;
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

    private class MainSelectorContextFactory extends DefaultContextFactory {

        public <E extends Identifiable> E createContext(TimeSeriesContext parent, String id, String description, Class<E> classType, Object... parameters) {
            if ( VisualizerContext.class.isAssignableFrom(classType)) {
                return (E)new VisualizerContext(getRootContext(), (VisualizerConfiguration)parameters[0]);
            }  else if ( DesktopContext.class.isAssignableFrom(classType)) {
                return (E)new DesktopContext(getRootContext(),(DesktopConfiguration)parameters[0]);
            } else {
                return super.createContext(parent, id, description, classType, parameters);
            }
        }
    }

}
