package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.config.UiTimeSeriesConfig;
import com.od.jtimeseries.ui.config.VisualizerConfiguration;
import com.od.jtimeseries.ui.identifiable.DesktopContext;
import com.od.jtimeseries.ui.identifiable.VisualizerContext;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.uicontext.IdentifiableListActionModel;
import com.od.jtimeseries.ui.util.ContextNameCheckUtility;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.swing.action.CompositeActionModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 24/02/12
 * Time: 17:53
 */
public class ShowSeriesInNewVisualizerAction extends AbstractShowSeriesAction<CompositeActionModel> {

    private Component parentComponent;
    private DesktopSelectionActionModel d;
    private VisualizerSelectionActionModel v;
    private IdentifiableListActionModel l;

    public ShowSeriesInNewVisualizerAction(Component parentComponent, DesktopSelectionActionModel d, VisualizerSelectionActionModel v, IdentifiableListActionModel l) {
        super(new CompositeActionModel(d, l), "Show in New Visualizer...", ImageUtils.VISUALIZER_NEW_16x16);
        this.parentComponent = parentComponent;
        this.d = d;
        this.v = v;
        this.l = l;
        super.putValue(SHORT_DESCRIPTION, "Show the selected series in a new visualizer");
    }

    protected boolean isModelStateActionable() {
        return l.isSelectionLimitedToTypes(UIPropertiesTimeSeries.class);
    }

    public void actionPerformed(ActionEvent e) {
        if ( d.isContextSelected() ) {
            DesktopContext desktopContext = d.getSelectedContext();
            java.util.List<UiTimeSeriesConfig> series = getSeriesConfigs(l.getSelected(UIPropertiesTimeSeries.class));
            String name = ContextNameCheckUtility.getNameFromUser(
                parentComponent, desktopContext, "Name for Visualizer?", "Choose Name for Visualizer", ""
            );

            if ( name != null) { //check if user cancelled
                VisualizerConfiguration c = new VisualizerConfiguration(name);
                VisualizerContext visualizerContext = desktopContext.create(c.getTitle(), c.getTitle(), VisualizerContext.class, c);
                visualizerContext.addTimeSeries(series);
                v.setSelectedContext(visualizerContext);
            }
        }
    }

}
