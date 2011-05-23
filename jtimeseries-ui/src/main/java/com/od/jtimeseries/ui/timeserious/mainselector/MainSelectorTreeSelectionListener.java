package com.od.jtimeseries.ui.timeserious.mainselector;

import com.od.jtimeseries.ui.event.TimeSeriousBusListener;
import com.od.jtimeseries.ui.identifiable.DesktopContext;
import com.od.jtimeseries.ui.identifiable.VisualizerContext;
import com.od.jtimeseries.ui.selector.SeriesSelectionPanel;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.uicontext.IdentifiableListActionModel;
import com.od.swing.action.ActionModelListener;
import com.od.swing.eventbus.EventSender;
import com.od.swing.eventbus.UIEventBus;

/**
* Created by IntelliJ IDEA.
* User: Nick
* Date: 28/04/11
* Time: 21:52
* To change this template use File | Settings | File Templates.
*
* Fire desktop selected and visualizer selected events if a single desktop or visualizer
* is selected in the main selector tree
*/
class MainSelectorTreeSelectionListener implements ActionModelListener {

    private SeriesSelectionPanel<UIPropertiesTimeSeries> selectionPanel;

    public MainSelectorTreeSelectionListener(SeriesSelectionPanel<UIPropertiesTimeSeries> selectionPanel) {
        this.selectionPanel = selectionPanel;
    }

    public void actionStateUpdated() {
        final IdentifiableListActionModel actionModel = selectionPanel.getSelectionActionModel();
        if ( actionModel.getSelected().size() == 1) {
            if ( actionModel.isSelectionLimitedToTypes(DesktopContext.class)) {
                UIEventBus.getInstance().fireEvent(TimeSeriousBusListener.class,
                new EventSender<TimeSeriousBusListener>() {
                    public void sendEvent(TimeSeriousBusListener listener) {
                        listener.desktopSelected((DesktopContext)actionModel.getSelected().get(0));
                    }
                });
            } else if ( actionModel.isSelectionLimitedToTypes(VisualizerContext.class)) {
                UIEventBus.getInstance().fireEvent(TimeSeriousBusListener.class,
                new EventSender<TimeSeriousBusListener>() {
                    public void sendEvent(TimeSeriousBusListener listener) {
                        listener.visualizerSelected((VisualizerContext) actionModel.getSelected().get(0));
                    }
                });
            }
        }
    }
}
