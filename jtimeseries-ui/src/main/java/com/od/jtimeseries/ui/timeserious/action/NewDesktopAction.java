package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.ui.config.DesktopConfiguration;
import com.od.jtimeseries.ui.identifiable.DesktopContext;
import com.od.jtimeseries.ui.util.ContextNameCheckUtility;
import com.od.jtimeseries.ui.util.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 06/04/11
 * Time: 07:10
 */
public class NewDesktopAction extends AbstractAction {

    private Component component;
    private TimeSeriesContext desktopContainingContext;
    private DesktopSelectionActionModel desktopSelectionActionModel;

    public NewDesktopAction(Component component, TimeSeriesContext desktopContainingContext, DesktopSelectionActionModel desktopSelectionActionModel) {
        super("New Desktop", ImageUtils.DESKTOP_NEW_16x16);
        this.component = component;
        this.desktopContainingContext = desktopContainingContext;
        this.desktopSelectionActionModel = desktopSelectionActionModel;
        super.putValue(SHORT_DESCRIPTION, "Create a new desktop component");
    }

    public void actionPerformed(ActionEvent e) {
        String name = ContextNameCheckUtility.getNameFromUser(component, desktopContainingContext, "Name for Desktop", "Choose Name", "");
        if ( name != null) { //check if user cancelled
            DesktopConfiguration config = new DesktopConfiguration(name);
            DesktopContext desktopContext = desktopContainingContext.create(name, name, DesktopContext.class, config);
            desktopSelectionActionModel.setSelectedContext(desktopContext);
        }
    }
}
