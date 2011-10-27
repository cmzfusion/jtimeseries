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
package com.od.jtimeseries.ui.timeserious.rootcontext;

import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.identifiable.Identifiable;
import com.od.jtimeseries.identifiable.IdentifiableTreeEvent;
import com.od.jtimeseries.identifiable.IdentifiableTreeListenerAdapter;

import java.util.List;

/**
 * Auto apply display name rules to series in the main selector
 */
class DisplayNameCalculatingTreeListener extends IdentifiableTreeListenerAdapter {

    private DisplayNameCalculator displayNameCalculator;

    public DisplayNameCalculatingTreeListener(DisplayNameCalculator displayNameCalculator) {
        this.displayNameCalculator = displayNameCalculator;
    }

    public void descendantAdded(IdentifiableTreeEvent contextTreeEvent) {
        for ( Identifiable i : contextTreeEvent.getNodes()) {
            final List<UIPropertiesTimeSeries> l = i.findAll(UIPropertiesTimeSeries.class).getAllMatches();
            displayNameCalculator.updateDisplayNames(l);
        }
    }
}
