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
import com.od.jtimeseries.identifiable.Identifiable;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.uicontext.IdentifiableListActionModel;

import javax.swing.*;
import java.util.LinkedHashSet;
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
    private SelectorActionFactory actionFactory = new SelectorActionFactory() {
        public void addMenuItems(JPopupMenu menu, SelectorComponent s, List<Identifiable> selectedIdentifiable) {
        }

        public Action getDefaultAction(Identifiable selectedIdentifiable) {
            return null;
        }
    };

    public SelectorComponent(TimeSeriesContext rootContext, IdentifiableListActionModel selectionsActionModel) {
        this.rootContext = rootContext;
        this.selectionsActionModel = selectionsActionModel;
    }

    public SelectorActionFactory getActionFactory() {
        return actionFactory;
    }

    public void setActionFactory(SelectorActionFactory selectorActionFactory) {
        this.actionFactory = selectorActionFactory;
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
