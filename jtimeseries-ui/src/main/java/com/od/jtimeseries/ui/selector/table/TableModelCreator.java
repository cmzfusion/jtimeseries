package com.od.jtimeseries.ui.selector.table;

import com.jidesoft.grid.BeanTableModel;
import com.od.jtimeseries.ui.timeseries.RemoteChartingTimeSeries;

import java.beans.IntrospectionException;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 27-Feb-2010
 * Time: 15:49:53
 * To change this template use File | Settings | File Templates.
 */
public class TableModelCreator {

    public BeanPerRowModel<RemoteChartingTimeSeries> createTableModel() {
        FixedColumnsBeanModel beanTableModel = null;
        try {
            beanTableModel = new FixedColumnsBeanModel(new ArrayList<RemoteChartingTimeSeries>(), RemoteChartingTimeSeries.class);
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }

        BeanPerRowModel<RemoteChartingTimeSeries> modelWrapper = new BeanPerRowModel.JideBeanModelWrapper(beanTableModel);
        PathTokenizingTableModel pathTokenizingTableModel = new PathTokenizingTableModel(modelWrapper);
        SummaryStatsTableModel summaryStatsTableModel = new SummaryStatsTableModel(pathTokenizingTableModel);
        return new EditableColumnsTableModel<RemoteChartingTimeSeries>(summaryStatsTableModel);
    }

}
