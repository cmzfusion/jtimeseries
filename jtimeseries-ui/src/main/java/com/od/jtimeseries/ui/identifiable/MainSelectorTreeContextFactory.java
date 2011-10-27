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

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.context.impl.DefaultContextFactory;
import com.od.jtimeseries.ui.config.DesktopConfiguration;
import com.od.jtimeseries.ui.config.VisualizerConfiguration;
import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;

/**
* Created by IntelliJ IDEA.
* User: Nick
* Date: 04/05/11
* Time: 11:34
 */
public class MainSelectorTreeContextFactory extends DefaultContextFactory {

    private DisplayNameCalculator displayNameCalculator;

    public MainSelectorTreeContextFactory(DisplayNameCalculator displayNameCalculator) {
        this.displayNameCalculator = displayNameCalculator;
    }

    public TimeSeriesContext createContext(TimeSeriesContext parent, String id, String description, Class classType, Object... parameters) {
        if ( VisualizerContext.class.isAssignableFrom(classType)) {
            return new VisualizerContext((VisualizerConfiguration)parameters[0]);
        }  else if ( DesktopContext.class.isAssignableFrom(classType)) {
            return new DesktopContext((DesktopConfiguration)parameters[0], displayNameCalculator);
        } else if ( SettingsContext.class.isAssignableFrom(classType)) {
            return new SettingsContext();
        } else if ( DisplayNamesContext.class.isAssignableFrom(classType)) {
            return new DisplayNamesContext(displayNameCalculator);
        } else {
            return super.createContext(parent, id, description, classType, parameters);
        }
    }
}
