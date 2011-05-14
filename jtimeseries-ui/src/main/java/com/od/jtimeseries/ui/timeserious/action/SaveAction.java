package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.config.ConfigAwareTreeManager;
import com.od.jtimeseries.ui.config.ConfigInitializer;
import com.od.jtimeseries.ui.config.TimeSeriousConfig;
import od.configutil.ConfigManagerException;

import javax.swing.*;
import java.awt.*;
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
