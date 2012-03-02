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
import com.od.jtimeseries.ui.identifiable.TimeSeriesServerContext;
import com.od.jtimeseries.ui.util.ImageUtils;

import javax.swing.*;

/**
* Created by IntelliJ IDEA.
* User: nick
* Date: 20/12/10
* <p/>
* To change this template use File | Settings | File Templates.
*/
class ContextTreeNode extends AbstractIdentifiableTreeNode {

    private TimeSeriesContext context;

    public ContextTreeNode(TimeSeriesContext context) {
        this.context = context;
    }

    public String toString() {
        return context.toString();
    }

    public TimeSeriesContext getIdentifiable() {
        return context;
    }

    protected Icon getIcon() {
        return context instanceof TimeSeriesServerContext ? ImageUtils.TIMESERIES_SERVER_ICON_16x16 : ImageUtils.CONTEXT_ICON_16x16;
    }
}
