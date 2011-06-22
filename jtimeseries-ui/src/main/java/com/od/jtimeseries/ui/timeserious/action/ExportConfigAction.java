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

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 06/05/11
 * Time: 12:07
 */
public class ExportConfigAction extends ExportImportFileAction {

    private ConfigAwareTreeManager configTree;
    private ConfigInitializer configInitializer;
    private JFrame mainFrame;

    public ExportConfigAction(JFrame mainFrame, ConfigAwareTreeManager configTree, ConfigInitializer configInitializer) {
        super("Export Config...", null);
        this.mainFrame = mainFrame;
        this.configTree = configTree;
        this.configInitializer = configInitializer;
        super.putValue(SHORT_DESCRIPTION, "Export config to a file");
    }

    public void actionPerformed(ActionEvent e) {
        final JFileChooser f = getFileChooser();
        int result = f.showSaveDialog(mainFrame);
        if ( result == JFileChooser.APPROVE_OPTION) {
            TimeSeriousConfig c = new TimeSeriousConfig();
            configTree.prepareConfigForSave(c);
            File selectedFile = f.getSelectedFile();
            selectedFile = appendXmlExtension(selectedFile);
            configInitializer.exportConfig(mainFrame, c, selectedFile);
        }
    }

    private File appendXmlExtension(File selectedFile) {
        if ( ! (selectedFile.getName().endsWith(".xml") || selectedFile.getName().endsWith(".XML"))) {
            selectedFile = new File(selectedFile.getParentFile(), selectedFile.getName() + ".xml");
        }
        return selectedFile;
    }

}
