package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.config.DesktopConfiguration;
import com.od.jtimeseries.ui.selector.shared.IdentifiableListActionModel;
import com.od.jtimeseries.ui.timeserious.DesktopContext;
import com.od.jtimeseries.ui.timeserious.HideablePeerContext;
import com.od.jtimeseries.ui.util.ImageUtils;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 26/04/11
 * Time: 10:19
 * To change this template use File | Settings | File Templates.
 */
public class RemoveDesktopAction extends AbstractRemoveHideablePeerAction {

    public RemoveDesktopAction(IdentifiableListActionModel selectionModel) {
        super(selectionModel, "Remove Desktop", ImageUtils.DESKTOP_DELETE_16x16, DesktopContext.class);
        super.putValue(SHORT_DESCRIPTION, "Remove the selected desktop");
    }

    public boolean isModelStateActionable() {
        boolean actionable = super.isModelStateActionable();
        actionable &= ! isMainDesktopSelected();
        return actionable;
    }

    public boolean isMainDesktopSelected() {
        List<DesktopContext> nodes = getActionModel().getSelected(DesktopContext.class);
        boolean result = false;
        for ( final DesktopContext n : nodes ) {
            if ( n.getId().equals(DesktopConfiguration.MAIN_DESKTOP_NAME)) {
                result = true;
                break;
            }
        }
        return result;
    }
}
