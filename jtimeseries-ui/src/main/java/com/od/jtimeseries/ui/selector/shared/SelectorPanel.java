/**
 * Copyright (C) 2009 (nick @ objectdefinitions.com)
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
package com.od.jtimeseries.ui.selector.shared;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.identifiable.IdentifiableTreeEvent;
import com.od.swing.action.ListSelectionActionModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 25-May-2009
 * Time: 11:43:35
 * To change this template use File | Settings | File Templates.
 */
public abstract class SelectorPanel<E extends UIPropertiesTimeSeries> extends TitleLabelPanel {

    protected java.util.List<SelectorPanelListener<E>> seriesSelectionListeners = new ArrayList<SelectorPanelListener<E>>();
    private ListSelectionActionModel<E> seriesActionModel;

    public SelectorPanel(ListSelectionActionModel<E> seriesActionModel) {
        this.seriesActionModel = seriesActionModel;
    }

    public void addSelectorListener(SelectorPanelListener<E> seriesSelectionListener) {
        seriesSelectionListeners.add(seriesSelectionListener);
    }

    protected void fireSelectedForDescription(E m) {
        java.util.List<SelectorPanelListener<E>> snapshot = new ArrayList<SelectorPanelListener<E>>(seriesSelectionListeners);
        for ( SelectorPanelListener<E> l : snapshot) {
            l.seriesSelectedForDescription(m);
        }
    }

    protected void fireSelectedForDescription(TimeSeriesContext m) {
        java.util.List<SelectorPanelListener<E>> snapshot = new ArrayList<SelectorPanelListener<E>>(seriesSelectionListeners);
        for ( SelectorPanelListener<E> l : snapshot) {
            l.contextSelectedForDescription(m);
        }
    }

    protected ListSelectionActionModel<E> getSeriesActionModel() {
        return seriesActionModel;
    }

    public static interface SelectorPanelListener<E extends UIPropertiesTimeSeries> {

        void seriesSelectedForDescription(E s);

        void contextSelectedForDescription(TimeSeriesContext m);
    }

    public static class SelectorPanelListenerAdapter<E extends UIPropertiesTimeSeries> implements SelectorPanelListener<E> {

        public void seriesSelectedForDescription(E s) {}

        public void contextSelectedForDescription(TimeSeriesContext m) {}
    }

    public static <E> List<E> getAffectedSeries(Class seriesClass, IdentifiableTreeEvent contextTreeEvent) {
        LinkedList<E> l = new LinkedList<E>();
        for ( Identifiable i : contextTreeEvent.getNodes()) {
            if ( seriesClass.isAssignableFrom(i.getClass())) {
                l.add((E)i);
            }
        }
        return l;
    }
}
