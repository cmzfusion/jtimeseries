package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.context.impl.DefaultTimeSeriesContext;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 09/05/11
 * Time: 09:20
 * To change this template use File | Settings | File Templates.
 */
public class SettingsContext extends DefaultTimeSeriesContext {

    public static final String SETTINGS_NODE_NAME = "Settings";

    public SettingsContext() {
        super(SETTINGS_NODE_NAME, SETTINGS_NODE_NAME, false);
    }
}
