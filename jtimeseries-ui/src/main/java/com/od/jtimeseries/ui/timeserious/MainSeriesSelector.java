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

    private TimeSeriesContext rootContext;
    private SeriesSelectionPanel<RemoteHttpTimeSeries> selectionPanel;

    public MainSeriesSelector(TimeSeriesContext rootContext) {
        this.rootContext = rootContext;

        selectionPanel = new SeriesSelectionPanel<RemoteHttpTimeSeries>(
            rootContext,
            RemoteHttpTimeSeries.class
        );

        setLayout(new BorderLayout());
        add(selectionPanel, BorderLayout.CENTER);
    }
}
