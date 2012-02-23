package com.od.jtimeseries.ui.timeserious.mainselector;

import com.od.jtimeseries.identifiable.Identifiable;
import com.od.jtimeseries.ui.identifiable.DesktopContext;
import com.od.jtimeseries.ui.identifiable.SettingsContext;
import com.od.jtimeseries.ui.identifiable.TimeSeriesServerContext;
import com.od.jtimeseries.ui.selector.tree.IdentifiableTreeComparator;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 21/02/12
* Time: 10:22
*/
public class MainSelectorTreeComparator extends IdentifiableTreeComparator {
    public int compare(Identifiable o1, Identifiable o2) {
        //sort server context first
        boolean o1IsServerContext = o1 instanceof TimeSeriesServerContext;
        boolean o2IsServerContext = o2 instanceof TimeSeriesServerContext;
        if ( o1IsServerContext != o2IsServerContext) {
            return o1IsServerContext ? -1 : 1;
        }

        boolean o1IsSettings = o1 instanceof SettingsContext;
        boolean o2IsSettings = o2 instanceof SettingsContext;              if ( o1IsSettings != o2IsSettings) {
            return o1IsSettings ? 1 : -1;
        }

        boolean o1IsMainDesktop = o1 instanceof DesktopContext && ((DesktopContext)o1).isMainDesktopContext();
        boolean o2IsMainDesktop = o2 instanceof DesktopContext && ((DesktopContext)o2).isMainDesktopContext();
        if ( o1IsMainDesktop != o2IsMainDesktop &&
        o1 instanceof DesktopContext && o2 instanceof DesktopContext) {
            return o1IsMainDesktop ? -1 : 1;
        }

        return super.compare(o1, o2);
    }
}
