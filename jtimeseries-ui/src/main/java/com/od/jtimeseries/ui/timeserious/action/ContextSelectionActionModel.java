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
package com.od.jtimeseries.ui.timeserious.action;

import com.od.swing.action.AbstractActionModel;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 29/04/11
 * Time: 09:22
 * To change this template use File | Settings | File Templates.
 */
public class ContextSelectionActionModel<E> extends AbstractActionModel {
    private E selectedContext;

    public E getSelectedContext() {
        return selectedContext;
    }

    public void setSelectedContext(E selectedContext) {
        if ( this.selectedContext != selectedContext) {
            this.selectedContext = selectedContext;
            setModelValid(selectedContext != null);
            fireActionStateUpdated();
        }
    }

    protected void doClearActionModelState() {
        selectedContext = null;
    }

    public boolean isContextSelected() {
        return selectedContext != null;
    }
}
