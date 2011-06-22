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
package com.od.jtimeseries.ui.selector.shared;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.uicontext.IdentifiableListActionModel;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.identifiable.IdentifiableTreeEvent;

import javax.swing.*;
import java.util.LinkedHashSet;
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
    private SelectorPopupMenuPopulator popupMenuPopulator = new SelectorPopupMenuPopulator() {
        public void addMenuItems(JPopupMenu menu, SelectorComponent s, List<Identifiable> selectedIdentifiable) {
        }
    };

    public SelectorComponent(TimeSeriesContext rootContext, IdentifiableListActionModel selectionsActionModel) {
        this.rootContext = rootContext;
        this.selectionsActionModel = selectionsActionModel;
    }

    public SelectorPopupMenuPopulator getPopupMenuPopulator() {
        return popupMenuPopulator;
    }

    public void setPopupMenuPopulator(SelectorPopupMenuPopulator selectorPopupMenuPopulator) {
        this.popupMenuPopulator = selectorPopupMenuPopulator;
    }

    public void showSelections(List<Identifiable> selected) {
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

    public static <E> List<E> getAffectedSeries(Class seriesClass, IdentifiableTreeEvent contextTreeEvent, boolean recursive) {
        LinkedList<E> l = new LinkedList<E>();
        for ( Identifiable i : contextTreeEvent.getNodes()) {
            addToList(seriesClass, l, i, recursive);
        }
        return l;
    }

    private static <E> void addToList(Class seriesClass, LinkedList<E> l, Identifiable i, boolean recursive) {
        if ( recursive) {
            for (Identifiable child : i.getChildren()) {
                addToList(seriesClass, l, child, recursive);
            }
        }
        if ( seriesClass.isAssignableFrom(i.getClass())) {
            l.add((E)i);
        }
    }

    protected LinkedHashSet<Identifiable> convertToIdentifiableInThisContext(List<Identifiable> identifiables) {
        LinkedHashSet<Identifiable> idInThisContext = new LinkedHashSet<Identifiable>();
        for ( Identifiable i : identifiables) {
            Identifiable inThisContext = rootContext.get(i.getPath());
            if ( inThisContext != null) {
                idInThisContext.add(inThisContext);
            }
        }
        return idInThisContext;
    }

    public void refreshActions() {
        //make sure all actions are updated to reflect the latest state of
        //any selected items, e.g. the selection has not changed, but since then
        //a visualizer has been hidden
        selectionsActionModel.fireActionStateUpdated();
    }
}
