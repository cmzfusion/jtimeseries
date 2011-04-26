package com.od.jtimeseries.ui.selector.tree;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.util.identifiable.Identifiable;

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
