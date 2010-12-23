package com.od.jtimeseries.ui.selector.tree;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.util.identifiable.Identifiable;

import java.util.Comparator;

/**
* Created by IntelliJ IDEA.
* User: nick
* Date: 20/12/10
* <p/>
* To change this template use File | Settings | File Templates.
*/
class IdentifiableTreeComparator implements Comparator<Identifiable> {
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
