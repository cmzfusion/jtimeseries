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
package com.od.jtimeseries.ui.timeserious.frame;

import com.od.jtimeseries.ui.identifiable.DesktopContext;
import com.od.jtimeseries.ui.uicontext.AbstractUIRootContext;
import com.od.jtimeseries.ui.uicontext.IdentifiableListActionModel;
import com.od.jtimeseries.ui.uicontext.ImportExportTransferHandler;
import com.od.jtimeseries.util.identifiable.Identifiable;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 21/03/11
 * Time: 17:08
 */
public class DesktopPaneTransferHandler extends ImportExportTransferHandler {

    private DesktopContext desktopContext;

    public DesktopPaneTransferHandler(AbstractUIRootContext rootContext, DesktopContext desktopContext, JDesktopPane desktopPane) {
        super(rootContext, new IdentifiableListActionModel());
        this.desktopContext = desktopContext;
    }

    protected Identifiable getTargetIdentifiableForDropOrPaste(TransferSupport supp) {
        return desktopContext;
    }
}
