package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.net.udp.TimeSeriesServerDictionary;
import com.od.jtimeseries.ui.download.panel.TimeSeriesServerContext;
import com.od.jtimeseries.ui.selector.shared.IdentifiableListActionModel;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.swing.action.ModelDrivenAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 16/03/11
* Time: 06:57
*/
public class RemoveServerAction extends ModelDrivenAction<IdentifiableListActionModel> {

    private JComponent parent;
    private TimeSeriesServerDictionary dictionary;

    public RemoveServerAction(JComponent parent, TimeSeriesServerDictionary dictionary, IdentifiableListActionModel actionModel) {
        super(actionModel, "Remove Server", ImageUtils.TIMESERIES_SERVER_REMOVE_ICON_16x16);
        this.parent = parent;
        this.dictionary = dictionary;
    }

    public void actionPerformed(ActionEvent e) {
        java.util.List<TimeSeriesServerContext> serverContexts = getActionModel().getSelected(TimeSeriesServerContext.class);
        for ( TimeSeriesServerContext s : serverContexts ) {
            int remove = JOptionPane.showConfirmDialog(
                SwingUtilities.getRoot(parent),
                "Remove Server " + s.getServer().getDescription() + ", and all its timeseries?",
                "Remove Server?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            if ( remove == JOptionPane.YES_OPTION) {
                dictionary.removeServer(s.getServer());
            }
        }
    }

    public boolean isModelStateActionable() {
        return getActionModel().isSelectionLimitedToType(TimeSeriesServerContext.class);
    }
}
