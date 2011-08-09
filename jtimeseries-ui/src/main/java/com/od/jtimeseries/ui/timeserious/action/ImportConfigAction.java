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
import com.od.jtimeseries.ui.config.ExportableConfig;
import com.od.jtimeseries.ui.config.TimeSeriousConfig;
import com.od.jtimeseries.ui.timeserious.rootcontext.TimeSeriousRootContext;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 06/05/11
 * Time: 13:19
 * To change this template use File | Settings | File Templates.
 */
public class ImportConfigAction extends ExportImportFileAction {

    private ConfigInitializer configInitializer;
    private JFrame mainFrame;
    private TimeSeriousRootContext rootContext;

    public ImportConfigAction(JFrame mainFrame, TimeSeriousRootContext rootContext, ConfigInitializer configInitializer) {
        super("Import Config...", null);
        this.mainFrame = mainFrame;
        this.rootContext = rootContext;
        this.configInitializer = configInitializer;
        super.putValue(SHORT_DESCRIPTION, "Import config from a file");
    }

    public void actionPerformed(ActionEvent e) {
        final JFileChooser f = getFileChooser();
        int result = f.showOpenDialog(mainFrame);
        if ( result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = f.getSelectedFile();
            ExportableConfig c = configInitializer.importConfig(mainFrame, selectedFile);
            if ( c != null) {
                //import to main desktop by default, so that imported
                //charts will appear there
                rootContext.doImport(mainFrame, Collections.singletonList(c), rootContext.getMainDesktopContext());
            }
        }
    }
}
