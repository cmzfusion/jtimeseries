package com.od.jtimeseries.ui.visualizer.chart.creator;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 10/08/11
 * Time: 11:18
 */
public enum ChartType {
    LineChart,
    AreaChart,
    ScatterChart;

    public static final ChartType DEFAULT_CHART_TYPE = AreaChart;

    public static ChartType nextType(ChartType chartType) {
        ChartType[] types = values();
        int index = getIndex(chartType, types);
        return types[ (index + 1) % types.length ];
    }

    private static int getIndex(ChartType chartType, ChartType[] types) {
        int result = -1;
        for ( int loop=0; loop < types.length; loop ++) {
            if ( types[loop] == chartType) {
                result = loop;
                break;
            }
        }
        return result;
    }

}
