package com.od.jtimeseries.ui.selector.action;

import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.swing.action.ListSelectionActionModel;
import com.od.swing.action.ModelDrivenAction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
*/
public class ReconnectSeriesAction<E extends UIPropertiesTimeSeries> extends ModelDrivenAction<ListSelectionActionModel<E>> {

    private JComponent componentToRepaint;

    public ReconnectSeriesAction(JComponent componentToRepaint, ListSelectionActionModel<E> seriesSelectionModel) {
        super(seriesSelectionModel, "Reconnect Time Series to Server", ImageUtils.CONNECT_ICON_16x16);
        this.componentToRepaint = componentToRepaint;
    }

    public void actionPerformed(ActionEvent e) {
        List<E> series = getActionModel().getSelected();
        for ( E s : series) {
           if ( s.isStale()) {
               s.setStale(false);
           }
        }
        componentToRepaint.repaint();
    }

    protected boolean isModelStateActionable() {
        for ( E s : getActionModel().getSelected()) {
            if (s.isStale() ) {
                return true;
            }
        }
        return false;
    }

}
