package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.selector.shared.IdentifiableListActionModel;
import com.od.jtimeseries.ui.timeserious.DesktopContext;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.swing.action.ModelDrivenAction;

import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 26/04/11
 * Time: 10:19
 * To change this template use File | Settings | File Templates.
 */
public class RemoveDesktopAction extends ModelDrivenAction<IdentifiableListActionModel> {

    public RemoveDesktopAction(IdentifiableListActionModel selectionModel) {
        super(selectionModel, "Remove Desktop", ImageUtils.DESKTOP_DELETE_16x16);
        super.putValue(SHORT_DESCRIPTION, "Remove the selected desktop");
    }

     public boolean isModelStateActionable() {
        return getActionModel().isSelectionLimitedToType(DesktopContext.class);
    }

    public void actionPerformed(ActionEvent e) {
        List<DesktopContext> nodes = getActionModel().getSelected(DesktopContext.class);
        for ( final DesktopContext n : nodes ) {
            n.setShown(false);
            n.getParent().removeChild(n);
        }
    }
}
