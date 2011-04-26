package com.od.jtimeseries.ui.selector.shared;

import com.od.jtimeseries.util.identifiable.Identifiable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 20/12/10
 * <p/>
 * <p/>
 * Change the selection when mouse is right clicked, and pop up a popup menu
 * <p/>
 * (By default, Swing does not change the selection on right click, which conflicts with expected behaviour on
 * most platforms)
 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4196497
 */
public abstract class RightClickSelectionPopupListener extends MouseAdapter {

    private SelectorComponent selectorComponent;
    private JComponent eventSource;
    private JPopupMenu menu = new JPopupMenu();

    //Set the magic property which makes swing popup menus seem professional
    //the effect of this is to make the first click outside the popup
    //capable of selecting nodes in the tree/table, as opposed to
    //simply dismissing the popup.
    //This avoids the need to click once to dismiss the popup
    //and then click again to select, which is more than a trifle aggravating
    static {
        UIManager.put("PopupMenu.consumeEventOnClose", Boolean.FALSE);
    }

    public RightClickSelectionPopupListener(SelectorComponent selectorComponent, JComponent eventSource) {
        this.selectorComponent = selectorComponent;
        this.eventSource = eventSource;
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

    private void showMenuIfPopupTrigger(final MouseEvent e) {
        if (e.isPopupTrigger()) {
            setSelectedItemsOnPopupTrigger(e);

            selectorComponent.refreshActions();

            List<JMenuItem> menuItems = getMenuItems(e);
            if (menuItems != null) {
                showMenu(e, menuItems);
            }
        }
    }

    private void showMenu(MouseEvent e, List<JMenuItem> menuItems) {
        menu.removeAll();
        for (JMenuItem i : menuItems) {
            menu.add(i);
        }
        menu.show(eventSource, e.getX() + 3, e.getY() + 3);
    }

    /**
     * The popup trigger / right mouse button has been clicked - subclass should implement the following with matches
     * windows explorer behaviour:
     * If the item under the click is not already selected, clear the current selections and select the
     * item, prior to showing the popup.
     * If the item under the click is already selected, keep the current selections and show the popup
     */
    protected abstract void setSelectedItemsOnPopupTrigger(MouseEvent e);

    private List<JMenuItem> getMenuItems(MouseEvent e) {
        List<JMenuItem> menuItems = new ArrayList<JMenuItem>();
        List<Identifiable> selectedIdentifiable = getSelectedIdentifiable(e);
        List<Action> actions = selectorComponent.getSelectorActionFactory().getActions(
                getSelectorComponent(),
                selectedIdentifiable
        );
        for (Action a : actions) {
            menuItems.add(new JMenuItem(a));
        }
        return menuItems;
    }

    protected abstract List<Identifiable> getSelectedIdentifiable(MouseEvent mouseEvent);

    protected abstract SelectorComponent getSelectorComponent();
}
