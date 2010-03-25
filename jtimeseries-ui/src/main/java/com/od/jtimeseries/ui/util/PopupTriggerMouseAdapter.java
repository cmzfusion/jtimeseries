/**
 * Copyright (C) 2009 (nick @ objectdefinitions.com)
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

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 17-Nov-2009
* Time: 18:19:20
*/
public class PopupTriggerMouseAdapter extends MouseAdapter {

    private JPopupMenu popupMenu;
    private Component triggerComponent;

    public PopupTriggerMouseAdapter(JPopupMenu popupMenu, Component triggerComponent) {
        this.popupMenu = popupMenu;
        this.triggerComponent = triggerComponent;
    }

    public void mouseClicked(MouseEvent e) {
        processPopupEvent(e);
    }

    public void mousePressed(MouseEvent e) {
        processPopupEvent(e);
    }

    public void mouseReleased(MouseEvent e) {
        processPopupEvent(e);
    }

    private void processPopupEvent(MouseEvent e) {
        if ( e.isPopupTrigger()) {
            popupMenu.show(triggerComponent, e.getX(), e.getY());
        }
    }
}
