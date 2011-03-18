package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.event.TimeSeriousBusListener;
import com.od.jtimeseries.ui.selector.shared.IdentifiableListActionModel;
import com.od.jtimeseries.ui.timeserious.VisualizerNode;
import com.od.jtimeseries.ui.timeserious.action.VisualizerSelectionActionModel;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.swing.action.ModelDrivenAction;
import com.od.swing.eventbus.EventSender;
import com.od.swing.eventbus.UIEventBus;

import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 16/03/11
 * Time: 07:05
 */
public class ShowHiddenVisualizerAction extends ModelDrivenAction<IdentifiableListActionModel> {

    public ShowHiddenVisualizerAction(IdentifiableListActionModel actionModel) {
        super(actionModel, "Show Visualizer", ImageUtils.SERIES_ICON_16x16);
        super.putValue(SHORT_DESCRIPTION, "Restore the selected visualizer to the desktop pane");
    }

    public boolean isModelStateActionable() {
        return getActionModel().isSelectionLimitedToType(VisualizerNode.class);
    }

    public void actionPerformed(ActionEvent e) {
        List<VisualizerNode> nodes = getActionModel().getSelected(VisualizerNode.class);
        for ( final VisualizerNode n : nodes ) {
            if ( n.isVisualizerHidden() ) {
                UIEventBus.getInstance().fireEvent(TimeSeriousBusListener.class, new EventSender<TimeSeriousBusListener>() {
                    public void sendEvent(TimeSeriousBusListener listener) {
                        listener.visualizerImportedFromConfig(n.getVisualizerConfiguration());
                    }
                } );
            }
        }
    }

}
