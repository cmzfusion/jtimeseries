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
    private TimeSeriesContext desktopContainer;
    private DesktopSelectionActionModel desktopSelectionActionModel;
    private ContextNameCheckUtility nameCheckUtility;

    public NewDesktopAction(JFrame frame, TimeSeriesContext desktopContainer, DesktopSelectionActionModel desktopSelectionActionModel) {
        super("New Desktop", ImageUtils.DESKTOP_NEW_16x16);
        this.frame = frame;
        this.desktopContainer = desktopContainer;
        this.desktopSelectionActionModel = desktopSelectionActionModel;
        super.putValue(SHORT_DESCRIPTION, "Create a new desktop frame");
        nameCheckUtility = new ContextNameCheckUtility(frame, desktopContainer);
    }

    public void actionPerformed(ActionEvent e) {
        String name = nameCheckUtility.getNameFromUser(frame, "Name for Desktop", "Choose Name", "");
        if ( name != null) { //check if user cancelled
            DesktopConfiguration config = new DesktopConfiguration(name);
            DesktopContext desktopContext = new DesktopContext(config);
            desktopContext.setShown(true);
            desktopContainer.addChild(desktopContext);
            desktopSelectionActionModel.setSelectedContext(desktopContext);
        }
    }
}
