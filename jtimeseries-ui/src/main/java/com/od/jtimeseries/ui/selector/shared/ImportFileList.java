package com.od.jtimeseries.ui.selector.shared;

import com.od.jtimeseries.ui.config.ConfigManagerForTimeSerious;
import com.od.jtimeseries.ui.config.ExportableConfig;
import od.configutil.ConfigManager;
import od.configutil.ConfigManagerException;
import od.configutil.FileSource;

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
                    uiConfig = cm.loadConfig("importFromFile", ExportableConfig.class, new FileSource((File)f));
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
