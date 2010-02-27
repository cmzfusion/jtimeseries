package com.od.jtimeseries.ui.chart;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 26-Feb-2010
 * Time: 23:39:08
 * To change this template use File | Settings | File Templates.
 */
public enum ChartRangeMode {
    
    SingleRange("Single Range"),
    RangePerId("Range Per Id"),
    RangePerSeries("Range Per Series");

    private String description;

    private ChartRangeMode(String description) {
        this.description = description;
    }

    public String toString() {
        return description;
    }

}
