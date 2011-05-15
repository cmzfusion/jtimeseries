package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.identifiable.IdentifiableTreeEvent;
import com.od.jtimeseries.util.identifiable.IdentifiableTreeListenerAdapter;

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
