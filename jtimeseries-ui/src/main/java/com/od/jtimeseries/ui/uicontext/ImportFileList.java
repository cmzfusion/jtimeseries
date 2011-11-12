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

import com.od.jtimeseries.ui.config.ConfigManagerForTimeSerious;
import com.od.jtimeseries.ui.config.ExportableConfig;
import od.configutil.ConfigManager;
import od.configutil.util.ConfigManagerException;
import od.configutil.sinkandsource.FileSource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 02/05/11
 * Time: 21:45
 *
 * A mechanism to try to make sure we only do the actual conversion /
 * config load once during a drag drop import operation
 */
public class ImportFileList {

    private static WeakHashMap<File,ExportableConfigWrapper> weakMap = new WeakHashMap<File, ExportableConfigWrapper>();

    public synchronized static List<ExportableConfig> getConfigs(List<File> fileList) {
        List<ExportableConfig> c = new ArrayList<ExportableConfig>();
        ConfigManager cm = new ConfigManagerForTimeSerious();
        for ( File f : fileList) {
             try {
                ExportableConfig uiConfig;
                ExportableConfigWrapper w = weakMap.get(f);
                if (w != null && w.lastModifiedTime == f.lastModified()) {
                    uiConfig = w.config;
                } else {
                    uiConfig = cm.loadConfig("importFromFile", ExportableConfig.class, new FileSource(f));
                    weakMap.put(f, new ExportableConfigWrapper(f.lastModified(), uiConfig));
                }
                c.add(uiConfig);
            } catch (ConfigManagerException e) {
                e.printStackTrace();
            }
        }
        return c;
    }

    private static class ExportableConfigWrapper {
        long lastModifiedTime;
        ExportableConfig config;

        private ExportableConfigWrapper(long lastModifiedTime, ExportableConfig config) {
            this.lastModifiedTime = lastModifiedTime;
            this.config = config;
        }
    }
}
