package com.od.jtimeseries.ui.visualizer;

import com.od.jtimeseries.context.ContextFactory;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.context.impl.DefaultContextFactory;
import com.od.jtimeseries.timeseries.TimeSeriesFactory;
import com.od.jtimeseries.timeseries.impl.DefaultTimeSeriesFactory;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

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
     * @return true if all the identifiable (and associated children) can be imported, apart from any nodes
     * which are specifically ignored.
     */
    protected boolean canImport(List<? extends Identifiable> identifiables, Identifiable target) {
        boolean result = true;
        LinkedHashSet<Identifiable> all = getIdentifiablesAndAllDescendents(identifiables);
        for ( Identifiable i : all) {
            if ( ! shouldIgnoreForImport(i, target) && ! canImport(i, target)) {
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * @return true if the identifiable should be ignored during import, e.g it is a folder node of a type which does
     * not need to be explicitly created. Ignored nodes in the selection will not prevent import taking place.
     */
    protected abstract boolean shouldIgnoreForImport(Identifiable i, Identifiable target);

    /*
     * @return true, if the identifiable can be imported
     */
    protected abstract boolean canImport(Identifiable i, Identifiable target);


    protected void doImport(List<? extends Identifiable> identifiables, Identifiable target) {
        LinkedHashSet<Identifiable> toAdd = getIdentifiablesAndAllDescendents(identifiables);

        for ( Identifiable s : toAdd) {
            if ( ! shouldIgnoreForImport(s, target) ) {
                ImportDetails d = getImportDetails(s, target);
                //TODO we may want to flag the conflict up to the user
                if ( ! rootContext.contains(d.getPath())) {
                    rootContext.create(d.getPath(), d.getDescription(), d.getLocalClassType(), d.getConfigObject());
                }
            }
        }
    }

    /**
     * @return an ImportDetails, which contains everything necessary to import the target identifiable
     */
    protected abstract ImportDetails getImportDetails(Identifiable identifiable, Identifiable target);

    private LinkedHashSet<Identifiable> getIdentifiablesAndAllDescendents(List<? extends Identifiable> identifiables) {
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
    public abstract int getSourceActions(List<? extends Identifiable> selected);


    public void doExport(List<Identifiable> transferData, int action) {
        if ( action == DnDConstants.ACTION_MOVE) {
            for ( Identifiable i : transferData) {
                rootContext.remove(i.getPath());
            }
        }
    }


    public TimeSeriesContext getRootContext() {
        return rootContext;
    }

    /**
     * All the information necessary to import an identifiable into this context
     */
    protected class ImportDetails {
        String path;
        String description;
        Class<? extends Identifiable> localClassType;
        Object configObject;

        public ImportDetails(String path, String description, Class<? extends Identifiable> localClassType, Object configObject) {
            this.path = path;
            this.description = description;
            this.localClassType = localClassType;
            this.configObject = configObject;
        }

        public String getPath() {
            return path;
        }

        public String getDescription() {
            return description;
        }

        public Class<? extends Identifiable> getLocalClassType() {
            return localClassType;
        }

        public Object getConfigObject() {
            return configObject;
        }
    }
}
