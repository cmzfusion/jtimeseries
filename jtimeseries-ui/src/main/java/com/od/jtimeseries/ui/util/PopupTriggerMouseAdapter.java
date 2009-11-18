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
