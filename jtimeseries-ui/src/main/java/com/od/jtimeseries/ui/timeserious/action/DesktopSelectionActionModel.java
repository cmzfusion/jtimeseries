package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.timeserious.TimeSeriousDesktop;
import com.od.swing.action.AbstractActionModel;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 08-Dec-2010
 * Time: 13:07:47
 * To change this template use File | Settings | File Templates.
 */
public class DesktopSelectionActionModel extends AbstractActionModel {

    private TimeSeriousDesktop desktop;

    public DesktopSelectionActionModel() {
    }

    public TimeSeriousDesktop getDesktop() {
        return desktop;
    }

    public void setDesktop(TimeSeriousDesktop desktop) {
        this.desktop = desktop;
        setModelValid(desktop != null);
    }

    @Override
    protected void doClearActionModelState() {
        desktop = null;
    }

    public boolean isDesktopSelected() {
        return desktop != null;
    }
}
