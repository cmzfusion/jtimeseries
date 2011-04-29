package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.event.TimeSeriousBusListener;
import com.od.jtimeseries.ui.event.TimeSeriousBusListenerAdapter;
import com.od.jtimeseries.ui.timeserious.VisualizerContext;
import com.od.swing.eventbus.UIEventBus;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 04/01/11
 * Time: 17:12
 *
 */
public class VisualizerSelectionActionModel extends ContextSelectionActionModel<VisualizerContext> {

    public VisualizerSelectionActionModel() {
         //change the selected desktop when the bus event is sent
        UIEventBus.getInstance().addEventListener(TimeSeriousBusListener.class,
            new TimeSeriousBusListenerAdapter() {
                 public void visualizerSelected(VisualizerContext v) {
                     setSelectedContext(v);
                 }

                 public void visualizerFrameDisposed(VisualizerContext v) {
                     clearActionModelState();
                 }
            }
        );
    }

}
