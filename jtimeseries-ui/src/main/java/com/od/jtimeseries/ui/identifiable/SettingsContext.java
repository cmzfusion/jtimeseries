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

import com.od.jtimeseries.context.impl.DefaultTimeSeriesContext;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 09/05/11
 * Time: 09:20
 * To change this template use File | Settings | File Templates.
 */
public class SettingsContext extends DefaultTimeSeriesContext {

    public static final String SETTINGS_NODE_NAME = "Settings";

    public SettingsContext() {
        super(SETTINGS_NODE_NAME, SETTINGS_NODE_NAME, false);
    }
}
