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
package com.od.jtimeseries.ui.uicontext;

import com.od.jtimeseries.util.identifiable.Identifiable;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 01/05/11
 * Time: 16:43
 * To change this template use File | Settings | File Templates.
 */
public class LocalSelectionsTransferData {

    private IdentifiableListActionModel selections;
    private TransferListener transferListener;
    private int action;

    public LocalSelectionsTransferData(IdentifiableListActionModel selections, TransferListener transferListener) {
        this.selections = selections;
        this.transferListener = transferListener;
    }

    public boolean isSelectionLimitedToType(Class c) {
        return selections.isSelectionLimitedToTypes(c);
    }

    public <C extends Identifiable> List<C> getSelected(Class<C> clazz) {
        return selections.getSelected(clazz);
    }

    public IdentifiableListActionModel getSelections() {
        return selections;
    }

    public TransferListener getTransferListener() {
        return transferListener;
    }

    public static interface TransferListener {
        void transferComplete(LocalSelectionsTransferData d, int actionType);
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }
}
