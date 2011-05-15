package com.od.jtimeseries.ui.identifiable;

import com.od.jtimeseries.context.impl.DefaultTimeSeriesContext;
import com.od.jtimeseries.ui.config.ExportableConfig;
import com.od.jtimeseries.ui.config.ExportableConfigHolder;
import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 12/05/11
 * Time: 08:12
 */
public class DisplayNamesContext extends DefaultTimeSeriesContext implements ExportableConfigHolder {

    public static final String DISPLAY_NAME_NODE_NAME = "Display Name Rules";

    private DisplayNameCalculator displayNameCalculator;

    public DisplayNamesContext(DisplayNameCalculator displayNameCalculator) {
        super(DISPLAY_NAME_NODE_NAME, DISPLAY_NAME_NODE_NAME, false);
        this.displayNameCalculator = displayNameCalculator;
    }

    public ExportableConfig getExportableConfig() {
        return displayNameCalculator.getDisplayNamePatternConfig();
    }

    public String getDefaultFileName() {
        return "timeSeriousDisplayNameRules";
    }
}