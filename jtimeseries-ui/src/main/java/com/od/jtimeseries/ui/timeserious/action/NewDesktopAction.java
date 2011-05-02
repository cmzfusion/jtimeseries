package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.ui.config.DesktopConfiguration;
import com.od.jtimeseries.ui.timeserious.ContextNameCheckUtility;
import com.od.jtimeseries.ui.timeserious.DesktopContext;
import com.od.jtimeseries.ui.util.ImageUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 06/04/11
 * Time: 07:10
 */
public class NewDesktopAction extends AbstractAction {

    private JFrame frame;
    private TimeSeriesContext desktopContainingContext;
    private DesktopSelectionActionModel desktopSelectionActionModel;

    public NewDesktopAction(JFrame frame, TimeSeriesContext desktopContainingContext, DesktopSelectionActionModel desktopSelectionActionModel) {
        super("New Desktop", ImageUtils.DESKTOP_NEW_16x16);
        this.frame = frame;
        this.desktopContainingContext = desktopContainingContext;
        this.desktopSelectionActionModel = desktopSelectionActionModel;
        super.putValue(SHORT_DESCRIPTION, "Create a new desktop frame");
    }

    public void actionPerformed(ActionEvent e) {
        String name = ContextNameCheckUtility.getNameFromUser(frame, desktopContainingContext, "Name for Desktop", "Choose Name", "");
        if ( name != null) { //check if user cancelled
            DesktopConfiguration config = new DesktopConfiguration(name);
            DesktopContext desktopContext = desktopContainingContext.create(name, name, DesktopContext.class, config);
            desktopSelectionActionModel.setSelectedContext(desktopContext);
        }
    }
}
