package com.od.jtimeseries.ui.config;

import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;
import od.configutil.ConfigManager;
import od.configutil.ConfigLogImplementation;
import od.configutil.XStreamSeralizer;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 23/03/11
 * Time: 06:57
 */
public class ConfigManagerForTimeSerious extends ConfigManager {

    private LogMethods logMethods = LogUtils.getLogMethods(ConfigManagerForTimeSerious.class);

    public ConfigManagerForTimeSerious() {
        setupLogging();
        //we'll use the XStreamForJTimeSeries to serialize our config files
        //this aliases some of the jtimeseries class names
        XStreamForJTimeSeries xStream = new XStreamForJTimeSeries();
        setConfigSerializer(new XStreamSeralizer(xStream));
    }

    private void setupLogging() {
        ConfigLogImplementation.setLogMethods(new od.configutil.LogMethods() {

            public void info(String s) {
                logMethods.logInfo(s);
            }

            public void error(String description, Throwable cause) {
                logMethods.logError(description, cause);
            }

            public void error(String description) {
                logMethods.logError(description);
            }

            public void debug(String s) {
                logMethods.logDebug(s);
            }

            public void warn(String s) {
                logMethods.logWarning(s);
            }
        });
    }
}
