/**
 * Copyright (C) 2009 (nick @ objectdefinitions.com)
 *
 * This file is part of JTimeseries.
 *
 * JTimeseries is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JTimeseries is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JTimeseries.  If not, see <http://www.gnu.org/licenses/>.
 */
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
