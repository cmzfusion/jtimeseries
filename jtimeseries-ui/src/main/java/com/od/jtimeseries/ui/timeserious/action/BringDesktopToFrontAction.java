package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.identifiable.DesktopContext;
import com.od.jtimeseries.ui.uicontext.IdentifiableListActionModel;
import com.od.jtimeseries.ui.util.ImageUtils;

import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 22/02/12
 * Time: 10:27
 */
public class BringDesktopToFrontAction extends CheckAllSelectedActionableAction<DesktopContext> {

    public BringDesktopToFrontAction(IdentifiableListActionModel actionModel,Class<DesktopContext> clazz) {
        super(actionModel, "Bring To Front", ImageUtils.DESKTOP_SHOW_16x16, clazz);
        setShortDescription();
    }

    public BringDesktopToFrontAction(DesktopContext c) {
        super(new IdentifiableListActionModel(Collections.singletonList(c)), "Bring to Front", ImageUtils.DESKTOP_SHOW_16x16, DesktopContext.class);
        setShortDescription();
    }

    private void setShortDescription() {
        super.putValue(SHORT_DESCRIPTION, "Bring desktop to Front");
    }

    protected boolean isActionable(DesktopContext n) {
        return ! n.isHidden();
    }

    protected void doAction(DesktopContext n) {
        n.bringToFront();
    }
}
