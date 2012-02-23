package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.identifiable.DesktopContext;
import com.od.jtimeseries.ui.uicontext.IdentifiableListActionModel;
import com.od.jtimeseries.ui.util.ImageUtils;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 21/02/12
 * Time: 18:44
 */
public class HideDesktopAction extends AbstractHidePeerAction<DesktopContext> {

    public HideDesktopAction(IdentifiableListActionModel actionModel) {
        super(actionModel, "Hide Desktop", ImageUtils.DESKTOP_HIDDEN_16x16, DesktopContext.class);
        setShortDescription();
    }

    public HideDesktopAction(DesktopContext c) {
        super(c, "Hide Desktop", ImageUtils.DESKTOP_HIDDEN_16x16, DesktopContext.class);
        setShortDescription();
    }

    public boolean isModelStateActionable() {
        boolean actionable = super.isModelStateActionable();
        actionable &= ! isMainDesktopSelected();
        return actionable;
    }

    private void setShortDescription() {
        super.putValue(SHORT_DESCRIPTION, "Hide the selected desktop");
    }
}
