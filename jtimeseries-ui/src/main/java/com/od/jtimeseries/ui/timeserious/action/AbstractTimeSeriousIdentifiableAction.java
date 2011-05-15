package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.config.DesktopConfiguration;
import com.od.jtimeseries.ui.identifiable.DesktopContext;
import com.od.jtimeseries.ui.uicontext.IdentifiableListActionModel;
import com.od.swing.action.ModelDrivenAction;

import javax.swing.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 11/05/11
 * Time: 12:52
 */
public abstract class AbstractTimeSeriousIdentifiableAction extends ModelDrivenAction<IdentifiableListActionModel> {

    public AbstractTimeSeriousIdentifiableAction(IdentifiableListActionModel actionModel, String name, ImageIcon imageIcon) {
        super(actionModel, name, imageIcon);
    }

    protected boolean isMainDesktopSelected() {
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
