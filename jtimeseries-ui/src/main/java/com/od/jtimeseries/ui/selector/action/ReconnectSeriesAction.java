package com.od.jtimeseries.ui.selector.action;

import com.od.jtimeseries.ui.selector.shared.IdentifiableListActionModel;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.swing.action.ListSelectionActionModel;
import com.od.swing.action.ModelDrivenAction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
*/
public class ReconnectSeriesAction extends ModelDrivenAction<IdentifiableListActionModel> {

    public ReconnectSeriesAction(IdentifiableListActionModel selectionModel) {
        super(selectionModel, "Reconnect Time Series to Server", ImageUtils.CONNECT_ICON_16x16);
    }

    public void actionPerformed(ActionEvent e) {
        List<Identifiable> series = getActionModel().getSelected();
        for ( Identifiable s : series) {
           if ( s instanceof UIPropertiesTimeSeries) {
               if (((UIPropertiesTimeSeries)s).isStale()) {
                    ((UIPropertiesTimeSeries)s).setStale(false);
               }
           }
        }
    }

    protected boolean isModelStateActionable() {
        boolean result = false;
        if (getActionModel().isSelectionLimitedToType(UIPropertiesTimeSeries.class) ) {
            result = true;
            //actionable if all selected are stale
            for (Identifiable i : getActionModel().getSelected()) {
                result &= ((UIPropertiesTimeSeries)i).isStale();
            }
        }
        return result;
    }

}
