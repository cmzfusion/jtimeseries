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
package com.od.jtimeseries.ui.uicontext;

import com.od.jtimeseries.context.ContextFactory;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.context.impl.DefaultContextFactory;
import com.od.jtimeseries.timeseries.TimeSeriesFactory;
import com.od.jtimeseries.timeseries.impl.DefaultTimeSeriesFactory;
import com.od.jtimeseries.ui.config.ExportableConfig;
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

    private static final LogMethods logMethods = LogUtils.getLogMethods(ContextImportExportHandler.class);

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
            if ( shouldImport(s, target) ) {
                ImportItem d = getImportItem(component, s, target);
                doImportForItem(component, target, d);
            }
        }
    }

     /**
     * @return true if the identifiable should be ignored during import, e.g it is a folder node of a type which does
     * not need to be explicitly created. Ignored nodes in the selection will not prevent import taking place.
     */
    protected boolean shouldImport(Identifiable i, Identifiable target) {
        return true;
    }

    /**
     * Subclass should override to create ImportItem if this Identifiable can be imported
     * @return an ImportDetails, which contains everything necessary to import the target exportable config, or null
     */
    protected ImportItem getImportItem(Component component, Identifiable identifiable, Identifiable target) {
        return null;
    }


    protected void doImport(Component component, List<ExportableConfig> configs, Identifiable target) {
        for ( ExportableConfig s : configs) {
            if ( shouldImport(s, target) ) {
                ImportItem d = getImportItem(component, s, target);
                doImportForItem(component, target, d);
            }
        }
    }

    protected boolean shouldImport(ExportableConfig s, Identifiable target) {
        return true;
    }

    /**
     * Subclass should override to create ImportItem if this ExportableConfig can be imported
     * @return an ImportDetails, which contains everything necessary to import the target exportable config, or null
     */
    protected ImportItem getImportItem(Component component, ExportableConfig s, Identifiable target) {
        return null;
    }

    protected void doImportForItem(Component component, Identifiable target, ImportItem item) {
        if ( item != null) {
            if ( rootContext.contains(item.getPath())) {
                pathAlreadyExistsOnImport(component, target, item);
            } else {
                rootContext.create(item.getPath(), item.getDescription(), item.getLocalClassType(), item.getConfigObject());
            }
        }
    }

    //may override to provide feedback to user where appropriate
    protected void pathAlreadyExistsOnImport(Component component, Identifiable target, ImportItem d) {
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
