package com.od.jtimeseries.ui.selector.table;

import com.jidesoft.grid.BeanTableModel;
import com.od.jtimeseries.ui.timeseries.RemoteChartingTimeSeries;

import java.util.ArrayList;
import java.util.LinkedList;
import java.beans.IntrospectionException;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 28-Feb-2010
 * Time: 18:17:43
 * To change this template use File | Settings | File Templates.
 */
public class FixedColumnsBeanModel extends BeanTableModel<RemoteChartingTimeSeries> {

    public FixedColumnsBeanModel(ArrayList<RemoteChartingTimeSeries> arrayList, Class<RemoteChartingTimeSeries> remoteChartingTimeSeriesClass) throws IntrospectionException {
        super(arrayList, remoteChartingTimeSeriesClass, generateColumnConfigStringForBeanTableModel());
    }

      //jide BeanTableModel requires the propertyNames and column display names as a String[]
    private static String[] generateColumnConfigStringForBeanTableModel() {
        java.util.List<String> colConfigStrings = new LinkedList<String>();
        for(FixedColumns c : FixedColumns.values()) {
            colConfigStrings.add(c.getPropertyName());
            colConfigStrings.add(c.getColumnName());
        }
        String[] colConfigString = colConfigStrings.toArray(new String[colConfigStrings.size()]);
        return colConfigString;
    }

    public String getColumnDescription(int colIndex) {
        return FixedColumns.values()[colIndex].getDescription();
    }
}
