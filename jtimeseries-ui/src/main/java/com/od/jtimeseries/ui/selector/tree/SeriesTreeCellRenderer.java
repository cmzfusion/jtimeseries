package com.od.jtimeseries.ui.selector.tree;

import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.util.Displayable;
import com.od.jtimeseries.util.identifiable.Identifiable;

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
    private boolean seriesSelectionEnabled;

    public SeriesTreeCellRenderer() {
        setLayout(new BorderLayout());
        add(delegateRenderer, BorderLayout.CENTER);
        seriesSelectionCheckbox.setOpaque(false);
        setOpaque(false);
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        delegateRenderer.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if ( value instanceof AbstractSeriesSelectionTreeNode) {
            delegateRenderer.setIcon(((AbstractSeriesSelectionTreeNode) value).getIcon());
            removeAll();
            delegateRenderer.setText(getDisplayName(((AbstractSeriesSelectionTreeNode) value).getIdentifiable()));
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
                delegateRenderer.setText(getDisplayName(((ContextTreeNode) value).getContext()));
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
