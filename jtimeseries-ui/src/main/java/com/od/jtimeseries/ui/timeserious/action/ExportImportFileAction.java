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
