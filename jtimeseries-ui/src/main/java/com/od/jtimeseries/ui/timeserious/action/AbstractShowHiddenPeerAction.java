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

import com.od.jtimeseries.ui.identifiable.HidablePeerContext;
import com.od.jtimeseries.ui.uicontext.IdentifiableListActionModel;
import com.od.swing.action.ModelDrivenAction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 28/04/11
 * Time: 15:57
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractShowHiddenPeerAction extends ModelDrivenAction<IdentifiableListActionModel> {

    private Class<? extends HidablePeerContext> hideableClass;

    public AbstractShowHiddenPeerAction(IdentifiableListActionModel actionModel, String name, ImageIcon imageIcon, Class<? extends HidablePeerContext> hideableClass) {
        super(actionModel, name, imageIcon);
        this.hideableClass = hideableClass;
    }

    public boolean isModelStateActionable() {
        boolean result = getActionModel().isSelectionLimitedToTypes(hideableClass);
        if ( result ) {
            List<? extends HidablePeerContext> nodes = getActionModel().getSelected(hideableClass);
            for ( final HidablePeerContext n : nodes ) {
                result &= n.isHidden();
            }
        }
        return result;
    }

    public void actionPerformed(ActionEvent e) {
        List<? extends HidablePeerContext> nodes = getActionModel().getSelected(hideableClass);
        for ( final HidablePeerContext n : nodes ) {
            if ( n.isHidden() ) {
                n.setShown(true);
            }
        }
    }

}
