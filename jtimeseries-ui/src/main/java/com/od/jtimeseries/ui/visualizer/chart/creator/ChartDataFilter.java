package com.od.jtimeseries.ui.visualizer.chart.creator;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 15/08/11
 * Time: 17:10
 *
 * Filters which can be applied to clean up data before charting
 * This list should grow - future implementation should be based on chain
 * of filtered series although we can represent the intial TreatNanAsZero filter more
 * easily with a simpler change since its filtering doesn't require removal
 * of timepoints
 */
public enum ChartDataFilter {
    NoFilter,
    NanAsZero
}
