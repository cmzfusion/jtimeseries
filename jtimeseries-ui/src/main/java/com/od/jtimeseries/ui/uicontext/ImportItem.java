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

import com.od.jtimeseries.ui.config.ExportableConfig;
import com.od.jtimeseries.util.identifiable.Identifiable;

/**
 * All the information necessary to import an identifiable into this context
 */
public class ImportItem {

    private String path;
    private String description;
    private Class<? extends Identifiable> localClassType;
    private ExportableConfig configObject;

    public ImportItem(String path, String description, Class<? extends Identifiable> localClassType, ExportableConfig configObject) {
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
