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

import com.od.jtimeseries.ui.util.ImageUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 25/05/11
* Time: 19:15
*/
class ShowAboutDialogAction extends AbstractAction {

    private JFrame ownerFrame;
    private long version;{ putValue(NAME, "About"); }

    public ShowAboutDialogAction(JFrame ownerFrame, long version) {
        this.ownerFrame = ownerFrame;
        this.version = version;
    }

    public void actionPerformed(ActionEvent e) {
        AboutDialog d = new AboutDialog(
            ownerFrame,
            "TimeSerious",
            "version: " + version,
            "JTimeseries Project htp://www.jtimeseries.com",
            "(c) Object Definitions Ltd. LGPL",
            ImageUtils.SPLASH_SCREEN.getImage()
        );
        d.pack();
        d.setLocationRelativeTo(null);
        d.setVisible(true);
    }
}
