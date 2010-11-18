package com.od.jtimeseries.ui.timeserious.config;

import od.configutil.ConfigManager;
import od.configutil.ConfigManagerException;
import od.configutil.PreferenceSettings;

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

    private final String MAIN_CONFIG_NAME = "timeSerious";

    //the user preference settings, which store the location of the config file directory in an os safe manner
    private PreferenceSettings preferenceSettings = new PreferenceSettings("jtimeseries/timeserious/ui/configs", "configProperties");
    private od.configutil.ConfigManager configManager;

    public TimeSeriousConfigManager() {
        preferenceSettings.load();
    }

    /**
     * @return the config loaded, or the default config
     */
    public TimeSeriousConfig loadConfig() throws ConfigManagerException {
        TimeSeriousConfig result = new TimeSeriousConfig();

        if ( preferenceSettings.isConfigDirectorySet()) {
            createConfigManager();
            if (configManager.configExists(MAIN_CONFIG_NAME)) {
                result = configManager.loadConfig(MAIN_CONFIG_NAME);
            }
        }
        return result;
    }

    private void createConfigManager() {
        configManager = new ConfigManager();
        configManager.setConfigDirectory(preferenceSettings.getMainConfigDirectory());
    }

    /**
     * @return true, if a config file directory has been determined and a ConfigManager created so that we can load and save configs
     */
    public boolean isInitialized() {
        //if the user has not already configured the main config file directory, show the wizard to do get them to do it now
        return configManager != null;
    }

    /**
     * If not initialized already, prompt the user to choose a config directory, and use it to initialize the
     * ConfigManager so that we can load and save configs
     */
    public void checkInitialized(JFrame mainFrame) {
        if ( ! isInitialized() ) {
            ConfigDirectorySelector s = new ConfigDirectorySelector(mainFrame);
            s.showSelectorDialog();
            File f = s.getSelectedDirectory();
            if ( f != null ) {
                preferenceSettings.setMainConfigDirectory(f);
                preferenceSettings.store();
                createConfigManager();
            }
        }
    }

    public void saveConfig(TimeSeriousConfig config) throws ConfigManagerException {
        if ( isInitialized() ) {
            configManager.saveConfig(MAIN_CONFIG_NAME, config);
        } else {
            throw new UnsupportedOperationException("Cannot save a config when ConfigManager not initialized");
        }
    }
}
