package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.JTimeSeries;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.ui.selector.SeriesSelectionPanel;
import com.od.jtimeseries.ui.timeseries.RemoteHttpTimeSeries;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 24-Nov-2010
 * Time: 09:36:25
 */
public class MainSeriesSelector extends JPanel {

    private SeriesSelectionPanel<UIPropertiesTimeSeries> selectionPanel;

    public MainSeriesSelector(TimeSeriesContext rootContext) {
        selectionPanel = new SeriesSelectionPanel<UIPropertiesTimeSeries>(
            rootContext,
            UIPropertiesTimeSeries.class
        );
        selectionPanel.setSeriesSelectionEnabled(false);

        setLayout(new BorderLayout());
        add(selectionPanel, BorderLayout.CENTER);
    }
}
