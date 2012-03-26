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
package com.od.jtimeseries.ui.identifiable;

import com.od.jtimeseries.context.impl.SeriesContext;
import com.od.jtimeseries.ui.config.ExportableConfig;
import com.od.jtimeseries.ui.config.ExportableConfigHolder;
import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 12/05/11
 * Time: 08:12
 */
public class DisplayNamesContext extends SeriesContext implements ExportableConfigHolder {

    public static final String DISPLAY_NAME_NODE_NAME = "Display Name Rules";

    private DisplayNameCalculator displayNameCalculator;

    public DisplayNamesContext(DisplayNameCalculator displayNameCalculator) {
        super(DISPLAY_NAME_NODE_NAME, DISPLAY_NAME_NODE_NAME, false);
        this.displayNameCalculator = displayNameCalculator;
    }

    public ExportableConfig getExportableConfig() {
        return displayNameCalculator.getDisplayNamePatternConfig();
    }

    public String getDefaultFileName() {
        return "timeSeriousDisplayNameRules";
    }
}