package com.od.jtimeseries.ui.timeserious.config;

import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;
import od.configutil.ConfigManager;
import od.configutil.ConfigManagerException;
import od.configutil.NoConfigFoundException;
import od.configutil.PreferenceSettings;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

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
     * @return the config loaded, or the default config
     */
    public TimeSeriousConfig loadConfig() throws ConfigManagerException {
        TimeSeriousConfig result = new TimeSeriousConfig();

        if ( preferenceSettings.isConfigDirectorySet()) {
            createConfigManager();
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

    private void createConfigManager() {
        configManager = new ConfigManager();
        configManager.setConfigDirectory(preferenceSettings.getMainConfigDirectory());
    }

    /**
     * @return true, if a config file directory has been determined and a ConfigManager created so that we can load and save configs
     */
    public boolean isInitialized() {
        return configManager != null;
    }

    /**
     * If not initialized already, prompt the user to choose a config directory, and use it to initialize the
     * ConfigManager so that we can load and save configs
     */
    public void checkInitialized(JFrame mainFrame) {
        if ( ! isInitialized() ) {
            File f = getUserToSelectDirectory(mainFrame);
            if ( f != null ) {
                preferenceSettings.setMainConfigDirectory(f);
                preferenceSettings.store();
                createConfigManager();
            }
        }
    }

    private File getUserToSelectDirectory(JFrame mainFrame) {
        ConfigDirectorySelector s = new ConfigDirectorySelector(mainFrame);
        s.showSelectorDialog();
        File f = s.getSelectedDirectory();
        while(f != null && ! f.canWrite()) {
            JOptionPane.showMessageDialog(mainFrame,
                "Cannot write to this location. Please choose or create another directory to save the config",
                "Cannot write config",
                JOptionPane.WARNING_MESSAGE
            );
            f = getUserToSelectDirectory(mainFrame);
        }
        return f;
    }

    public void saveConfig(JFrame mainFrame, TimeSeriousConfig config) throws ConfigManagerException {
        checkInitialized(mainFrame);
        if ( isInitialized()) {
            doSave(config);
        } else {
            logMethods.logInfo("Not saving, config manager not initialized");
        }
    }

    private void doSave(TimeSeriousConfig config) throws ConfigManagerException {
        configManager.saveConfig(MAIN_CONFIG_NAME, config);
    }
}
