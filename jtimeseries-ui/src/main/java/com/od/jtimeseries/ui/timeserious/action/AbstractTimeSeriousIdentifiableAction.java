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

import com.od.jtimeseries.ui.config.DesktopConfiguration;
import com.od.jtimeseries.ui.identifiable.DesktopContext;
import com.od.jtimeseries.ui.uicontext.IdentifiableListActionModel;
import com.od.swing.action.ModelDrivenAction;

import javax.swing.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 11/05/11
 * Time: 12:52
 */
public abstract class AbstractTimeSeriousIdentifiableAction extends ModelDrivenAction<IdentifiableListActionModel> {

    public AbstractTimeSeriousIdentifiableAction(IdentifiableListActionModel actionModel, String name, ImageIcon imageIcon) {
        super(actionModel, name, imageIcon);
    }

    protected boolean isMainDesktopSelected() {
        List<DesktopContext> nodes = getActionModel().getSelected(DesktopContext.class);
        boolean result = false;
        for ( final DesktopContext n : nodes ) {
            if ( n.getId().equals(DesktopConfiguration.MAIN_DESKTOP_NAME)) {
                result = true;
                break;
            }
        }
        return result;
    }
}
