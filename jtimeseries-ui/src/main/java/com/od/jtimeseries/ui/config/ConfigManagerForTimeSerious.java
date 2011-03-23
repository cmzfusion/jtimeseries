package com.od.jtimeseries.ui.config;

import od.configutil.ConfigManager;
import od.configutil.XStreamSeralizer;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 23/03/11
 * Time: 06:57
 */
public class ConfigManagerForTimeSerious extends ConfigManager {

    public ConfigManagerForTimeSerious() {

        //we'll use the XStreamForJTimeSeries to serialize our config files
        //this aliases some of the jtimeseries class names
        XStreamForJTimeSeries xStream = new XStreamForJTimeSeries();
        setConfigSerializer(new XStreamSeralizer(xStream));
    }
}
