package com.od.jtimeseries.ui.visualizer;

import com.od.jtimeseries.context.ContextFactory;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.context.impl.DefaultContextFactory;
import com.od.jtimeseries.timeseries.TimeSeriesFactory;
import com.od.jtimeseries.timeseries.impl.DefaultTimeSeriesFactory;
import com.od.jtimeseries.ui.config.ExportableConfig;
import com.od.jtimeseries.ui.selector.shared.IdentifiableListActionModel;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 30/04/11
 * Time: 15:36
 */
public abstract class ContextImportExportHandler {

    protected static final LogMethods logMethods = LogUtils.getLogMethods(ContextImportExportHandler.class);

    private TimeSeriesContext rootContext;
    private ContextFactory contextFactory = new DefaultContextFactory();
    private TimeSeriesFactory timeSeriesFactory = new DefaultTimeSeriesFactory();

    public ContextImportExportHandler(TimeSeriesContext rootContext) {
        this.rootContext = rootContext;
    }

    public ContextFactory getContextFactory() {
        return contextFactory;
    }

    public void setContextFactory(ContextFactory contextFactory) {
        this.contextFactory = contextFactory;
    }

    public TimeSeriesFactory getTimeSeriesFactory() {
        return timeSeriesFactory;
    }

    public void setTimeSeriesFactory(TimeSeriesFactory timeSeriesFactory) {
        this.timeSeriesFactory = timeSeriesFactory;
    }

    /**
     * @return true if all the identifiable (and its descendants) can be imported, apart from any nodes
     * which are specifically ignored.
     */
    protected boolean canImport(Component component, IdentifiableListActionModel identifiables, Identifiable target) {
        boolean result = true;
        for ( Identifiable i : identifiables.getSelected()) {
            if ( ! canImport(i, target)) {
                result = false;
                break;
            }
        }
        return result;
    }

    /*
     * @return true, if the identifiable (and its descendants) can be imported
     */
    protected boolean canImport(Identifiable i, Identifiable target) {
        return true;
    }

    protected boolean canImportFromExternalConfig(Component component, Identifiable target) {
        return false;
    }

    protected void doImport(Component component, IdentifiableListActionModel identifiables, Identifiable target) {
        LinkedHashSet<Identifiable> toAdd = getIdentifiablesAndAllDescendents(identifiables.getSelected());
        for ( Identifiable s : toAdd) {
            if ( ! shouldIgnoreForImport(s, target) ) {
                ImportDetails d = getImportDetails(component, s, target);
                doImport(component, target, d);
            }
        }
    }

     /**
     * @return true if the identifiable should be ignored during import, e.g it is a folder node of a type which does
     * not need to be explicitly created. Ignored nodes in the selection will not prevent import taking place.
     */
    protected boolean shouldIgnoreForImport(Identifiable i, Identifiable target) {
        return false;
    }

    /**
     * @return an ImportDetails, which contains everything necessary to import the target identifiable
     */
    protected ImportDetails getImportDetails(Component component, Identifiable identifiable, Identifiable target) {
        return null;
    }


    protected void doImport(Component component, List<ExportableConfig> configs, Identifiable target) {
        for ( ExportableConfig s : configs) {
            if ( ! shouldIgnoreForImport(s, target) ) {
                ImportDetails d = getImportDetails(component, s, target);
                doImport(component, target, d);
            }
        }
    }

    protected boolean shouldIgnoreForImport(ExportableConfig s, Identifiable target) {
        return false;
    }

    /**
     * @return an ImportDetails, which contains everything necessary to import the target exportable config
     */
    protected ImportDetails getImportDetails(Component component, ExportableConfig s, Identifiable target) {
        return null;
    }

    protected void doImport(Component component, Identifiable target, ImportDetails d) {
        if ( d != null) {
            if ( rootContext.contains(d.getPath())) {
                pathAlreadyExistsOnImport(component, target, d);
            } else {
                rootContext.create(d.getPath(), d.getDescription(), d.getLocalClassType(), d.getConfigObject());
            }
        }
    }

    //may override to provide feedback to user where appropriate
    protected void pathAlreadyExistsOnImport(Component component, Identifiable target, ImportDetails d) {
    }

    protected LinkedHashSet<Identifiable> getIdentifiablesAndAllDescendents(List<? extends Identifiable> identifiables) {
        LinkedHashSet<Identifiable> toAdd = new LinkedHashSet<Identifiable>();
        for ( Identifiable i : identifiables) {
            //identifiables in the list may be at different levels of the hierarchy from
            //the same tree structure, parents appear before their descendants
            //Check we have not already added this node to the list before adding it,
            if ( ! toAdd.contains(i)) {
                //this node, plus any children
                toAdd.add(i);
                toAdd.addAll(i.findAll(Identifiable.class).getAllMatches());
            }
        }
        return toAdd;
    }

    /**
     * @return the source actions e.g. COPY/CUT supported by this handler, one of the DnDConstants
     */
    public abstract int getSourceActions(IdentifiableListActionModel selected);


    public void doExport(IdentifiableListActionModel transferData, int action) {
        if ( action == DnDConstants.ACTION_MOVE) {
            for ( Identifiable i : transferData.getSelected()) {
                rootContext.remove(i.getPath());
            }
        }
    }


    public TimeSeriesContext getRootContext() {
        return rootContext;
    }

}
