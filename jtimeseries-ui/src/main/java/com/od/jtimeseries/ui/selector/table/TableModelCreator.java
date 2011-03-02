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


import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;

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

    public <E extends UIPropertiesTimeSeries> BeanPerRowModel<E> createTableModel(Class<E> clazz) {
        FixedColumnsBeanModel<E> beanTableModel = null;
        try {
            beanTableModel = new FixedColumnsBeanModel<E>(new ArrayList<E>(), clazz);
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }

        BeanPerRowModel<E> modelWrapper = new BeanPerRowModel.JideBeanModelWrapper<E>(beanTableModel);
        RowRefreshingTableModel<E> statsRefreshingModel = new RowRefreshingTableModel<E>(modelWrapper);
        PathTokenizingTableModel<E> pathTokenizingTableModel = new PathTokenizingTableModel<E>(statsRefreshingModel);
        SummaryStatsTableModel<E> summaryStatsTableModel = new SummaryStatsTableModel<E>(pathTokenizingTableModel);
        return new EditableColumnsTableModel<E>(summaryStatsTableModel);
    }

}
