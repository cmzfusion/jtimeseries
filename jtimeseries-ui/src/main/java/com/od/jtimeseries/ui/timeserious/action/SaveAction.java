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
package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.config.ConfigAwareTreeManager;
import com.od.jtimeseries.ui.config.ConfigInitializer;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 07/05/11
 * Time: 10:58
 * To change this template use File | Settings | File Templates.
 */
public class SaveAction extends AbstractSaveConfigAction {

    public SaveAction(JFrame mainFrame, ConfigAwareTreeManager configTree, ConfigInitializer configInitializer) {
        super("Save", null, mainFrame, configTree, configInitializer);
        super.putValue(SHORT_DESCRIPTION, "Save config");
    }

    public void actionPerformed(ActionEvent e) {
        confirmAndSaveConfig("Save Config?", JOptionPane.YES_NO_OPTION);
    }

}
