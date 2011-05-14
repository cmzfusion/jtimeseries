package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.context.impl.DefaultContextFactory;
import com.od.jtimeseries.ui.config.DesktopConfiguration;
import com.od.jtimeseries.ui.config.VisualizerConfiguration;
import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.util.identifiable.Identifiable;

/**
* Created by IntelliJ IDEA.
* User: Nick
* Date: 04/05/11
* Time: 11:34
 */
class MainSelectorTreeContextFactory extends DefaultContextFactory {

    private DisplayNameCalculator displayNameCalculator;

    public MainSelectorTreeContextFactory(DisplayNameCalculator displayNameCalculator) {

        this.displayNameCalculator = displayNameCalculator;
    }

    public <E extends Identifiable> E createContext(TimeSeriesContext parent, String id, String description, Class<E> classType, Object... parameters) {
        if ( VisualizerContext.class.isAssignableFrom(classType)) {
            return (E)new VisualizerContext((VisualizerConfiguration)parameters[0]);
        }  else if ( DesktopContext.class.isAssignableFrom(classType)) {
            return (E)new DesktopContext((DesktopConfiguration)parameters[0], displayNameCalculator);
        } else if ( SettingsContext.class.isAssignableFrom(classType)) {
            return (E)new SettingsContext();
        } else if ( DisplayNamesContext.class.isAssignableFrom(classType)) {
            return (E)new DisplayNamesContext(displayNameCalculator);
        } else {
            return super.createContext(parent, id, description, classType, parameters);
        }
    }
}
