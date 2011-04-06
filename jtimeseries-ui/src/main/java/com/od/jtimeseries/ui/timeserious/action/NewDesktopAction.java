package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.timeserious.ContextNameCheckUtility;
import com.od.jtimeseries.ui.timeserious.TimeSeriousRootContext;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 06/04/11
 * Time: 07:10
 */
public class NewDesktopAction extends AbstractAction {

    private JFrame mainFrame;
    private TimeSeriousRootContext timeSeriousRootContext;

    public NewDesktopAction(JFrame mainFrame, TimeSeriousRootContext timeSeriousRootContext) {
        super("New Desktop");
        this.mainFrame = mainFrame;
        this.timeSeriousRootContext = timeSeriousRootContext;
        super.putValue(SHORT_DESCRIPTION, "Create a new desktop frame");
    }

    public void actionPerformed(ActionEvent e) {
        String name = ContextNameCheckUtility.getNameFromUser(mainFrame, "Name for Desktop", "Choose Name", "");

    }
}
