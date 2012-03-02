package com.od.jtimeseries.ui.timeserious.action;

import com.od.jtimeseries.ui.config.UiTimeSeriesConfig;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.swing.action.ActionModel;
import com.od.swing.action.CompositeActionModel;
import com.od.swing.action.ModelDrivenAction;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 28/02/12
 * Time: 09:16
 */
public abstract class AbstractShowSeriesAction<E extends ActionModel> extends ModelDrivenAction<E> {

    public static final int MAX_SERIES_TO_ENABLE_FOR_CHARTING = 5;

    public AbstractShowSeriesAction(E actionModel, String name, ImageIcon imageIcon) {
        super(actionModel, name, imageIcon);
    }

    protected List<UiTimeSeriesConfig> getSeriesConfigs(List<UIPropertiesTimeSeries> selectedSeries) {
        List<UiTimeSeriesConfig> series = new ArrayList<UiTimeSeriesConfig>();
        for ( UIPropertiesTimeSeries s : selectedSeries) {
            UiTimeSeriesConfig c = s.getConfig();
            if ( selectedSeries.size() <= MAX_SERIES_TO_ENABLE_FOR_CHARTING) {
                c.setSelected(true); //series charted by default
            }
            series.add(c);
        }
        return series;
    }
}
