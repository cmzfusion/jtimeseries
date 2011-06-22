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
package com.od.jtimeseries.ui.selector.tree;

import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
* Created by IntelliJ IDEA.
* User: nick
* Date: 20/12/10
* <p/>
* To change this template use File | Settings | File Templates.
*/
class SeriesSelectionMouseListener<E extends UIPropertiesTimeSeries> extends MouseAdapter {

    private  int hotspot = new JCheckBox().getPreferredSize().width;
    private JTree tree;

    public SeriesSelectionMouseListener(JTree tree) {
        this.tree = tree;
    }

    public void mouseClicked(MouseEvent me){
        TreePath path = tree.getPathForLocation(me.getX(), me.getY());
        if(path==null)
            return;
        if(me.getX()>tree.getPathBounds(path).x+hotspot)
            return;

        Object o = path.getLastPathComponent();
        if ( o instanceof SeriesTreeNode) {
            E m = ((SeriesTreeNode<E>)o).getTimeSeries();
            if ( m instanceof UIPropertiesTimeSeries) {
                UIPropertiesTimeSeries s = (UIPropertiesTimeSeries)m;
                s.setSelected(!s.isSelected());
            }
            tree.repaint();
        }
    }
}
