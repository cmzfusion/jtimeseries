package com.od.jtimeseries.ui.selector.shared;

import com.od.jtimeseries.util.identifiable.Identifiable;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

            menu.removeAll();
            addMenuItems(menu, e);
            if (menu.getComponentCount() > 0) {
                showMenu(e);
            }
        }
    }

    private void showMenu(MouseEvent e) {
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

    private void addMenuItems(JPopupMenu menu, MouseEvent e) {
        List<Identifiable> selectedIdentifiable = getSelectedIdentifiable(e);
        selectorComponent.getPopupMenuPopulator().addMenuItems(
            menu,
            getSelectorComponent(),
            selectedIdentifiable
        );
    }

    protected abstract List<Identifiable> getSelectedIdentifiable(MouseEvent mouseEvent);

    protected abstract SelectorComponent getSelectorComponent();
}
