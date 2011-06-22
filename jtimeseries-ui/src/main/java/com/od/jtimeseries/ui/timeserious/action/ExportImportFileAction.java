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

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 06/05/11
 * Time: 12:43
 * To change this template use File | Settings | File Templates.
 */
public abstract class ExportImportFileAction extends AbstractAction {
    private static JFileChooser fileChooser;

    public ExportImportFileAction(String name, Icon icon) {
        super(name, icon);
    }

    //sharing the same instance means we keep the directory location
    //each time
    protected JFileChooser getFileChooser() {
        if ( fileChooser == null) {
            fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("XML file", "xml", "XML"));
        }
        return fileChooser;
    }
}
