package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.ui.event.TimeSeriousBusListener;
import com.od.jtimeseries.ui.selector.shared.IdentifiableListActionModel;
import com.od.jtimeseries.ui.timeserious.VisualizerNode;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.swing.action.ModelDrivenAction;
import com.od.swing.eventbus.EventSender;
import com.od.swing.eventbus.UIEventBus;

import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18/03/11
 * Time: 06:44
 */
public class RemoveVisualizerAction extends ModelDrivenAction<IdentifiableListActionModel> {

    private Identifiable visualizerContext;

    public RemoveVisualizerAction(IdentifiableListActionModel selectionModel, Identifiable visualizerContext) {
        super(selectionModel, "Remove Visualizer", ImageUtils.SERIES_ICON_16x16);
        this.visualizerContext = visualizerContext;
        super.putValue(SHORT_DESCRIPTION, "Remove the selected visualizer");
    }

     public boolean isModelStateActionable() {
        return getActionModel().isSelectionLimitedToType(VisualizerNode.class);
    }

    public void actionPerformed(ActionEvent e) {
        List<VisualizerNode> nodes = getActionModel().getSelected(VisualizerNode.class);
        for ( final VisualizerNode n : nodes ) {
            UIEventBus.getInstance().fireEvent(TimeSeriousBusListener.class, new EventSender<TimeSeriousBusListener>() {
                public void sendEvent(TimeSeriousBusListener listener) {
                    listener.visualizerRemoved(n.getVisualizerConfiguration(), n.getInternalFrame());
                }
            } );
            visualizerContext.removeChild(n);
        }
    }
}
