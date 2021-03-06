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

import com.od.jtimeseries.identifiable.Identifiable;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.util.Displayable;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

/**
* Created by IntelliJ IDEA.
* User: nick
* Date: 20/12/10
* <p/>
* To change this template use File | Settings | File Templates.
*/
public class SeriesTreeCellRenderer extends JPanel implements TreeCellRenderer {

    DefaultTreeCellRenderer delegateRenderer = new DefaultTreeCellRenderer();
    private JCheckBox seriesSelectionCheckbox = new JCheckBox();
    private boolean seriesSelectionEnabled = true;

    public SeriesTreeCellRenderer() {
        setLayout(new BorderLayout());
        add(delegateRenderer, BorderLayout.CENTER);
        seriesSelectionCheckbox.setOpaque(false);
        setOpaque(false);
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        delegateRenderer.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if ( value instanceof AbstractIdentifiableTreeNode) {
            delegateRenderer.setIcon(((AbstractIdentifiableTreeNode) value).getIcon());
            removeAll();
            delegateRenderer.setText(getDisplayName(((AbstractIdentifiableTreeNode) value).getIdentifiable()));
            if ( value instanceof SeriesTreeNode ) {
                SeriesTreeNode seriesNode = (SeriesTreeNode)value;
                Object timeSeries = seriesNode.getTimeSeries();
                if ( timeSeries instanceof UIPropertiesTimeSeries) {
                    seriesSelectionCheckbox.setSelected(((UIPropertiesTimeSeries)timeSeries).isSelected());
                }
                if ( seriesSelectionEnabled ) {
                    add(seriesSelectionCheckbox, BorderLayout.WEST);
                }
                add(delegateRenderer, BorderLayout.CENTER);
            } else {
                delegateRenderer.setText(getDisplayName(((AbstractIdentifiableTreeNode)value).getIdentifiable()));
                add(delegateRenderer, BorderLayout.CENTER);
            }
            return this;
        } else {
            return delegateRenderer;
        }
    }

    //get the display name for an identifiable in the context tree
    static String getDisplayName(Identifiable i) {
        if ( i instanceof Displayable) {
            return ((Displayable)i).getDisplayName();
        } else {
            return i.getId();
        }
    }

    public void setSeriesSelectionEnabled(boolean enabled) {
        seriesSelectionEnabled = enabled;
    }
}
