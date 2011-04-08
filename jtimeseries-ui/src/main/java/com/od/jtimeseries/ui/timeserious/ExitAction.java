package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.config.ConfigAwareTreeManager;
import com.od.jtimeseries.ui.config.ConfigInitializer;
import com.od.jtimeseries.ui.config.TimeSeriousConfig;
import com.od.jtimeseries.ui.util.ImageUtils;
import od.configutil.ConfigManagerException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 08/04/11
* Time: 06:58
*/
public class ExitAction extends AbstractAction {

    private ConfigAwareTreeManager configTree;
    private ConfigInitializer configInitializer;
    private JFrame mainFrame;

    ExitAction(ConfigAwareTreeManager configTree, ConfigInitializer configInitializer) {
        super("Exit", ImageUtils.EXIT_16x16);
        this.configTree = configTree;
        this.configInitializer = configInitializer;
        super.putValue(SHORT_DESCRIPTION, "Exit and save config");
    }

    public void setMainFrame(JFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public void actionPerformed(ActionEvent e) {
        if ( confirmAndSaveConfig(mainFrame) ) {
            System.exit(0);
        }
    }

    public boolean confirmAndSaveConfig(Component c) {
        int option = JOptionPane.showConfirmDialog(
                c,
                "Save Config?",
                "Exit TimeSerious",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if ( option == JOptionPane.YES_OPTION) {
            saveConfigOnShutdown();
        }
        return option != JOptionPane.CANCEL_OPTION;
    }

    private void saveConfigOnShutdown() {
        TimeSeriousConfig config = new TimeSeriousConfig();
        configTree.prepareConfigForSave(config);
        try {
            configInitializer.saveConfig(mainFrame, config);
        } catch (ConfigManagerException e1) {
            //todo, add handling
            e1.printStackTrace();
        }
    }
}
