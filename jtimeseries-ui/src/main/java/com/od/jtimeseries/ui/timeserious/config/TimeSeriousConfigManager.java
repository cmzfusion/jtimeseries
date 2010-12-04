package com.od.jtimeseries.ui.timeserious.config;

import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;
import od.configutil.*;

import javax.swing.*;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 18-Nov-2010
 * Time: 21:24:48
 * To change this template use File | Settings | File Templates.
 */
public class TimeSeriousConfigManager {

    private static LogMethods logMethods = LogUtils.getLogMethods(TimeSeriousConfigManager.class);

    private final String MAIN_CONFIG_NAME = "timeSerious";

    //the user preference settings, which store the location of the config file directory in an os safe manner
    private PreferenceSettings preferenceSettings = new PreferenceSettings("jtimeseries/timeserious/ui/configs", "configProperties");
    private od.configutil.ConfigManager configManager;

    public TimeSeriousConfigManager() {
        preferenceSettings.load();
    }

    /**
     * Find a config folder from user preferences and create a config manager to load configs
     * Configs may be loaded even if the folder is no longer writable to timeserious
     *
     * @return the config loaded, or the default config
     */
    public TimeSeriousConfig loadConfig() throws ConfigManagerException {
        TimeSeriousConfig result = new TimeSeriousConfig();

        if ( preferenceSettings.isConfigDirectorySet()) {
            configManager = createConfigManager(preferenceSettings.getMainConfigDirectory());
            try {
                result = configManager.loadConfig(MAIN_CONFIG_NAME);
            } catch (NoConfigFoundException nfe) {
                logMethods.logWarning("Could not find a config " + MAIN_CONFIG_NAME + " config, will use default config");
            } catch (ConfigManagerException n) {
                logMethods.logWarning("Could not load " + MAIN_CONFIG_NAME + " config, will use default config", n);
            }
        }
        return result;
    }

    private ConfigManager createConfigManager(File configDirectory) {
        ConfigManager configManager = new ConfigManager();
        configManager.setConfigDirectory(configDirectory);
        return configManager;
    }

    /**
     * @return true, if a config file directory has been determined and a ConfigManager created so that we can load and save configs
     */
    private boolean isInitializedAndWritable() {
        return configManager != null && configManager.canWrite();
    }

    /**
     * If not initialized already, prompt the user to choose a config directory, and use it to initialize the
     * ConfigManager so that we can load and save configs
     */
    private boolean initializeConfigManager(JFrame mainFrame) {
        if ( ! isInitializedAndWritable() ) {
            configManager = createConfigManager(mainFrame);
            if ( configManager != null ) {
                saveConfigManagerPreferences(configManager);
            }
        }
        return isInitializedAndWritable();
    }

    //the config directory is saved using Preferences mechanism
    //this gives us the ability to find the main config file again on startup
    private void saveConfigManagerPreferences(ConfigManager m) {
        FileSourceAndSink fileSink = (FileSourceAndSink)m.getConfigSink();
        preferenceSettings.setMainConfigDirectory(fileSink.getConfigDirectory());
        preferenceSettings.store();
    }

    private ConfigManager createConfigManager(JFrame mainFrame) {
        ConfigDirectorySelector s = new ConfigDirectorySelector(mainFrame);
        s.showSelectorDialog();
        File f = s.getSelectedDirectory();
        ConfigManager result = null;
        //if user selects a directory, proceed, or assume they don't want to save
        if(f != null) {
            result = createConfigManager(f);
            if ( ! result.canWrite()) {
                JOptionPane.showMessageDialog(mainFrame,
                    "Cannot write to this location. Please choose another directory to save the config",
                    "Cannot write config",
                    JOptionPane.WARNING_MESSAGE
                );
                result = createConfigManager(mainFrame);
            }
        }
        return result;
    }

    /**
     * Check that a configManager has been initialized and is writable
     * If not, prompt the user for a config directory and create a new configManager
     * to save the config
     *
     * @return true, if config was saved, or false if user decides not to save
     * @throws ConfigManagerException if config save fails
     */
    public boolean saveConfig(JFrame mainFrame, TimeSeriousConfig config) throws ConfigManagerException {
        boolean canSave = initializeConfigManager(mainFrame);
        if ( canSave ) {
            doSave(config);
        } else {
            logMethods.logInfo("Not saving, config manager not initialized");
        }
        return canSave;
    }

    private void doSave(TimeSeriousConfig config) throws ConfigManagerException {
        configManager.saveConfig(MAIN_CONFIG_NAME, config);
    }
}
