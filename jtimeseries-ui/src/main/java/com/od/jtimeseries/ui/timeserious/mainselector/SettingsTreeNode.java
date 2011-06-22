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
package com.od.jtimeseries.ui.timeserious.mainselector;

import com.od.jtimeseries.ui.identifiable.SettingsContext;
import com.od.jtimeseries.ui.selector.tree.AbstractSeriesSelectionTreeNode;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.jtimeseries.util.identifiable.Identifiable;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 09/05/11
 * Time: 09:20
 */
public class SettingsTreeNode extends AbstractSeriesSelectionTreeNode {

    private SettingsContext settingsContext;

    public SettingsTreeNode(SettingsContext settingsContext) {
        this.settingsContext = settingsContext;
    }

    public Identifiable getIdentifiable() {
        return settingsContext;
    }

    protected Icon getIcon() {
        return ImageUtils.SETTINGS_16x16;
    }
}
