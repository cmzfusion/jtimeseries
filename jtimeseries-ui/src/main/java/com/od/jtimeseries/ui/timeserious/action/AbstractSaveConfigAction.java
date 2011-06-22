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
import com.od.jtimeseries.ui.config.TimeSeriousConfig;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;
import od.configutil.ConfigManagerException;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 07/05/11
 * Time: 11:00
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractSaveConfigAction extends AbstractAction {

    private static LogMethods logMethods = LogUtils.getLogMethods(AbstractSaveConfigAction.class);


    private ConfigAwareTreeManager configTree;
    private ConfigInitializer configInitializer;
    private JFrame mainFrame;

    public AbstractSaveConfigAction(String name, Icon icon, JFrame mainFrame, ConfigAwareTreeManager configTree, ConfigInitializer configInitializer) {
        super(name, icon);
        this.mainFrame = mainFrame;
        this.configTree = configTree;
        this.configInitializer = configInitializer;
    }

    /**
     * @return  true, if app may exit
     */
    public boolean confirmAndSaveConfig(String title, int options) {
        int option = JOptionPane.showConfirmDialog(
                mainFrame,
                "Save your config?",
                title,
                options,
                JOptionPane.QUESTION_MESSAGE
        );

        boolean saveOK = true;
        if ( option == JOptionPane.YES_OPTION) {
            saveOK = doSave();
        }

        return option == JOptionPane.CANCEL_OPTION ? false : saveOK;
    }

    private boolean doSave() {
        TimeSeriousConfig config = new TimeSeriousConfig();
        configTree.prepareConfigForSave(config);
        boolean result = true;
        try {
            configInitializer.saveConfig(mainFrame, config);
        } catch (ConfigManagerException e1) {
            logMethods.logError("Failed to save config", e1);
            JOptionPane.showMessageDialog(mainFrame, "Failed to save your config", "Save Failed", JOptionPane.ERROR_MESSAGE);
            result = false;
        }
        return result;
    }
}
