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
package com.od.jtimeseries.ui.config;

import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;
import od.configutil.util.ConfigLogImplementation;
import od.configutil.ConfigManager;
import od.configutil.serializer.XStreamSeralizer;

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
        ConfigLogImplementation.setLogMethods(new od.configutil.util.LogMethods() {

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
