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

import com.od.jtimeseries.ui.identifiable.DesktopContext;
import com.od.jtimeseries.ui.uicontext.IdentifiableListActionModel;
import com.od.jtimeseries.ui.util.ImageUtils;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 28/04/11
 * Time: 15:55
 * To change this template use File | Settings | File Templates.
 */
public class ShowHidableDesktopAction extends AbstractShowHidablePeerAction<DesktopContext> {

    public ShowHidableDesktopAction(IdentifiableListActionModel actionModel) {
        super(actionModel, "Show Desktop", ImageUtils.DESKTOP_SHOW_16x16, DesktopContext.class);
        setShortDescription();
    }

    public ShowHidableDesktopAction(DesktopContext c) {
        super(c, "Show Desktop", ImageUtils.DESKTOP_SHOW_16x16, DesktopContext.class);
        setShortDescription();
    }

    private void setShortDescription() {
        super.putValue(SHORT_DESCRIPTION, "Restore the selected desktop");
    }
}
