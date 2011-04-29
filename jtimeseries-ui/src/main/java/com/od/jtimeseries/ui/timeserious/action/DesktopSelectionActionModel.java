package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.event.TimeSeriousBusListener;
import com.od.jtimeseries.ui.event.TimeSeriousBusListenerAdapter;
import com.od.jtimeseries.ui.timeserious.DesktopContext;
import com.od.swing.eventbus.UIEventBus;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 08-Dec-2010
 * Time: 13:07:47
 * To change this template use File | Settings | File Templates.
 */
public class DesktopSelectionActionModel extends ContextSelectionActionModel<DesktopContext> {

    public DesktopSelectionActionModel() {

        //change the selected desktop when the bus event is sent
        UIEventBus.getInstance().addEventListener(TimeSeriousBusListener.class,
            new TimeSeriousBusListenerAdapter() {
                 public void desktopSelected(DesktopContext desktopPane) {
                     setSelectedContext(desktopPane);
                 }

                 public void desktopDisposed(DesktopContext desktopPane) {
                     clearActionModelState();
                 }
            }
        );
    }

}
