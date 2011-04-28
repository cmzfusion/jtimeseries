package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.event.TimeSeriousBusListener;
import com.od.jtimeseries.ui.event.TimeSeriousBusListenerAdapter;
import com.od.jtimeseries.ui.timeserious.VisualizerContext;
import com.od.jtimeseries.ui.timeserious.VisualizerInternalFrame;
import com.od.swing.action.AbstractActionModel;
import com.od.swing.eventbus.UIEventBus;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 04/01/11
 * Time: 17:12
 *
 */
public class VisualizerSelectionActionModel extends AbstractActionModel {

    private VisualizerContext selectedVisualizer;

    public VisualizerSelectionActionModel() {
         //change the selected desktop when the bus event is sent
        UIEventBus.getInstance().addEventListener(TimeSeriousBusListener.class,
            new TimeSeriousBusListenerAdapter() {
                 public void visualizerSelected(VisualizerContext v) {
                     setSelectedVisualizer(v);
                 }

                 public void visualizerFrameDisposed(VisualizerContext v) {
                     clearActionModelState();
                 }
            }
        );
    }

    public VisualizerContext getSelectedVisualizer() {
        return selectedVisualizer;
    }

    public void setSelectedVisualizer(VisualizerContext selectedVisualizer) {
        this.selectedVisualizer = selectedVisualizer;
        setModelValid(selectedVisualizer != null);
    }

    @Override
    protected void doClearActionModelState() {
        selectedVisualizer = null;
    }
}
