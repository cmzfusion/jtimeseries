package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.config.ConfigAwareTreeManager;
import com.od.jtimeseries.ui.config.ConfigInitializer;
import com.od.jtimeseries.ui.config.TimeSeriousConfig;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 06/05/11
 * Time: 13:19
 * To change this template use File | Settings | File Templates.
 */
public class ImportConfigAction extends ExportImportFileAction {

 private ConfigAwareTreeManager configTree;
    private ConfigInitializer configInitializer;
    private JFrame mainFrame;

    public ImportConfigAction(JFrame mainFrame, ConfigAwareTreeManager configTree, ConfigInitializer configInitializer) {
        super("Import Config...", null);
        this.mainFrame = mainFrame;
        this.configTree = configTree;
        this.configInitializer = configInitializer;
        super.putValue(SHORT_DESCRIPTION, "Import config from a file");
    }

    public void actionPerformed(ActionEvent e) {
        final JFileChooser f = getFileChooser();
        int result = f.showOpenDialog(mainFrame);
        if ( result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = f.getSelectedFile();
            TimeSeriousConfig c = configInitializer.importConfig(mainFrame, selectedFile);
            if ( c != null) {
                configTree.clearConfig();
                configTree.restoreConfig(c);
            }
        }
    }
}
