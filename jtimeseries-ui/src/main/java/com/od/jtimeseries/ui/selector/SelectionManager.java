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
package com.od.jtimeseries.ui.selector;


import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 06-Jan-2009
 * Time: 17:49:42
 */
public interface SelectionManager<E extends UIPropertiesTimeSeries> {
    
    List<E> getSelectedTimeSeries();

    void addSelectionListener(TimeSeriesSelectorListener<E> l);

    void removeSelectionListener(TimeSeriesSelectorListener<E> l);

    void addSelection(E s);

    void removeSelection(E s);

    void setSelectedTimeSeries(List<E> selections);
}
