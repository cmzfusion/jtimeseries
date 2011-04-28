package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.selector.shared.IdentifiableListActionModel;
import com.od.jtimeseries.ui.timeserious.DesktopContext;
import com.od.jtimeseries.ui.util.ImageUtils;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 28/04/11
 * Time: 15:55
 * To change this template use File | Settings | File Templates.
 */
public class ShowHiddenDesktopAction extends AbstractShowHiddenPeerAction {

    public ShowHiddenDesktopAction(IdentifiableListActionModel actionModel) {
        super(actionModel, "Show Desktop", ImageUtils.DESKTOP_SHOW_16x16, DesktopContext.class);
        super.putValue(SHORT_DESCRIPTION, "Restore the selected desktop");
    }
}
