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
public class StatsRefreshingTableModel<E> extends AbstractRowLookupTableModel<E> {

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

    public StatsRefreshingTableModel(BeanPerRowModel wrappedModel) {
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
    }

    private void addPropertyListeners(Object bean) {
        statsWeakListener.addListenerTo(bean);
    }
}
