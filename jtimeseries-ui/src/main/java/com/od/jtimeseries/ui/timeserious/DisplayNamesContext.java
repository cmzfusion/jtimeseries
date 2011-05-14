package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.context.impl.DefaultTimeSeriesContext;
import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 12/05/11
 * Time: 08:12
 */
public class DisplayNamesContext extends DefaultTimeSeriesContext {

    public DisplayNamesContext() {
        super("Display Name Rules", "Display Name Rules", false);
    }
}