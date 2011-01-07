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

import javax.swing.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 25-May-2009
 * Time: 11:43:35
 * To change this template use File | Settings | File Templates.
 */
public abstract class SelectorComponent<E extends UIPropertiesTimeSeries> extends TitleLabelPanel {

    private TimeSeriesContext rootContext;
    private IdentifiableListActionModel selectionsActionModel;
    private SelectorActionFactory selectorActionFactory = new SelectorActionFactory() {
        public List<Action> getActions(SelectorComponent s, List<Identifiable> selectedIdentifiable) {
            return Collections.emptyList();
        }
    };

    public SelectorComponent(TimeSeriesContext rootContext, IdentifiableListActionModel selectionsActionModel) {
        this.rootContext = rootContext;
        this.selectionsActionModel = selectionsActionModel;
    }

    public SelectorActionFactory getSelectorActionFactory() {
        return selectorActionFactory;
    }

    public void setSelectorActionFactory(SelectorActionFactory selectorActionFactory) {
        this.selectorActionFactory = selectorActionFactory;
    }

    protected IdentifiableListActionModel getSelectionsActionModel() {
        return selectionsActionModel;
    }

    protected abstract void addContextTreeListener();

    protected abstract void buildView();

    protected void setupSeries() {
        //hold the context tree lock so that the context tree doesn't change while we are constructing the nodes or populating the table
        //add the context listener while we hold the lock to guarantee we receive all subsequent events
        try {
            rootContext.getTreeLock().readLock().lock();
            buildView();
            addContextTreeListener();
        } finally {
            rootContext.getTreeLock().readLock().unlock();
        }
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
