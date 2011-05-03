package com.od.jtimeseries.ui.visualizer;

import com.od.jtimeseries.ui.config.ExportableConfig;
import com.od.jtimeseries.ui.config.ExportableConfigHolder;
import com.od.jtimeseries.util.identifiable.Identifiable;

/**
 * All the information necessary to import an identifiable into this context
 */
public class ImportDetails {
    String path;
    String description;
    Class<? extends Identifiable> localClassType;
    ExportableConfig configObject;

    public ImportDetails(String path, String description, Class<? extends Identifiable> localClassType, ExportableConfig configObject) {
        this.path = path;
        this.description = description;
        this.localClassType = localClassType;
        this.configObject = configObject;
    }

    public String getPath() {
        return path;
    }

    public String getDescription() {
        return description;
    }

    public Class<? extends Identifiable> getLocalClassType() {
        return localClassType;
    }

    public ExportableConfig getConfigObject() {
        return configObject;
    }
}
