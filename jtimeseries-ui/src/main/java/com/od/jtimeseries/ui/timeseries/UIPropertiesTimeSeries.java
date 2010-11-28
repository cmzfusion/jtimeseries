package com.od.jtimeseries.ui.timeseries;

import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;

import java.beans.PropertyChangeListener;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 27-Nov-2010
 * Time: 23:57:31
 * To change this template use File | Settings | File Templates.
 */
public interface UIPropertiesTimeSeries extends IdentifiableTimeSeries {

    String SELECTED_PROPERTY = "selected";
    String STALE_PROPERTY = "stale";

    boolean isSelected();

    void setSelected(boolean selected);

    boolean isStale();

    void setStale(boolean stale);

    void addPropertyChangeListener(String selectedProperty, PropertyChangeListener selectionPropertyListener);

    void removePropertyChangeListener(PropertyChangeListener selectionPropertyListener);
}
