package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.event.TimeSeriousBusListener;
import com.od.jtimeseries.ui.event.TimeSeriousBusListenerAdapter;
import com.od.jtimeseries.ui.timeserious.TimeSeriousDesktopPane;
import com.od.swing.action.AbstractActionModel;
import com.od.swing.eventbus.UIEventBus;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 08-Dec-2010
 * Time: 13:07:47
 * To change this template use File | Settings | File Templates.
 */
public class DesktopSelectionActionModel extends AbstractActionModel {

    private TimeSeriousDesktopPane desktop;

    public DesktopSelectionActionModel() {

        //change the selected desktop when the bus event is sent
        UIEventBus.getInstance().addEventListener(TimeSeriousBusListener.class,
            new TimeSeriousBusListenerAdapter() {
                 public void desktopSelected(TimeSeriousDesktopPane desktopPane) {
                     setDesktop(desktopPane);
                 }

                 public void desktopDisposed(TimeSeriousDesktopPane desktopPane) {
                     clearActionModelState();
                 }
            }
        );
    }

    public TimeSeriousDesktopPane getDesktop() {
        return desktop;
    }

    public void setDesktop(TimeSeriousDesktopPane desktop) {
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
