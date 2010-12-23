package com.od.jtimeseries.ui.selector.tree;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
* Created by IntelliJ IDEA.
* User: nick
* Date: 20/12/10
* <p/>
* To change this template use File | Settings | File Templates.
*/
class PopupMenuMouseListener extends MouseAdapter {

    private JTree tree;
    private List<Action> seriesActions;

    public PopupMenuMouseListener(JTree tree, List<Action> seriesActions) {
        this.tree = tree;
        this.seriesActions = seriesActions;
    }

    public void mousePressed(MouseEvent e) {
        showMenuIfPopupTrigger(e);
    }

    public void mouseClicked(MouseEvent e) {
        showMenuIfPopupTrigger(e);

    }

    public void mouseReleased(MouseEvent e) {
        showMenuIfPopupTrigger(e);
    }

    private void showMenuIfPopupTrigger(MouseEvent e) {
        if (e.isPopupTrigger()) {
            List<JMenuItem> menuItems = getMenuItems(e);
            if ( menuItems != null) {
                JPopupMenu menu = new JPopupMenu();
                for ( JMenuItem i : menuItems) {
                    menu.add(i);
                }
                menu.show(tree, e.getX() + 3, e.getY() + 3);
            }
        }
    }

    private List<JMenuItem> getMenuItems(MouseEvent e) {
        TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
        List<JMenuItem> menuItems = new ArrayList<JMenuItem>();
        if (selPath != null ) {
            Object selectedNode = selPath.getLastPathComponent();
            if ( selectedNode instanceof SeriesTreeNode) {
                for ( Action a : seriesActions) {
                    menuItems.add(new JMenuItem(a));
                }
            }
        }
        return menuItems;
    }
}
