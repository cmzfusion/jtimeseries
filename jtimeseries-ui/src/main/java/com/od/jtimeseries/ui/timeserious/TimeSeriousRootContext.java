package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.net.udp.TimeSeriesServerDictionary;
import com.od.jtimeseries.ui.config.*;
import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.ui.download.panel.TimeSeriesServerContext;
import com.od.jtimeseries.ui.visualizer.AbstractUIRootContext;
import com.od.jtimeseries.ui.visualizer.ContextImportExportHandler;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

import java.net.UnknownHostException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 13-Dec-2010
 * Time: 07:19:42
 * To change this template use File | Settings | File Templates.
 */
public class TimeSeriousRootContext extends AbstractUIRootContext implements CollectionClearingConfigAware {

    private static LogMethods logMethods = LogUtils.getLogMethods(TimeSeriousRootContext.class);
    private TimeSeriesServerDictionary serverDictionary;

    public TimeSeriousRootContext(TimeSeriesServerDictionary serverDictionary, DisplayNameCalculator displayNameCalculator) {
        super(displayNameCalculator);
        this.serverDictionary = serverDictionary;
        addTreeListener(new DisplayNameCalculatingTreeListener(displayNameCalculator));

        ContextImportExportHandler h = new MainSelectorImportExportHandler(this);
        setImportExportHandler(h);
    }

    public DesktopContext getMainDesktopContext() {
        return get(DesktopConfiguration.MAIN_DESKTOP_NAME, DesktopContext.class);
    }

    protected ContextUpdatingBusListener createContextBusListener() {
        return new ServerSeriesLoadingBusListener(this);
    }

    public void prepareConfigForSave(TimeSeriousConfig config) {
        saveServers(config);
        saveDesktops(config);
    }

    private void saveServers(TimeSeriousConfig config) {
        List<TimeSeriesServerContext> serverContexts = findAll(TimeSeriesServerContext.class).getAllMatches();
        List<TimeSeriesServerConfig> serverConfigs = new LinkedList<TimeSeriesServerConfig>();
        for ( TimeSeriesServerContext c : serverContexts) {
            serverConfigs.add(new TimeSeriesServerConfig(c));
        }
        config.setTimeSeriesServerConfigs(serverConfigs);
    }


    private void saveDesktops(TimeSeriousConfig config) {
        for ( DesktopContext desktopContext : findAll(DesktopContext.class).getAllMatches()) {
            config.setDesktopConfigration(desktopContext.getId(), desktopContext.getConfiguration());
        }
    }

    public void restoreConfig(TimeSeriousConfig config) {
        restoreSevers(config);
        restoreDesktops(config);
    }

    private void restoreSevers(TimeSeriousConfig config) {
        for (TimeSeriesServerConfig c : config.getServerConfigs()) {
            try {
                serverDictionary.getOrCreateServer(
                    c.getHostName(),
                    c.getPort(),
                    c.getDescription()
                );
            } catch (UnknownHostException e) {
                logMethods.logError("Could not create server " + serverDictionary, e);
            }
        }
    }

    private void restoreDesktops(TimeSeriousConfig config) {
        for ( Map.Entry<String, DesktopConfiguration> configEntry : config.getDesktopConfigs().entrySet()) {
            createDesktopContext(configEntry.getValue());
        }
    }


    private DesktopContext createDesktopContext(DesktopConfiguration desktopConfiguration) {
        DesktopContext context = create(desktopConfiguration.getTitle(), desktopConfiguration.getTitle(), DesktopContext.class, desktopConfiguration);
        return context;
    }

    public List<CollectionClearingConfigAware> getConfigAwareChildren() {
        return Collections.emptyList();
    }

    public void clearConfig() {
        for ( Identifiable i : getChildren()) {
            if ( i instanceof TimeSeriesServerContext ) {
                serverDictionary.removeServer(((TimeSeriesServerContext)i).getServer());
            } else if ( i instanceof DesktopContext) {
                removeChild(i);
            }
        }
    }
}
