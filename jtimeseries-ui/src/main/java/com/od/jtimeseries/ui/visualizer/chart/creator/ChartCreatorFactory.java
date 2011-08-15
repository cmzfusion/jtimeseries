package com.od.jtimeseries.ui.visualizer.chart.creator;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 10/08/11
 * Time: 11:19
 */
public class ChartCreatorFactory {

    public AbstractXYChartCreator getChartCreator(ChartType chartType, ChartCreatorParameters p) {
        switch(chartType) {
            case AreaChart:
                return new XYAreaChartCreator(p);
            case LineChart:
                return new XYLineChartCreator(p);
            case ScatterChart:
                return new XYScatterChartCreator(p);
            default:
                throw new UnsupportedOperationException("Unsupported ChartType");
        }

    }

}
