package com.od.jtimeseries.ui.uicontext;

import com.od.jtimeseries.context.impl.DefaultTimeSeriesContext;
import com.od.jtimeseries.ui.config.ExportableConfig;
import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.ui.event.TimeSeriousBusListener;
import com.od.jtimeseries.ui.util.Disposable;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.swing.eventbus.UIEventBus;
import com.od.swing.weakreferencelistener.WeakReferenceListener;

import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 23/02/11
 * Time: 06:48
 */
public abstract class AbstractUIRootContext extends DefaultTimeSeriesContext {

    protected static final LogMethods logMethods = LogUtils.getLogMethods(AbstractUIRootContext.class);
    private ContextImportExportHandler importExportHandler = new DummyImportExportHandler();
    private ContextUpdatingBusListener contextBusListener;

    public AbstractUIRootContext(DisplayNameCalculator displayNameCalculator) {
        displayNameCalculator.addRootContext(this);

        contextBusListener = createContextBusListener();
        WeakReferenceListener l = new WeakReferenceListener(TimeSeriousBusListener.class, contextBusListener);
        l.addListenerTo(UIEventBus.getInstance());
    }

    /**
     * Set the import export handler which defines logic for copy/paste
     * drag/drop, and the factories to create items
     */
    protected void setImportExportHandler(ContextImportExportHandler importExportHandler) {
        this.importExportHandler = importExportHandler;
        setTimeSeriesFactory(importExportHandler.getTimeSeriesFactory());
        setContextFactory(importExportHandler.getContextFactory());
    }

    public boolean canImport(Component component, IdentifiableListActionModel identifiables, Identifiable target) {
        return importExportHandler.canImport(component, identifiables, target);
    }

    public void doImport(Component component, IdentifiableListActionModel identifiables, Identifiable target) {
        importExportHandler.doImport(component, identifiables, target);
    }

    public void doImport(Component component, List<ExportableConfig> configs, Identifiable target) {
        importExportHandler.doImport(component, configs, target);
    }

    protected abstract ContextUpdatingBusListener createContextBusListener();

    public void dispose() {
        for (Identifiable i : findAll(Identifiable.class).getAllMatches()) {
            if ( i instanceof Disposable) {
                ((Disposable)i).dispose();
            }
        }
    }

    public int getSourceActions(IdentifiableListActionModel selected) {
        return importExportHandler.getSourceActions(selected);
    }

    public void doExport(IdentifiableListActionModel transferData, int action) {
        importExportHandler.doExport(transferData, action);
    }

    public boolean canImportFromExternalConfig(Component component, Identifiable target) {
        return importExportHandler.canImportFromExternalConfig(component, target);
    }

    private class DummyImportExportHandler extends ContextImportExportHandler {

        public DummyImportExportHandler() {
            super(AbstractUIRootContext.this);
        }

        protected boolean shouldImport(Identifiable i, Identifiable target) {
            return true;
        }

        protected boolean canImport(Identifiable i, Identifiable target) {
            return false;
        }

        protected ImportItem getImportItem(Component component, Identifiable identifiable, Identifiable target) {
            return null;
        }

        public int getSourceActions(IdentifiableListActionModel selected) {
            return DnDConstants.ACTION_NONE;
        }

        public void doExport(IdentifiableListActionModel transferData, int action) {
        }
    }
}
