/**
 * Copyright (C) 2011 (nick @ objectdefinitions.com)
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
package com.od.jtimeseries.ui.selector.tree;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.identifiable.Identifiable;

import java.util.Comparator;

/**
* Created by IntelliJ IDEA.
* User: nick
* Date: 20/12/10
*
* Compare the identifiable nodes in a selector tree
* The Displayable interface is used to get the name for comparison where possible
*/
public class IdentifiableTreeComparator implements Comparator<Identifiable> {
    public int compare(Identifiable o1, Identifiable o2) {
        //sort context folders before series, then by display name
        boolean o1IsContext = o1 instanceof TimeSeriesContext;
        boolean o2IsContext = o2 instanceof TimeSeriesContext;
        if ( o1IsContext != o2IsContext) {
            return o1IsContext ? 1 : -1;
        }
        return SeriesTreeCellRenderer.getDisplayName(o1).compareTo(SeriesTreeCellRenderer.getDisplayName(o2));
    }
}
