package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.ui.download.panel.LoadSeriesFromServerCommand;
import com.od.jtimeseries.ui.identifiable.TimeSeriesServerContext;
import com.od.jtimeseries.ui.uicontext.IdentifiableListActionModel;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.swing.action.ModelDrivenAction;

import java.awt.event.ActionEvent;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 16/03/11
* Time: 06:58
*/
public class RefreshServerSeriesAction extends ModelDrivenAction<IdentifiableListActionModel> {

    private TimeSeriesContext rootContext;

    public RefreshServerSeriesAction(TimeSeriesContext rootContext, IdentifiableListActionModel actionModel) {
        super(actionModel, "Refresh Series from Server", ImageUtils.TIMESERIES_SERVER_REFRESH_ICON_16x16);
        this.rootContext = rootContext;
    }

    public void actionPerformed(ActionEvent e) {
        java.util.List<TimeSeriesServerContext> serverContexts = getActionModel().getSelected(TimeSeriesServerContext.class);
        LoadSeriesFromServerCommand l = new LoadSeriesFromServerCommand(rootContext);
        for ( TimeSeriesServerContext c : serverContexts ) {
            l.execute(c.getServer());
        }
    }

    public boolean isModelStateActionable() {
        return getActionModel().isSelectionLimitedToType(TimeSeriesServerContext.class);
    }
}
