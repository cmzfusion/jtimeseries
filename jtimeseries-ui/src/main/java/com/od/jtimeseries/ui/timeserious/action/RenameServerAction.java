package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.identifiable.TimeSeriesServerContext;
import com.od.jtimeseries.ui.uicontext.IdentifiableListActionModel;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.swing.action.ModelDrivenAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 16/03/11
* Time: 06:56
*/
public class RenameServerAction extends ModelDrivenAction<IdentifiableListActionModel> {

    private JComponent parent;

    public RenameServerAction(JComponent parent, IdentifiableListActionModel actionModel) {
        super(actionModel, "Rename Server", ImageUtils.TIMESERIES_SERVER_RENAME_ICON_16x16);
        this.parent = parent;
    }

    public void actionPerformed(ActionEvent e) {
         java.util.List<TimeSeriesServerContext> serverContexts = getActionModel().getSelected(TimeSeriesServerContext.class);
         for ( TimeSeriesServerContext s : serverContexts ) {
            String name = JOptionPane.showInputDialog(
                    SwingUtilities.getRoot(parent),
                    "Rename Server " + s.getServer().getDescription() + "?",
                    s.getServer().getDescription()
            );
            if ( name != null && name.length() > 0) {
                s.getServer().setDescription(name);
            }
        }
    }

    public boolean isModelStateActionable() {
        return getActionModel().isSelectionLimitedToTypes(TimeSeriesServerContext.class);
    }
}
