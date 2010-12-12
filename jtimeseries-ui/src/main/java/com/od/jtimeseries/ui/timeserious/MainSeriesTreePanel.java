package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.JTimeSeries;
import com.od.jtimeseries.ui.selector.SeriesSelectionPanel;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 24-Nov-2010
 * Time: 09:36:25
 * To change this template use File | Settings | File Templates.
 */
public class MainSeriesTreePanel extends JPanel {

    private SeriesSelectionPanel<UIPropertiesTimeSeries> selectionPanel = new SeriesSelectionPanel<UIPropertiesTimeSeries>(
       JTimeSeries.createRootContext(),
       UIPropertiesTimeSeries.class
    );

    public MainSeriesTreePanel() {
        add(selectionPanel);
    }

}
