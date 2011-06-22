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
package com.od.jtimeseries.ui.selector.table;

import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.swing.weakreferencelistener.WeakReferenceListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 25/02/11
 * Time: 17:42
 */
public class RowRefreshingTableModel<E> extends AbstractRowLookupTableModel<E> {

    private PropertyChangeListener tableUpdatingStatsPropertyListener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            int row = getRow((E)evt.getSource());
            fireTableRowsUpdated(row, row);
        }
    };

    private WeakReferenceListener statsWeakListener = new WeakReferenceListener(
        UIPropertiesTimeSeries.STATS_REFRESH_TIME_PROPERTY,
        tableUpdatingStatsPropertyListener
    );

    //row rendering is driven from stale property
    private WeakReferenceListener staleWeakListener = new WeakReferenceListener(
        UIPropertiesTimeSeries.STALE_PROPERTY,
        tableUpdatingStatsPropertyListener
    );

    public RowRefreshingTableModel(BeanPerRowModel wrappedModel) {
        super(wrappedModel);
    }

    public void clear() {
        BeanPerRowModel<E> wrappedModel = super.getWrappedModel();
        for ( int row = 0; row < wrappedModel.getRowCount(); row ++) {
            E bean = wrappedModel.getObject(row);
            removePropertyListeners(bean);
        }
        super.clear();
    }

    public void addObjects(List timeSeries) {
        super.addObjects(timeSeries);
        for ( Object o : timeSeries) {
            addPropertyListeners(o);
        }
    }

    public void removeObject(E s) {
        super.removeObject(s);
        removePropertyListeners(s);
    }

    private void removePropertyListeners(Object bean) {
        statsWeakListener.removeListenerFrom(bean);
        staleWeakListener.removeListenerFrom(bean);
    }

    private void addPropertyListeners(Object bean) {
        statsWeakListener.addListenerTo(bean);
        staleWeakListener.addListenerTo(bean);
    }
}
