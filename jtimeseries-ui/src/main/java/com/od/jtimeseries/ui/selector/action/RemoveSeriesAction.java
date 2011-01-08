package com.od.jtimeseries.ui.selector.action;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.ui.selector.shared.IdentifiableListActionModel;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.swing.action.ListSelectionActionModel;
import com.od.swing.action.ModelDrivenAction;

import java.awt.event.ActionEvent;
import java.util.List;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
*/
public class RemoveSeriesAction extends ModelDrivenAction<IdentifiableListActionModel> {

    public RemoveSeriesAction(IdentifiableListActionModel seriesSelectionModel) {
        super(seriesSelectionModel, "Remove Series", ImageUtils.REMOVE_ICON_16x16);
    }

    public void actionPerformed(ActionEvent e) {
        List<Identifiable> series = getActionModel().getSelected();
        for ( Identifiable s : series) {
            TimeSeriesContext c = (TimeSeriesContext)s.getParent();
            c.removeChild(s);
        }
    }
}
