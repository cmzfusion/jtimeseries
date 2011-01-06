package com.od.jtimeseries.ui.selector.action;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.swing.action.ListSelectionActionModel;
import com.od.swing.action.ModelDrivenAction;

import java.awt.event.ActionEvent;
import java.util.List;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
*/
public class RemoveSeriesAction<E extends UIPropertiesTimeSeries> extends ModelDrivenAction<ListSelectionActionModel<E>> {

    public RemoveSeriesAction(ListSelectionActionModel<E> seriesSelectionModel) {
        super(seriesSelectionModel, "Remove Series", ImageUtils.REMOVE_ICON_16x16);
    }

    public void actionPerformed(ActionEvent e) {
        List<E> series = getActionModel().getSelected();
        for ( E s : series) {
            TimeSeriesContext c = (TimeSeriesContext)s.getParent();
            s.setSelected(false);
            c.removeChild(s);
        }
    }
}
