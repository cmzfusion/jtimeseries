package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.ui.net.udp.UiTimeSeriesServerDictionary;
import com.od.jtimeseries.ui.selector.SeriesSelectionPanel;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 11/04/11
 * Time: 06:55
 */
public class DefaultDesktopFrame extends AbstractDesktopFrame {

    public DefaultDesktopFrame(UiTimeSeriesServerDictionary serverDictionary, DisplayNameCalculator displayNameCalculator, DesktopContext desktopContext, SeriesSelectionPanel selectionPanel) {
        super(serverDictionary, displayNameCalculator, desktopContext, selectionPanel);
        layoutComponents();
        configureFrame(desktopContext.getDesktopConfiguration());
    }

    private void layoutComponents() {
        getContentPane().add(getDesktopPane(), BorderLayout.CENTER);
    }
}
