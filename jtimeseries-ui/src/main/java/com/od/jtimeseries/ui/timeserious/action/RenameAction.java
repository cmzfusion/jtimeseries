package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.ui.config.ExportableConfig;
import com.od.jtimeseries.ui.download.panel.TimeSeriesServerContext;
import com.od.jtimeseries.ui.selector.shared.IdentifiableListActionModel;
import com.od.jtimeseries.ui.timeserious.ContextNameCheckUtility;
import com.od.jtimeseries.ui.timeserious.DesktopContext;
import com.od.jtimeseries.ui.timeserious.HideablePeerContext;
import com.od.jtimeseries.ui.timeserious.VisualizerContext;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.jtimeseries.ui.visualizer.AbstractUIRootContext;
import com.od.swing.action.ModelDrivenAction;

import javax.swing.*;
import java.awt.dnd.DnDConstants;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 04/05/11
 * Time: 10:11
 *
 * Rename identifiable by changing the id
 * (as opposed to a logical name whereby the Displayable name changes only)
 */
public class RenameAction extends ModelDrivenAction<IdentifiableListActionModel> {

    private JComponent parent;

    public RenameAction(JComponent parent, IdentifiableListActionModel actionModel) {
        super(actionModel, "Rename", null);
        this.parent = parent;
    }

    public void actionPerformed(ActionEvent e) {
         java.util.List<HideablePeerContext> serverContexts = getActionModel().getSelected(HideablePeerContext.class);
         for ( HideablePeerContext s : serverContexts ) {
            String name = JOptionPane.showInputDialog(
                    SwingUtilities.getRoot(parent),
                    "Rename " + s.getId() + "?",
                    s.getId()
            );
            if ( name != null && name.length() > 0 && ! name.equals(s.getId())) {
                TimeSeriesContext p = s.getParent();
                name = ContextNameCheckUtility.checkName(parent, p, name);
                p.removeChild(s);
                ExportableConfig configuration = s.getConfiguration();
                configuration.setTitle(name);
                HideablePeerContext c = s.newInstance(p, configuration);
                p.addChild(c);
            }
        }
    }

    public boolean isModelStateActionable() {
        return getActionModel().isSelectionLimitedToType(VisualizerContext.class) ||
                getActionModel().isSelectionLimitedToType(DesktopContext.class);
    }
}
