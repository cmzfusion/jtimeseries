package com.od.jtimeseries.ui.visualizer;

import com.od.jtimeseries.context.impl.DefaultTimeSeriesContext;
import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.ui.event.TimeSeriousBusListener;
import com.od.jtimeseries.ui.timeserious.ContextUpdatingBusListener;
import com.od.jtimeseries.ui.util.Disposable;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.swing.eventbus.UIEventBus;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 23/02/11
 * Time: 06:48
 */
public abstract class AbstractUIRootContext extends DefaultTimeSeriesContext {

    protected static final LogMethods logMethods = LogUtils.getLogMethods(VisualizerRootContext.class);
    private ImportExportHandler importExportHandler = new DummyImportExportHandler();

    public AbstractUIRootContext(DisplayNameCalculator displayNameCalculator) {
        displayNameCalculator.addRootContext(this);
    }

    protected void initializeFactoriesAndContextBusListener(ImportExportHandler importExportHandler) {
        this.importExportHandler = importExportHandler;
        setTimeSeriesFactory(importExportHandler.getTimeSeriesFactory());
        setContextFactory(importExportHandler.getContextFactory());

        UIEventBus.getInstance().addEventListener(
            TimeSeriousBusListener.class,
            createContextBusListener()
        );
    }

    public boolean canImport(List<? extends Identifiable> identifiables, Identifiable target) {
        return importExportHandler.canImport(identifiables, target);
    }

    public void doImport(List<? extends Identifiable> identifiables, Identifiable target) {
        importExportHandler.doImport(identifiables, target);
    }

    protected abstract ContextUpdatingBusListener createContextBusListener();

    public void dispose() {
        for (Identifiable i : findAll(Identifiable.class).getAllMatches()) {
            if ( i instanceof Disposable) {
                ((Disposable)i).dispose();
            }
        }
    }

    private class DummyImportExportHandler extends ImportExportHandler {

        public DummyImportExportHandler() {
            super(AbstractUIRootContext.this);
        }

        protected boolean shouldIgnoreForImport(Identifiable i, Identifiable target) {
            return false;
        }

        protected boolean canImport(Identifiable i, Identifiable target) {
            return false;
        }

        protected ImportDetails getImportDetails(Identifiable identifiable, Identifiable target) {
            return null;
        }
    }
}
