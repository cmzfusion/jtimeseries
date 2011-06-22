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
package com.od.jtimeseries.ui.util;

import com.jidesoft.wizard.WizardDialog;
import com.od.swing.progress.ProgressLayeredPane;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 15-Dec-2010
 * Time: 21:20:24
 * To change this template use File | Settings | File Templates.
 */
public class ProgressIndicatorWizard extends WizardDialog {
    private boolean progressPaneAdded;

    public ProgressIndicatorWizard(Frame owner, String title) throws HeadlessException {
        super(owner, title);
    }

    public void addProgressPane(float alphaTransparency, int animationIconSize, int fontSize) {
        if ( ! progressPaneAdded ) {
            Component c = ProgressIndicatorWizard.this.getContentPane().getComponent(0);
            ProgressIndicatorWizard.this.getContentPane().remove(c);
            ProgressLayeredPane progressPane = new ProgressLayeredPane(c, alphaTransparency, animationIconSize, fontSize);
            ProgressIndicatorWizard.this.getContentPane().add(progressPane);
            progressPaneAdded = true;
        }
    }
}
